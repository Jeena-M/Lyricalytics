package edu.usc.csci310.project.services;

import edu.usc.csci310.project.models.User;
import edu.usc.csci310.project.requests.CreateAccountRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.*;

@Service
public class UserService {
    private final Connection connection;
    private final PasswordEncoder passwordEncoder;

    public UserService(Connection connection) {
        this.connection = connection;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public int createAccount(CreateAccountRequest request) throws SQLException {
        // Encrypt the password before storing it
        String encryptedPassword = passwordEncoder.encode(request.getPassword());

        String sql = "INSERT INTO users (username, password, privacy) VALUES (?, ?, 1)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Set parameters for the insert statement
            stmt.setString(1, request.getUsername());
            stmt.setString(2, encryptedPassword);  // Store the encrypted password

            // Execute the insert statement
            int rowsAffected = stmt.executeUpdate();

            // Check if insert was successful
            if (rowsAffected > 0) {
                // SQLite specific: Retrieve the last inserted row ID
                try (Statement statement = connection.createStatement();
                     ResultSet rs = statement.executeQuery("SELECT last_insert_rowid()")) {
                    if (rs.next()) {
                        return rs.getInt(1); // Return the generated ID
                    } else {
                        throw new SQLException("Failed to retrieve the generated ID.");
                    }
                }
            } else {
                throw new SQLException("No rows affected during the insert.");
            }
        } catch (SQLException e) {
            // if it's our duplicate‐username error, rethrow with the exact text
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                throw new SQLException("Username already exists");
            }
            // otherwise let it bubble as a 500
            throw e;
        }
    }

    public boolean doesUsernameExist(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username); // Set the ID parameter in the query

            try (ResultSet rs = stmt.executeQuery()) {
                // If a user with the given username is found, return true
                return rs.next();
            }
        }
    }


    public boolean correctUsernameAndPassword(String username, String password) throws SQLException {
        String sql = "SELECT password FROM users WHERE username = ?";
        boolean authenticated = false;            // ← result moved outside the inner try
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {                  // user found
                    String stored = rs.getString("password");
                    if (stored != null &&
                            (stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$"))) {
                        authenticated = passwordEncoder.matches(password, stored);
                    } else {
                        authenticated = password.equals(stored);  // plain-text fallback
                    }
                }
            }
        }
        return authenticated;                     // ← falls through the two ‘} }’ lines
    }


    public User getUser(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("id"), rs.getString("username"));
                } else {
                    throw new SQLException("User with username " + username + " not found.");
                }
            }
        }
    }

    public boolean isAccountLocked(String username) throws SQLException {
        boolean locked = false;
        String sql = "SELECT locked_until FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Timestamp lockedUntil = rs.getTimestamp("locked_until");
                    if (lockedUntil != null && lockedUntil.after(new Timestamp(System.currentTimeMillis()))) {
                        locked = true;
                    }
                }
            }
        }
        return locked;
    }


    public boolean recordFailedAttempt(String username) throws SQLException {
        String sql = "SELECT failed_attempts, last_failed_attempt FROM users WHERE username = ?";
        int failedAttempts = 0;
        Timestamp lastFailed = null;
        Timestamp now = new Timestamp(System.currentTimeMillis());

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    failedAttempts = rs.getInt("failed_attempts");
                    lastFailed = rs.getTimestamp("last_failed_attempt");
                }
            }
        }

        if (lastFailed == null || (now.getTime() - lastFailed.getTime()) > 60000) {
            failedAttempts = 1;
        } else {
            failedAttempts++;
        }

        if (failedAttempts >= 3) {
            lockAccount(username);
            return true;
        } else {
            String updateSql = "UPDATE users SET failed_attempts = ?, last_failed_attempt = ? WHERE username = ?";
            try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                updateStmt.setInt(1, failedAttempts);
                updateStmt.setTimestamp(2, now);
                updateStmt.setString(3, username);
                updateStmt.executeUpdate();
            }
            return false;
        }
    }

    public void resetFailedAttempts(String username) throws SQLException {
        String sql = "UPDATE users SET failed_attempts = 0, last_failed_attempt = NULL, locked_until = NULL WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        }
    }

    public void lockAccount(String username) throws SQLException {
        //change lockout time based on RE
        Timestamp lockedUntil = new Timestamp(System.currentTimeMillis() + 30 * 1000);
        String sql = "UPDATE users SET locked_until = ?, failed_attempts = 0, last_failed_attempt = NULL WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setTimestamp(1, lockedUntil);
            stmt.setString(2, username);
            stmt.executeUpdate();
        }
    }
}

