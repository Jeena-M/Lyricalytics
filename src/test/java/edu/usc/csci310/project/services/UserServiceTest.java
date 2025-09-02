package edu.usc.csci310.project.services;

import edu.usc.csci310.project.models.User;
import edu.usc.csci310.project.requests.CreateAccountRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private Connection connection;
    private PreparedStatement preparedStatement;
    private PreparedStatement updateStatement;
    private ResultSet rs;
    private Statement statement;
    private UserService userService;

    @BeforeEach
    void setUp() {
        connection = mock(Connection.class);
        preparedStatement = mock(PreparedStatement.class);
        updateStatement = mock(PreparedStatement.class);
        rs = mock(ResultSet.class);
        statement = mock(Statement.class);
        userService = new UserService(connection);
    }

    @Test
    void createAccountThrowsUsernameAlreadyExistsOnUniqueConstraint() throws SQLException {
        String sql = "INSERT INTO users (username, password, privacy) VALUES (?, ?, 1)";
        when(connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
                .thenReturn(preparedStatement);

        // simulate duplicate‐key error
        CreateAccountRequest req = new CreateAccountRequest();
        req.setUsername("Alice");
        req.setPassword("Secret");
        req.setConfirmPassword("Secret");
        when(preparedStatement.executeUpdate())
                .thenThrow(new SQLException("UNIQUE constraint failed: users.username"));

        SQLException ex = assertThrows(
                SQLException.class,
                () -> userService.createAccount(req)
        );
        assertEquals("Username already exists", ex.getMessage());
    }

    @Test
    void createAccountThrowsGenericSQLExceptionOnInsertError() throws SQLException {
        String sql = "INSERT INTO users (username, password, privacy) VALUES (?, ?, 1)";
        when(connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
                .thenReturn(preparedStatement);

        // simulate some other SQL error
        CreateAccountRequest req = new CreateAccountRequest();
        req.setUsername("Bob");
        req.setPassword("Pass");
        req.setConfirmPassword("Pass");
        when(preparedStatement.executeUpdate())
                .thenThrow(new SQLException("Some DB error"));

        SQLException ex = assertThrows(
                SQLException.class,
                () -> userService.createAccount(req)
        );
        assertEquals("Some DB error", ex.getMessage());
    }

    @Test
    void createAccountThrowsWhenLastInsertRowIdQueryFails() throws SQLException {
        String sql = "INSERT INTO users (username, password, privacy) VALUES (?, ?, 1)";
        when(connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
                .thenReturn(preparedStatement);

        CreateAccountRequest req = new CreateAccountRequest();
        req.setUsername("Charlie");
        req.setPassword("Secret1");
        req.setConfirmPassword("Secret1");

        // simulate successful insert
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(connection.createStatement()).thenReturn(statement);
        // but fail when reading back last_insert_rowid()
        when(statement.executeQuery("SELECT last_insert_rowid()"))
                .thenThrow(new SQLException("fail to retrieve id"));

        SQLException ex = assertThrows(
                SQLException.class,
                () -> userService.createAccount(req)
        );
        assertEquals("fail to retrieve id", ex.getMessage());
    }

    @Test
    void createAccountEncryptsPasswordBeforeStorage() throws SQLException {
        String sql = "INSERT INTO users (username, password, privacy) VALUES (?, ?, 1)";
        when(connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
                .thenReturn(preparedStatement);

        CreateAccountRequest req = new CreateAccountRequest();
        req.setUsername("Dave");
        req.setPassword("MyPwd");
        req.setConfirmPassword("MyPwd");

        // simulate full success path
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery("SELECT last_insert_rowid()")).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt(1)).thenReturn(42);

        int id = userService.createAccount(req);
        assertEquals(42, id);

        // verify the username and *an encrypted* password got set
        verify(preparedStatement).setString(eq(1), eq("Dave"));
        verify(preparedStatement).setString(eq(2),
                argThat(pw -> pw.startsWith("$2a$") || pw.startsWith("$2b$") || pw.startsWith("$2y$"))
        );
    }


    @Test
    void doesUsernameExist() throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);

        assertTrue(userService.doesUsernameExist("Jeena"));
    }


    @Test
    void getUser() throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt("id")).thenReturn(2);
        when(rs.getString("username")).thenReturn("Zoe");

        User u = userService.getUser("Zoe");
        assertEquals("Zoe", u.getUsername());
        assertEquals(2, u.getId());
    }

    @Test
    void getUserFails() throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        assertThrows(SQLException.class, () -> userService.getUser("Zoe1"));
    }

    @Test
    void isAccountLockedReturnsTrue() throws SQLException {
        String sql = "SELECT locked_until FROM users WHERE username = ?";
        when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(rs);

        Timestamp futureTimestamp = new Timestamp(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1));
        when(rs.next()).thenReturn(true);
        when(rs.getTimestamp("locked_until")).thenReturn(futureTimestamp);

        assertTrue(userService.isAccountLocked("user1"));
    }

    @Test
    void isAccountLockedReturnsFalseWhenNull() throws SQLException {
        String sql = "SELECT locked_until FROM users WHERE username = ?";
        when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true);
        when(rs.getTimestamp("locked_until")).thenReturn(null);

        assertFalse(userService.isAccountLocked("user1"));
    }

    @Test
    void isAccountLockedReturnsFalseWhenExpired() throws SQLException {
        String sql = "SELECT locked_until FROM users WHERE username = ?";
        when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(rs);

        Timestamp pastTimestamp = new Timestamp(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(1));
        when(rs.next()).thenReturn(true);
        when(rs.getTimestamp("locked_until")).thenReturn(pastTimestamp);

        assertFalse(userService.isAccountLocked("user1"));
    }

    @Test
    void isAccountLockedReturnsFalseWhenNoRow() throws SQLException {
        String sql = "SELECT locked_until FROM users WHERE username = ?";
        when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(false);

        assertFalse(userService.isAccountLocked("user1"));
    }

    @Test
    void recordFailedAttemptDoesNotLockAccountWhenAttemptsLessThanThree() throws SQLException {
        String selectSql = "SELECT failed_attempts, last_failed_attempt FROM users WHERE username = ?";
        when(connection.prepareStatement(selectSql)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true);
        when(rs.getInt("failed_attempts")).thenReturn(0);
        when(rs.getTimestamp("last_failed_attempt")).thenReturn(null);

        String updateSql = "UPDATE users SET failed_attempts = ?, last_failed_attempt = ? WHERE username = ?";
        when(connection.prepareStatement(updateSql)).thenReturn(updateStatement);
        when(updateStatement.executeUpdate()).thenReturn(1);

        assertFalse(userService.recordFailedAttempt("user1"));
    }

    @Test
    void recordFailedAttemptWhenNoRecordExists() throws SQLException {
        String selectSql = "SELECT failed_attempts, last_failed_attempt FROM users WHERE username = ?";
        when(connection.prepareStatement(selectSql)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(false);

        String updateSql = "UPDATE users SET failed_attempts = ?, last_failed_attempt = ? WHERE username = ?";
        when(connection.prepareStatement(updateSql)).thenReturn(updateStatement);
        when(updateStatement.executeUpdate()).thenReturn(1);

        assertFalse(userService.recordFailedAttempt("user1"));
    }

    @Test
    void recordFailedAttemptLocksAccountWhenAttemptsReachThree() throws SQLException {
        String selectSql = "SELECT failed_attempts, last_failed_attempt FROM users WHERE username = ?";
        when(connection.prepareStatement(selectSql)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true);
        when(rs.getInt("failed_attempts")).thenReturn(2);
        Timestamp recentTimestamp = new Timestamp(System.currentTimeMillis() - 30000); // 30 seconds ago
        when(rs.getTimestamp("last_failed_attempt")).thenReturn(recentTimestamp);

        String lockSql = "UPDATE users SET locked_until = ?, failed_attempts = 0, last_failed_attempt = NULL WHERE username = ?";
        when(connection.prepareStatement(lockSql)).thenReturn(updateStatement);
        when(updateStatement.executeUpdate()).thenReturn(1);

        assertTrue(userService.recordFailedAttempt("user1"));
    }

    @Test
    void resetFailedAttemptsResetsValues() throws SQLException {
        String sql = "UPDATE users SET failed_attempts = 0, last_failed_attempt = NULL, locked_until = NULL WHERE username = ?";
        when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        assertDoesNotThrow(() -> userService.resetFailedAttempts("user1"));
    }

    @Test
    void lockAccountSetsLockAndResetsFailures() throws SQLException {
        String sql = "UPDATE users SET locked_until = ?, failed_attempts = 0, last_failed_attempt = NULL WHERE username = ?";
        when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        assertDoesNotThrow(() -> userService.lockAccount("user1"));

        verify(preparedStatement).setTimestamp(eq(1), argThat(ts -> ts.after(new Timestamp(System.currentTimeMillis()))));
        verify(preparedStatement).setString(2, "user1");
    }

    @Test
    void recordFailedAttemptResetsOldAttempts() throws SQLException {
        String selectSql = "SELECT failed_attempts, last_failed_attempt FROM users WHERE username = ?";
        when(connection.prepareStatement(selectSql)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true);
        when(rs.getInt("failed_attempts")).thenReturn(2);
        Timestamp oldTimestamp = new Timestamp(System.currentTimeMillis() - 70000);
        when(rs.getTimestamp("last_failed_attempt")).thenReturn(oldTimestamp);

        String updateSql = "UPDATE users SET failed_attempts = ?, last_failed_attempt = ? WHERE username = ?";
        PreparedStatement updateStmt = mock(PreparedStatement.class);
        when(connection.prepareStatement(updateSql)).thenReturn(updateStmt);
        when(updateStmt.executeUpdate()).thenReturn(1);

        assertFalse(userService.recordFailedAttempt("user1"));

        verify(updateStmt).setInt(1, 1);
    }

    @Test
    void isAccountLockedReturnsFalseWhenLockedUntilIsExactlyNow() throws SQLException {
        String sql = "SELECT locked_until FROM users WHERE username = ?";
        when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(rs);

        Timestamp now = new Timestamp(System.currentTimeMillis());
        when(rs.next()).thenReturn(true);
        when(rs.getTimestamp("locked_until")).thenReturn(now);

        assertFalse(userService.isAccountLocked("user1"));
        verify(preparedStatement).close();
        verify(rs).close();
    }

    @Test
    void isAccountLockedThrowsSQLExceptionOnPrepare() throws SQLException {
        String sql = "SELECT locked_until FROM users WHERE username = ?";
        when(connection.prepareStatement(sql)).thenThrow(new SQLException("Test exception"));

        assertThrows(SQLException.class, () -> userService.isAccountLocked("user1"));
    }

    @Test
    void isAccountLockedThrowsSQLExceptionWhilePreparingStatement() throws SQLException {
        String sql = "SELECT locked_until FROM users WHERE username = ?";
        when(connection.prepareStatement(sql)).thenThrow(new SQLException("Mock exception"));

        assertThrows(SQLException.class, () -> userService.isAccountLocked("user1"));
    }

    @Test
    void isAccountLockedThrowsSQLExceptionWhileExecutingQuery() throws SQLException {
        String sql = "SELECT locked_until FROM users WHERE username = ?";
        when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Mock exception"));

        assertThrows(SQLException.class, () -> userService.isAccountLocked("user1"));
    }

    @Test
    void isAccountLockedThrowsSQLExceptionWhenAccessingResultSet() throws SQLException {
        String sql = "SELECT locked_until FROM users WHERE username = ?";
        when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true);
        when(rs.getTimestamp("locked_until")).thenThrow(new SQLException("Mock exception"));

        assertThrows(SQLException.class, () -> userService.isAccountLocked("user1"));
    }

    @Test
    void createAccountFailToRetrieveID() throws SQLException {
        String sql = "INSERT INTO users (username, password, privacy) VALUES (?, ?, 1)";
        when(connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
                .thenReturn(preparedStatement);

        CreateAccountRequest req = new CreateAccountRequest();
        req.setUsername("Alice");
        req.setPassword("Secret");
        req.setConfirmPassword("Secret");
        when(preparedStatement.executeUpdate()).thenReturn(1);
        Statement statement = mock();
        when(connection.createStatement()).thenReturn(statement);
//        ResultSet rs = mock();
        when(statement.executeQuery("SELECT last_insert_rowid()")).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        assertThrows(SQLException.class, () -> userService.createAccount(req));
    }

    @Test
    void createAccountNoRowsAffected() throws SQLException {
        String sql = "INSERT INTO users (username, password, privacy) VALUES (?, ?, 1)";
        when(connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
                .thenReturn(preparedStatement);

        CreateAccountRequest req = new CreateAccountRequest();
        req.setUsername("Alice");
        req.setPassword("Secret");
        req.setConfirmPassword("Secret");
        when(preparedStatement.executeUpdate()).thenReturn(0);

        assertThrows(SQLException.class, () -> userService.createAccount(req));
    }

    @Test
    void correctUserNameAndPasswordBranch1() throws SQLException {
        String sql = "SELECT password FROM users WHERE username = ?";
//        PreparedStatement stmt = mock();
//        Connection connection = mock();
        when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
//        ResultSet rs = mock();
        when(preparedStatement.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getString(any())).thenReturn("$2a$a");
        UserService us = new UserService(connection);
        assertFalse(us.correctUsernameAndPassword("name", "wrongPass"));
    }

    @Test
    void correctUserNameAndPasswordBranch2() throws SQLException {
        String sql = "SELECT password FROM users WHERE username = ?";
//        PreparedStatement stmt = mock();
//        Connection connection = mock();
        when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
//        ResultSet rs = mock();
        when(preparedStatement.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getString(any())).thenReturn("$2b$a");
        UserService us = new UserService(connection);
        assertFalse(us.correctUsernameAndPassword("name", "wrongPass"));
    }

    @Test
    void correctUserNameAndPasswordBranch3() throws SQLException {
        String sql = "SELECT password FROM users WHERE username = ?";
//        PreparedStatement stmt = mock();
//        Connection connection = mock();
        when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
//        ResultSet rs = mock();
        when(preparedStatement.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getString(any())).thenReturn("$2y$a");
        UserService us = new UserService(connection);
        assertFalse(us.correctUsernameAndPassword("name", "wrongPass"));
    }

    @Test
    void correctUserNameAndPasswordBranch4() throws SQLException {
        String sql = "SELECT password FROM users WHERE username = ?";
//        PreparedStatement stmt = mock();
//        Connection connection = mock();
        when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
//        ResultSet rs = mock();
        when(preparedStatement.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getString(any())).thenReturn("$2ab$a");
        UserService us = new UserService(connection);
        assertTrue(us.correctUsernameAndPassword("name", "$2ab$a"));
    }

    @Test
    void correctUserNameAndPasswordNextCheck() throws SQLException {
        String sql = "SELECT password FROM users WHERE username = ?";
//        PreparedStatement stmt = mock();
//        Connection connection = mock();
        when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
//        ResultSet rs = mock();
        when(preparedStatement.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);
        UserService us = new UserService(connection);
        assertFalse(us.correctUsernameAndPassword("name", "wrongPass"));
    }

    @Test
    void returnsFalseWhenResultSetIsEmpty() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);           // no rows

        UserService svc = new UserService(conn);
        assertFalse(svc.correctUsernameAndPassword("ghost", "irrelevant"));
    }

    @Test
    void returnsTrueForMatchingBcrypt() throws Exception {
        BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
        String raw   = "secret";
        String hash  = enc.encode(raw);              // starts with "$2a$..."

        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getString("password")).thenReturn(hash);

        UserService svc = new UserService(conn);
        assertTrue(svc.correctUsernameAndPassword("alice", raw));
    }

    @Test
    void returnsTrueForMatchingPlainText() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getString("password")).thenReturn("plainpass");

        UserService svc = new UserService(conn);
        assertTrue(svc.correctUsernameAndPassword("bob", "plainpass"));
    }

    @Test
    void returnsFalseWhenStoredPasswordIsNull() throws Exception {
        Connection        conn = mock(Connection.class);
        PreparedStatement ps   = mock(PreparedStatement.class);
        ResultSet         rs   = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);          // user found
        when(rs.getString("password")).thenReturn(null);  // but password is NULL

        UserService svc = new UserService(conn);   // uses the normal single-arg constructor
        assertFalse(svc.correctUsernameAndPassword("charlie", "whatever"));
    }


    @Test
    void correctUserNameAndPasswordNextException() throws SQLException {
        String sql = "SELECT password FROM users WHERE username = ?";
//        PreparedStatement stmt = mock();
//        Connection connection = mock();
        when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
//        ResultSet rs = mock();
        when(preparedStatement.executeQuery()).thenReturn(rs);
        when(rs.next()).thenThrow(new SQLException("error"));
        UserService us = new UserService(connection);
        assertThrows(SQLException.class, () -> us.correctUsernameAndPassword("name", "wrongPass"));
    }

    @Test
    void correctUserNameAndPasswordSetStringException() throws SQLException {
        String sql = "SELECT password FROM users WHERE username = ?";
//        PreparedStatement stmt = mock();
//        Connection connection = mock();
        when(connection.prepareStatement(sql)).thenThrow(new SQLException("error"));
//        ResultSet rs = mock();
        when(preparedStatement.executeQuery()).thenReturn(rs);
        when(rs.next()).thenThrow(new SQLException("error"));
        UserService us = new UserService(connection);
        assertThrows(SQLException.class, () -> us.correctUsernameAndPassword("name", "wrongPass"));
    }

    
}
