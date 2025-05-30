package com.smartvend.app.db;

import java.sql.DriverManager;
import java.sql.SQLException;

public class DBManager {
    private static DBManager instance;
    private String jdbcUrl;
    private String username;
    private String password;
    private java.sql.Connection connection;

    private DBManager() {
        this.jdbcUrl = "jdbc:postgresql://db:5432/smartvend";
        this.username = "smartuser";
        this.password = "smartpass";
    }

    public static DBManager getInstance() {
        if (instance == null) {
            instance = new DBManager();
        }
        return instance;
    }

    public java.sql.Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(jdbcUrl, username, password);
            } catch (SQLException e) {
                System.err.println("Failed to establish database connection: " + e.getMessage());
                connection = null;
            }
        }
        return connection;
    }

    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
            } catch (java.sql.SQLException e) {
                System.err.println("Failed to close connection: " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }
}
