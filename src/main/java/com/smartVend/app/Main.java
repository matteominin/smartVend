package com.smartvend.app;

import java.util.List;
import java.util.Scanner;

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
import com.smartvend.app.db.DatabaseInitializer;
import com.smartvend.app.model.connection.Connection;
import com.smartvend.app.model.maintenance.Task;
import com.smartvend.app.model.transaction.Transaction;
import com.smartvend.app.model.user.Customer;
import com.smartvend.app.model.user.User;
import com.smartvend.app.model.user.Worker;
import com.smartvend.app.model.vendingmachine.Item;
import com.smartvend.app.services.CustomerService;
import com.smartvend.app.services.MachineService;
import com.smartvend.app.services.TaskService;
import com.smartvend.app.services.TransactionService;
import com.smartvend.app.services.UserService;
import com.smartvend.app.services.WorkerService;

import jakarta.persistence.EntityManagerFactory;



public class Main {

    // ──────────────────────── ʝ Δ ʋ Δ Ƀ ʀ ɘ ʍ ──────────────────────── //
    private static void printBanner() {
        System.out.println("\n==========================================");
        System.out.println("          ☕  Welcome to JavaBrew  ☕       ");
        System.out.println("==========================================\n");
    }
    // ─────────────────────────────────────────────────────────────────── //

    public static void main(String[] args) {
        try {
            EntityManagerFactory emf = DatabaseInitializer.getEntityManagerFactory();

            /* ---------- DAO & Service wiring ---------- */
            UserService      userService      = new UserService(new UserDaoImpl(emf));
            CustomerService  customerService = new CustomerService(
                    new CustomerDaoImpl(emf),
                    new ConnectionDaoImpl(emf),
                    new InventoryDaoImpl(emf),
                    new ItemDaoImpl(emf),
                    new ConcreteVendingMachineDaoImpl(emf),
                    new TransactionService(new TransactionDaoImpl(emf)));
            WorkerService    workerService   = new WorkerService(
                    new WorkerDaoImpl(emf),
                    new TaskDaoImpl(emf));

            UserController   userController   = new UserController(userService);

            Scanner scanner = new Scanner(System.in);
            User    user    = null;

            printBanner();

            /* ---------- Login / Sign-Up loop ---------- */
            while (user == null) {
                System.out.println("1. Login");
                System.out.println("2. Sign Up");
                System.out.print("Select an option (1 or 2): ");
                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1" -> {
                        System.out.print("Email   : ");
                        String email = scanner.nextLine();
                        System.out.print("Password: ");
                        String pwd   = scanner.nextLine();
                        try {
                            user = userController.login(email, pwd);
                        } catch (IllegalArgumentException ex) {
                            System.out.println("⚠️  " + ex.getMessage() + "\n");
                        }
                    }
                    case "2" -> {
                        System.out.print("Name       : ");
                        String name    = scanner.nextLine();
                        System.out.print("Surname    : ");
                        String surname = scanner.nextLine();
                        System.out.print("Email      : ");
                        String email   = scanner.nextLine();
                        System.out.print("Password   : ");
                        String pwd     = scanner.nextLine();
                        System.out.print("Role (1 = Customer, 2 = Worker): ");
                        String rInput  = scanner.nextLine().trim();

                        User.Role role  = "2".equals(rInput) ? User.Role.Worker : User.Role.Customer;

                        try {
                            User newUser = new User(email, name, surname, pwd);
                            newUser.setRole(role);
                            user = userController.signUp(newUser);

                            if (role == User.Role.Customer) {
                                customerService.createCustomerFromUser(user);
                            } else {
                                workerService.createWorkerFromUser(user);
                            }
                            System.out.println("\n✅ Account created! Logged in as " + role + ".\n");
                        } catch (Exception ex) {
                            System.out.println("⚠️  Registration failed: " + ex.getMessage() + "\n");
                        }
                    }
                    default -> System.out.println("Please choose 1 or 2.\n");
                }
            }

            /* ---------- Post-login dispatch ---------- */
            System.out.printf("Hello %s %s! (%s)%n%n",
                    user.getName(), user.getSurname(), user.getRole());

            switch (user.getRole()) {

                // ────── ADMIN ───────────────────────────
                case Admin -> System.out.println("🔧 Admin panel coming soon…\n");

                // ────── WORKER ──────────────────────────
                case Worker -> {
                    TaskService     taskService     = new TaskService(new TaskDaoImpl(emf));
                    WorkerController workerCtrl      = new WorkerController(workerService, taskService);
                    Worker           worker          = workerCtrl.getWorkerByUserId(user.getId());
                    List<Task>       tasks           = workerCtrl.getTasks(worker.getId());

                    System.out.println("🛠  Your Tasks:");
                    if (tasks.isEmpty()) {
                        System.out.println("   • No tasks assigned.\n");
                    } else {
                        tasks.forEach(t -> System.out.printf(
                                "   • [%d] %s  (%s)%n",
                                t.getId(), t.getDescription(), t.getStatus()));
                        System.out.println();
                    }
                }

                // ────── CUSTOMER ────────────────────────
                case Customer -> {
                    CustomerController custCtrl = new CustomerController(userService, customerService);
                    String cmd;

                    do {
                        Customer cust = custCtrl.getCustomerByUserId(user.getId());
                        System.out.println("""
                                1. Check balance
                                2. Connect to a vending machine
                                3. View transaction history
                                4. Recharge balance
                                5. Exit""");
                        System.out.print("Choose (1-5): ");
                        cmd = scanner.nextLine().trim();

                        switch (cmd) {
                            case "1" -> System.out.printf("%n💰 Balance: %.2f €%n%n", cust.getBalance());

                            case "2" -> {
                                MachineService machService = new MachineService(
                                        new ItemDaoImpl(emf),
                                        new ConcreteVendingMachineDaoImpl(emf),
                                        new MaintenanceDaoImpl(emf));
                                MachineController machCtrl = new MachineController(machService);

                                List<String> machines = machCtrl.getAllAvailableMachines();
                                if (machines.isEmpty()) {
                                    System.out.println("\nNo machines available.\n");
                                    break;
                                }

                                System.out.println("\nAvailable machines:");
                                machines.forEach(mid -> System.out.println("   • " + mid));
                                System.out.print("Enter machine ID: ");
                                String mId = scanner.nextLine().trim();

                                if (!machines.contains(mId)) {
                                    System.out.println("Invalid ID.\n");
                                    break;
                                }

                                Connection conn = custCtrl.connect(mId, cust.getId());
                                List<Item> items = custCtrl.getInventory(conn);
                                if (items.isEmpty()) {
                                    System.out.println("Machine empty.\n");
                                    custCtrl.disconnect(conn);
                                    break;
                                }

                                System.out.println("\nItems:");
                                items.forEach(it -> System.out.printf(
                                        "   • [%d] %s – %.2f €%n",
                                        it.getId(), it.getName(), it.getPrice()));

                                System.out.print("Item IDs to buy (comma): ");
                                String[] idsStr  = scanner.nextLine().split(",");
                                List<Long> ids   = new java.util.ArrayList<>();
                                for (String s : idsStr) {
                                    try { ids.add(Long.parseLong(s.trim())); }
                                    catch (NumberFormatException ignore) {}
                                }

                                if (ids.isEmpty()) {
                                    System.out.println("No valid IDs.\n");
                                    custCtrl.disconnect(conn);
                                    break;
                                }

                                try {
                                    custCtrl.buyItem(conn.getId(), ids);
                                    System.out.println("✅ Purchase successful!\n");
                                } catch (Exception ex) {
                                    System.out.println("⚠️  " + ex.getMessage() + "\n");
                                } finally {
                                    custCtrl.disconnect(conn);
                                }
                            }

                            case "3" -> {
                                List<Transaction> txs = custCtrl.getTransactionHistory(cust.getId());
                                if (txs.isEmpty()) {
                                    System.out.println("\nNo transactions.\n");
                                } else {
                                    System.out.println();
                                    txs.forEach(t -> System.out.printf(
                                            "   • [%d] %.2f € on %s%n",
                                            t.getId(), t.getTotalAmout(), t.getDate()));
                                    System.out.println();
                                }
                            }

                            case "4" -> {
                                System.out.printf("Current balance: %.2f €%n", cust.getBalance());
                                System.out.print("Amount to add: ");
                                String in = scanner.nextLine();
                                try {
                                    double amt = Double.parseDouble(in);
                                    custCtrl.rechargeBalance(cust.getId(), amt);
                                    System.out.println("✅ Balance updated!\n");
                                } catch (NumberFormatException ex) {
                                    System.out.println("Invalid number.\n");
                                } catch (Exception ex) {
                                    System.out.println("⚠️  " + ex.getMessage() + "\n");
                                }
                            }

                            case "5" -> System.out.println("\nGoodbye!\n");
                            default  -> System.out.println("Choose 1-5.\n");
                        }
                    } while (!"5".equals(cmd));
                }

                // ────── GENERIC USER ────────────────────
                default -> System.out.println("Standard user menu coming soon…\n");
            }

            scanner.close();
        } catch (Exception ex) {
            System.err.println("Fatal: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            DatabaseInitializer.shutdown();
            System.out.println("☕  JavaBrew terminated.");
        }
    }
}
