package edu.usc.csci310.project.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
public class UserDatabaseConfig {
    private static final String DATABASE_URL = "jdbc:sqlite:userDatabase.db";

    @Bean
    public Connection sqliteConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL);
    }
}
