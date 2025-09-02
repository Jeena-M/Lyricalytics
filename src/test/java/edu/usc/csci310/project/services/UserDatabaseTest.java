package edu.usc.csci310.project.services;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserDatabaseTest {

    @Test
    void initializeDatabaseSuccess() throws SQLException {
        Connection connection = mock();
        Statement statement = mock();

        when(connection.createStatement()).thenReturn(statement);

        String createTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL, " +
                "password TEXT NOT NULL";

        when(statement.executeUpdate(createTableSQL)).thenReturn(1);

        UserDatabase initializer = new UserDatabase(connection);
        assertDoesNotThrow(initializer::initializeDatabase);
    }

    @Test
    void initializeDatabaseFailure() throws SQLException {
        Connection connection = mock();
        Statement statement = mock();

        when(connection.createStatement()).thenThrow(new SQLException("ERROR!"));

        UserDatabase initializer = new UserDatabase(connection);
        assertThrows(RuntimeException.class, () -> initializer.initializeDatabase());
    }
}