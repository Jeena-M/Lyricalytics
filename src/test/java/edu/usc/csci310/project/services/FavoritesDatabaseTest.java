package edu.usc.csci310.project.services;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FavoritesDatabaseTest {

    @Test
    void initializeDatabaseSuccess() throws SQLException {
        Connection connection = mock();
        Statement statement = mock();

        when(connection.createStatement()).thenReturn(statement);

        String createTableSQL = "CREATE TABLE IF NOT EXISTS favorites (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL, " +
                "song TEXT NOT NULL, " +
                "artist TEXT NOT NULL, " +
                "FOREIGN KEY (username) REFERENCES users(username)" +
                ");";

        when(statement.executeUpdate(createTableSQL)).thenReturn(1);

        FavoritesDatabase initializer = new FavoritesDatabase(connection);
        assertDoesNotThrow(initializer::initializeDatabase);
    }

    @Test
    void initializeDatabaseFailure() throws SQLException {
        Connection connection = mock();
        Statement statement = mock();

        when(connection.createStatement()).thenThrow(new SQLException("ERROR!"));

        FavoritesDatabase initializer = new FavoritesDatabase(connection);
        assertThrows(RuntimeException.class, () -> initializer.initializeDatabase());
    }
}
