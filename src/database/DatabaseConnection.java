// ====================================================================================
// FILE 1: src/database/DatabaseConnection.java
// ====================================================================================

package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Handles database connection to MySQL
 */
public class DatabaseConnection {
    
    // Database credentials - CHANGE THESE to match your MySQL setup
    private static final String URL = "jdbc:mysql://localhost:3306/billing_app";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "your_password"; // Change this
    
    /**
     * Get a connection to the database
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
    }
    
    /**
     * Test database connection
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            return false;
        }
    }
}