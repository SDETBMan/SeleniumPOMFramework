package com.framework.utils;

import java.sql.*;

// TODO: Integrate with User Management DB once environment is available

/**
 * DatabaseUtils: Provides methods for interacting with SQL databases.
 * Enables Backend-to-Frontend data validation and dynamic test data sourcing.
 */
public class DatabaseUtils {

    /**
     * Establishes a connection to the database using credentials from ConfigReader.
     */
    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(
                    ConfigReader.getProperty("db.url"),
                    ConfigReader.getProperty("db.user"),
                    ConfigReader.getProperty("db.password")
            );
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to connect to Database: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Fetches a single active username from the database.
     * Demonstrates dynamic data sourcing for test scripts.
     */
    public static String getValidUser() {
        String username = null;
        String query = "SELECT username FROM users WHERE is_active = 1 LIMIT 1";

        // Using try-with-resources to ensure Connection, Statement, and ResultSet are closed automatically
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                username = rs.getString("username");
                System.out.println("[INFO] Dynamic Test Data Acquired: " + username);
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Database Query Failed: " + e.getMessage());
        }
        return (username != null) ? username : "fallback_user";
    }
}
