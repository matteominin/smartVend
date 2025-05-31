package com.smartvend.app;

import com.smartvend.app.db.DatabaseInitializer;

public class Main {
    public static void main(String[] args) {
        try {
            DatabaseInitializer.initializeDatabase();
            System.out.println("SmartVend application started successfully!");
        } catch (Exception e) {
            System.err.println("Application failed to start: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseInitializer.shutdown();
            System.out.println("Main application logic finished.");
        }
    }
}