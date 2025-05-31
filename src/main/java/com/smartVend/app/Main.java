package com.smartvend.app;

import com.smartvend.app.db.DatabaseInitializer;

public class Main {
    public static void main(String[] args) {
        DatabaseInitializer.initializeDatabase();

        DatabaseInitializer.shutdown();
    }
}