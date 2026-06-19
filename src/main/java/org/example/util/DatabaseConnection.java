package org.example.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Database credentials and configuration URL
    private static final String URL = "jdbc:mysql://localhost:3306/WholesaleInventoryDB";
    private static final String USER = "root";
    private static final String PASSWORD = "motassimbilla~~~" +""; // <-- If you set a different password during MySQL setup, type it here!

    private static Connection connection = null;

    // Static method to get a single, shared connection instance
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Load the MySQL JDBC Driver class explicitly
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("⚡ Database Connection successfully established!");
            } catch (ClassNotFoundException e) {
                System.err.println("❌ MySQL JDBC Driver not found. Check your pom.xml dependency!");
                e.printStackTrace();
            }
        }
        return connection;
    }

    // Method to close the connection safely when the app shuts down
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("🔌 Database connection closed cleanly.");
            } catch (SQLException e) {
                System.err.println("❌ Failed to close database connection.");
                e.printStackTrace();
            }
        }
    }
}