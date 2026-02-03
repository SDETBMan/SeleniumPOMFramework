package com.framework.utils;

import java.sql.*;

public class DatabaseUtils {

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                ConfigReader.getProperty("db.url"),
                ConfigReader.getProperty("db.user"),
                ConfigReader.getProperty("db.password")
        );
    }

    public static String getValidUser() {
        String username = "";
        String query = "SELECT username FROM users WHERE is_active = 1 LIMIT 1";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                username = rs.getString("username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return username;
    }
}
