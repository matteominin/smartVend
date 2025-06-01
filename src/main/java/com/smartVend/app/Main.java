package com.smartvend.app;

import com.smartvend.app.db.DatabaseInitializer;

import java.util.List;
import java.util.Scanner;

import jakarta.persistence.EntityManagerFactory;

import com.smartvend.app.model.connection.Connection;
import com.smartvend.app.model.maintenance.Task;
import com.smartvend.app.model.transaction.Transaction;
import com.smartvend.app.controllers.CustomerController;
import com.smartvend.app.controllers.MachineController;
import com.smartvend.app.controllers.UserController;
import com.smartvend.app.controllers.WorkerController;
import com.smartvend.app.dao.impl.ConcreteVendingMachineDaoImpl;
import com.smartvend.app.dao.impl.ConnectionDaoImpl;
import com.smartvend.app.dao.impl.CustomerDaoImpl;
import com.smartvend.app.dao.impl.InventoryDaoImpl;
import com.smartvend.app.dao.impl.ItemDaoImpl;
import com.smartvend.app.dao.impl.MaintenanceDaoImpl;
import com.smartvend.app.dao.impl.TaskDaoImpl;
import com.smartvend.app.dao.impl.TransactionDaoImpl;
import com.smartvend.app.dao.impl.UserDaoImpl;
import com.smartvend.app.dao.impl.WorkerDaoImpl;
import com.smartvend.app.model.user.*;
import com.smartvend.app.model.vendingmachine.Item;
import com.smartvend.app.services.CustomerService;
import com.smartvend.app.services.MachineService;
import com.smartvend.app.services.TaskService;
import com.smartvend.app.services.TransactionService;
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

            System.out.println("\nWelcome to SmartVend! Please log in or sign up.");
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

            System.out.println("\nLogin successful! Welcome, " + user.getName() + " " + user.getSurname());

            switch (user.getRole()) {
                case Admin:
                    System.out.println("You have admin privileges.\n");
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
                    System.out.println("You are logged in as a customer.\n");
                    // Initialize CustomerController and services if needed
                    CustomerService customerService = new CustomerService(
                            new CustomerDaoImpl(emf),
                            new ConnectionDaoImpl(emf),
                            new InventoryDaoImpl(emf),
                            new ItemDaoImpl(emf),
                            new ConcreteVendingMachineDaoImpl(emf),
                            new TransactionService(new TransactionDaoImpl(emf)));
                    CustomerController customerController = new CustomerController(userService, customerService);

                    String customerMenuChoice;
                    do {
                        Customer customer = customerController.getCustomerByUserId(user.getId());
                        System.out.println("\n1. Check balance");
                        System.out.println("2. Connect to a vending machine");
                        System.out.println("3. View transaction history");
                        System.out.println("4. Recharge balance");
                        System.out.println("5. Exit");
                        System.out.print("Enter your choice (1, 2, 3, 4 or 5): ");
                        customerMenuChoice = scanner.nextLine();

                        switch (customerMenuChoice) {
                            case "1":
                                System.out.println("\nYour current balance is: " + customer.getBalance() + "€");
                                break;
                            case "2":
                                MachineService machineService = new MachineService(
                                        new ItemDaoImpl(emf),
                                        new ConcreteVendingMachineDaoImpl(emf),
                                        new MaintenanceDaoImpl(emf));
                                MachineController machineController = new MachineController(machineService);
                                List<String> machineIds = machineController.getAllAvailableMachines();
                                System.out.println("\nAvailable vending machines:");
                                for (String machineId : machineIds) {
                                    System.out.println("Machine ID: " + machineId);
                                }
                                if (machineIds.isEmpty()) {
                                    System.out.println("No available vending machines.");
                                    break;
                                }

                                System.out.print("\nEnter the ID of the vending machine you want to connect to: ");
                                String machineId = scanner.nextLine();

                                if (!machineIds.contains(machineId)) {
                                    System.out.println("Invalid machine ID. Please try again.");
                                    continue;
                                }

                                // Connect to the vending machine
                                Connection connection = customerController.connect(machineId, customer.getId());
                                System.out.println("Connected to vending machine with ID: " + machineId);

                                // Display items in the machine
                                System.out.println("Loading items from the machine...\n");
                                List<Item> items = customerController.getInventory(connection);
                                if (items.isEmpty()) {
                                    System.out.println("No items available in this machine.");
                                    continue;
                                }
                                System.out.println("Items available in the machine:");
                                for (Item item : items) {
                                    System.out.println("Item ID: " + item.getId() + ", Name: " + item.getName()
                                            + ", Price: " + item.getPrice() + " €");
                                }

                                System.out.print("\nEnter the IDs of items you want to buy (comma separated): ");
                                String itemIdsInput = scanner.nextLine();
                                String[] itemIdsArray = itemIdsInput.split(",");
                                List<Long> itemIds = new java.util.ArrayList<>();
                                for (String id : itemIdsArray) {
                                    try {
                                        itemIds.add(Long.parseLong(id.trim()));
                                    } catch (NumberFormatException e) {
                                        System.out.println("Invalid item ID: " + id);
                                    }
                                }
                                if (itemIds.isEmpty()) {
                                    System.out.println("No valid item IDs provided.");
                                    continue;
                                }

                                // Buy items
                                try {
                                    customerController.buyItem(connection.getId(), itemIds);
                                    System.out.println("Items purchased successfully!");
                                } catch (IllegalArgumentException e) {
                                    System.out.println("Purchase failed: " + e.getMessage());
                                }
                                // Disconnect from the machine
                                customerController.disconnect(connection);
                                System.out.println("Disconnected from vending machine with ID: " + machineId);
                                break;
                            case "3":
                                List<Transaction> transactions = customerController
                                        .getTransactionHistory(customer.getId());
                                System.out.println("\nTransaction history:");
                                if (transactions.isEmpty()) {
                                    System.out.println("No transactions found.");
                                } else {
                                    for (Transaction transaction : transactions) {
                                        System.out.println("Transaction ID: " + transaction.getId()
                                                + ", Amount: " + transaction.getTotalAmout() + " €, Date: "
                                                + transaction.getDate());
                                    }
                                }
                                break;
                            case "4":
                                System.out.println("Your current balance is: " + customer.getBalance() + "€");
                                System.out.print("Enter the amount to recharge: ");
                                double rechargeAmount = scanner.nextDouble();
                                scanner.nextLine();
                                if (rechargeAmount <= 0) {
                                    System.out.println("Invalid recharge amount. Please try again.");
                                    continue;
                                }
                                customerController.rechargeBalance(customer.getId(), rechargeAmount);
                                customer = customerController.getCustomerByUserId(user.getId());
                                System.out.println("Balance recharged successfully! New balance: "
                                        + customer.getBalance() + "€");
                                break;
                            case "5":
                                System.out.println("Exiting customer menu.");
                                break;
                            default:
                                System.out.println("Invalid choice.");
                                break;
                        }
                    } while (!customerMenuChoice.equals("5"));

                    break;
                case User:
                    System.out.println("You are logged in as a user.");
                    break;
            }

            scanner.close();
        } catch (Exception e) {
            System.err.println("Application failed to start: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("Main application logic finished.\n");
            DatabaseInitializer.shutdown();
        }
    }
}