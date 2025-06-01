package com.smartvend.app;

import com.smartvend.app.db.DatabaseInitializer;

import java.util.List;
import java.util.Scanner;

import jakarta.persistence.EntityManagerFactory;

import com.smartvend.app.model.maintenance.Task;
import com.smartvend.app.controllers.UserController;
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
            UserService userService = new UserService(new UserDaoImpl(emf));
            UserController userController = new UserController(userService);

            Scanner scanner = new Scanner(System.in);

            System.out.println("\n\n\nWelcome to SmartVend! Please log in or sign up.");
            System.out.print("Enter your email: ");
            String email = scanner.nextLine();
            System.out.print("Enter your password: ");
            String password = scanner.nextLine();

            User user;
            try {
                user = userController.login(email, password);
            } catch (IllegalArgumentException e) {
                System.err.println("Login failed: " + e.getMessage());
                scanner.close();
                return;
            }

            System.out.println("\n\nLogin successful! Welcome, " + user.getName() + " " + user.getSurname());

            switch (user.getRole()) {
                case Admin:
                    System.out.println("You have admin privileges.\n\n");
                    // Admin specific logic can be added here
                    break;
                case Worker:
                    System.out.println("You are logged in as a worker.\n");

                    // Initialize WorkerController and services
                    TaskService taskService = new TaskService(new TaskDaoImpl(emf));
                    WorkerService workerService = new WorkerService(new WorkerDaoImpl(emf), new TaskDaoImpl(emf));
                    WorkerController workerController = new WorkerController(workerService, taskService);

                    Worker worker = workerController.getWorkerByUserId(user.getId());

                    List<Task> tasks = workerController.getTasks(worker.getId());

                    System.out.println("Tasks assigned to you:");
                    if (tasks.isEmpty()) {
                        System.out.println("No tasks assigned.");
                    } else {
                        for (Task task : tasks) {
                            System.out.println("Task ID: " + task.getId() + ", Description: " + task.getDescription()
                                    + ", Status: " + task.getStatus());
                        }
                    }

                    break;
                case Customer:
                    System.out.println("You are logged in as a customer.");

                    break;
                case User:
                    System.out.println("You are logged in as a user.");
                    // User specific logic can be added here
                    break;
            }

            scanner.close();
        } catch (Exception e) {
            System.err.println("Application failed to start: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("\nMain application logic finished.\n\n\n");
            DatabaseInitializer.shutdown();
        }
    }
}