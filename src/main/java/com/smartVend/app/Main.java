package com.smartvend.app;

import com.smartvend.app.db.DatabaseInitializer;
import java.util.Scanner;

import jakarta.persistence.EntityManagerFactory;

import com.smartvend.app.controllers.UserController;
import com.smartvend.app.dao.impl.UserDaoImpl;
import com.smartvend.app.model.user.*;
import com.smartvend.app.services.UserService;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("SmartVend application started successfully!");
            // Initialize the database
            EntityManagerFactory emf = DatabaseInitializer.getEntityManagerFactory();

            // Initialize controllers
            UserService userService = new UserService(new UserDaoImpl(emf));
            UserController userController = new UserController(userService);

            Scanner scanner = new Scanner(System.in);

            System.out.println("Welcome to SmartVend! Please log in or sign up.");
            System.out.print("Enter your email: ");
            String email = scanner.nextLine();
            System.out.print("Enter your password: ");
            String password = scanner.nextLine();

            User user = userController.login(email, password);
            if (user != null) {
                System.out.println("Login successful! Welcome, " + user.getName() + " " + user.getSurname());
            } else {
                System.out.println("Login failed. Please check your credentials.");
                scanner.close();
                return;
            }

            scanner.close();
        } catch (Exception e) {
            System.err.println("Application failed to start: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseInitializer.shutdown();
            System.out.println("Main application logic finished.");
        }
    }
}