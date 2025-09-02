package edu.usc.csci310.project.configuration;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class UserDatabaseConfigTest {

    @Test
    void sqliteConnection() throws SQLException {
        String DATABASE_URL = "jdbc:sqlite:userDatabase.db";
        Connection connection = mock();

        // Mock the Static Class as "driverManager"
        try (MockedStatic<DriverManager> driverManager = Mockito.mockStatic(DriverManager.class)) {
            // Mock its static method behaviors
            driverManager.when(() -> DriverManager.getConnection(DATABASE_URL)).thenReturn(connection);

            UserDatabaseConfig databaseConfig = new UserDatabaseConfig();
            Connection actualConnection = databaseConfig.sqliteConnection();

            assertEquals(connection, actualConnection);
        }
    }
}