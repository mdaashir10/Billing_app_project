// ====================================================================================
// FILE 1: src/database/DatabaseConnection.java
// ====================================================================================

package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Handles database connection to MySQL (Docker-compatible)
 */
public class DatabaseConnection {
    
    // Database credentials from environment variables or defaults
    private static final String DB_HOST = System.getenv().getOrDefault("DB_HOST", "mysql");
    private static final String DB_PORT = System.getenv().getOrDefault("DB_PORT", "3306");
    private static final String DB_NAME = System.getenv().getOrDefault("DB_NAME", "billing_app");
    private static final String DB_USER = System.getenv().getOrDefault("DB_USER", "billing_user");
    private static final String DB_PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "billing123");
    
    private static final String URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME + 
                                     "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    
    /**
     * Get a connection to the database
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
    }
    
    /**
     * Test database connection with retries
     */
    public static boolean testConnection() {
        int maxRetries = 5;
        int retryCount = 0;
        
        while (retryCount < maxRetries) {
            try (Connection conn = getConnection()) {
                return conn != null && !conn.isClosed();
            } catch (SQLException e) {
                retryCount++;
                System.err.println("Database connection attempt " + retryCount + " failed: " + e.getMessage());
                if (retryCount < maxRetries) {
                    try {
                        Thread.sleep(2000); // Wait 2 seconds before retry
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        return false;
    }
}