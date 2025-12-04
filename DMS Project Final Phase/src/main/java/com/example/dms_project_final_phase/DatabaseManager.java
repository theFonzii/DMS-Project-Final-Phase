package com.example.dms_project_final_phase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * David Alfonsi
 * CEN 3024C - Software Development 1
 * December 3rd, 2025
 * Manages the JDBC connection to a SQLite database.
 * The user supplies the database path at runtime.
 */
public class DatabaseManager {

    private Connection connection;

    /**
     * Opens a new connection to the SQLite database at the given path.
     * Any existing connection is closed first.
     *
     * @param dbPath path to the .db or .sqlite file
     */
    public void connect(String dbPath) {
        close(); // close any existing connection
        try {
            String url = "jdbc:sqlite:" + dbPath;
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            throw new com.example.dms_project_final_phase.DbException("Failed to connect to SQLite database.", e);
        }
    }

    /**
     * Returns the active JDBC connection.
     * @return active connection
     * @throws IllegalStateException if connect() has not been called yet
     */
    public Connection getConnection() {
        if (connection == null) {
            throw new IllegalStateException("Database connection not initialized. Call connect() first.");
        }
        return connection;
    }

    /**
     * Closes the current connection (if any).
     */
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ignored) {
                // swallow close exceptions
            } finally {
                connection = null;
            }
        }
    }
}
