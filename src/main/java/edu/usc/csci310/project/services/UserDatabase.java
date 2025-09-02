package edu.usc.csci310.project.services;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Service
public class UserDatabase {

    private final Connection connection;

    @Autowired
    public UserDatabase(Connection connection) {
        this.connection = connection;
    }

    @PostConstruct
    public void initializeDatabase() {
        try (Statement stmt = connection.createStatement()) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT NOT NULL, " +
                    "password TEXT NOT NULL, " +
                    "failed_attempts INTEGER DEFAULT 0, " +
                    "last_failed_attempt TIMESTAMP, " +
                    "locked_until TIMESTAMP," +
                    "privacy BOOLEAN DEFAULT 1" +
                    ");";
            stmt.executeUpdate(createTableSQL);
            System.out.println("Table users created");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error initializing the database schema", e);
        }
    }
}
