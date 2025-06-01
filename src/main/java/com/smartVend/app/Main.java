package com.smartvend.app;

import com.smartvend.app.db.DatabaseInitializer;
import java.util.Scanner;

import javax.swing.text.html.parser.Entity;
import jakarta.persistence.EntityManagerFactory;

import com.smartvend.app.controllers.WorkerController;
import com.smartvend.app.dao.impl.TaskDaoImpl;
import com.smartvend.app.dao.impl.UserDaoImpl;
import com.smartvend.app.dao.impl.WorkerDaoImpl;
import com.smartvend.app.model.user.*;
import com.smartvend.app.services.TaskService;
import com.smartvend.app.services.UserService;
import com.smartvend.app.services.WorkerService;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("SmartVend application started successfully!");
            // Initialize the database
            EntityManagerFactory emf = DatabaseInitializer.getEntityManagerFactory();

            // Initialize controllers
            UserService userService = new UserService(new UserDaoImpl());

            // Ask for role
            Scanner scanner = new Scanner(System.in);
            System.out.println("\n\n\nSelect your role:");
            System.out.println("1. Admin");
            System.out.println("2. Worker");
            System.out.println("3. Customer");
            System.out.print("\nEnter the number corresponding to your role: ");
            String roleInput = scanner.nextLine().trim();
            String role;
            switch (roleInput) {
                case "2":
                    role = "worker";
                    break;
                case "3":
                    role = "customer";
                    break;
                default:
                    System.out.println("Invalid selection. Defaulting to 'customer'.");
                    role = "customer";
            }
            System.out.println("You selected: " + role);

            // Prompt for login credentials
            System.out.print("Enter your email: ");
            String email = scanner.nextLine().trim();

            System.out.print("Enter your password: ");
            String password = scanner.nextLine().trim();

            switch (role) {
                case "admin":
                    // TODO: Implement admin logic
                    System.out.println("Admin functionality is not yet implemented.");
                    break;
                case "worker":
                    WorkerService workerService = new WorkerService(new WorkerDaoImpl(emf), userService,
                            new TaskDaoImpl());
                    TaskService taskService = new TaskService(new TaskDaoImpl());
                    WorkerController workerController = new WorkerController(workerService, taskService);
                    Worker worker = workerController.login(email, password);
                    System.out.println("Welcome, " + worker.getUser().getName() + "! You are logged in as a Worker."
                            + "isActive: " + worker.isActive());
                    break;
                case "customer":
                    // TODO: Implement customer logic
                    System.out.println("Customer functionality is not yet implemented.");
                    break;
                default:
                    System.out.println("Invalid role selected.");
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