package com.smartvend.app.services;

import java.time.Instant;
import java.util.List;

import com.smartvend.app.dao.impl.AdminDaoImpl;
import com.smartvend.app.dao.impl.ConcreteVendingMachineDaoImpl;
import com.smartvend.app.dao.impl.CustomerDaoImpl;
import com.smartvend.app.dao.impl.TaskDaoImpl;
import com.smartvend.app.dao.impl.UserDaoImpl;
import com.smartvend.app.dao.impl.WorkerDaoImpl;
import com.smartvend.app.model.maintenance.MaintenanceReport;
import com.smartvend.app.model.maintenance.MaintenanceStatus;
import com.smartvend.app.model.maintenance.Task;
import com.smartvend.app.model.user.Admin;
import com.smartvend.app.model.user.Customer;
import com.smartvend.app.model.user.User;
import com.smartvend.app.model.user.Worker;
import com.smartvend.app.model.vendingmachine.ConcreteVendingMachine;

public class AdminService {
    private ConcreteVendingMachineDaoImpl concreteVendingMachineDao;
    private UserDaoImpl userDao;
    private WorkerDaoImpl workerDao;
    private CustomerDaoImpl customerDao;
    private AdminDaoImpl adminDao;
    private TaskDaoImpl taskDao;
    private MaintenanceService maintenanceService;

    public AdminService(ConcreteVendingMachineDaoImpl concreteVendingMachineDao, UserDaoImpl userDao,
            WorkerDaoImpl workerDao, CustomerDaoImpl customerDao, AdminDaoImpl adminDao, TaskDaoImpl taskDao,
            MaintenanceService maintenanceService) {
        this.concreteVendingMachineDao = concreteVendingMachineDao;
        this.userDao = userDao;
        this.workerDao = workerDao;
        this.customerDao = customerDao;
        this.adminDao = adminDao;
        this.taskDao = taskDao;
        this.maintenanceService = maintenanceService;
    }

    // Machine CRUD
    public List<ConcreteVendingMachine> getMachines() {
        if (concreteVendingMachineDao == null) {
            throw new IllegalStateException("ConcreteVendingMachineDao is not initialized");
        }
        return concreteVendingMachineDao.findAll();
    }

    public ConcreteVendingMachine getMachineById(String id) {
        if (concreteVendingMachineDao == null) {
            throw new IllegalStateException("ConcreteVendingMachineDao is not initialized");
        }
        return concreteVendingMachineDao.findById(id);
    }

    public ConcreteVendingMachine createMachine(ConcreteVendingMachine machine) {
        if (concreteVendingMachineDao == null) {
            throw new IllegalStateException("ConcreteVendingMachineDao is not initialized");
        }
        if (machine == null) {
            throw new IllegalArgumentException("Machine cannot be null");
        }
        return concreteVendingMachineDao.createMachine(machine);
    }

    public ConcreteVendingMachine updateMachine(ConcreteVendingMachine machine) {
        if (concreteVendingMachineDao == null) {
            throw new IllegalStateException("ConcreteVendingMachineDao is not initialized");
        }
        if (machine == null || machine.getId() == null) {
            throw new IllegalArgumentException("Machine or machine ID cannot be null");
        }
        return concreteVendingMachineDao.updateMachine(machine);
    }

    public void deleteMachine(String id) {
        if (concreteVendingMachineDao == null) {
            throw new IllegalStateException("ConcreteVendingMachineDao is not initialized");
        }
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Machine ID cannot be null or empty");
        }
        concreteVendingMachineDao.deleteMachine(id);
    }

    // User CRUD
    public List<User> getUsers() {
        if (userDao == null) {
            throw new IllegalStateException("UserDao is not initialized");
        }
        return userDao.findAll();
    }

    public User getUserByEmail(String email) {
        if (userDao == null) {
            throw new IllegalStateException("UserDao is not initialized");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        return userDao.getUserByEmail(email);
    }

    public User createUser(User user) {
        if (userDao == null) {
            throw new IllegalStateException("UserDao is not initialized");
        }
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        return userDao.createUser(user);
    }

    public User updateUser(User user) {
        if (userDao == null) {
            throw new IllegalStateException("UserDao is not initialized");
        }
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        return userDao.updateUser(user);
    }

    public void deleteUser(Long id) {
        if (userDao == null) {
            throw new IllegalStateException("UserDao is not initialized");
        }
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("User ID is invalid");
        }
        userDao.deleteUser(id);
    }

    // Worker CRUD
    public List<Worker> getWorkers() {
        if (workerDao == null) {
            throw new IllegalStateException("WorkerDao is not initialized");
        }
        return workerDao.findAllWorkers();
    }

    public Worker getWorkerByEmail(String email) {
        if (workerDao == null) {
            throw new IllegalStateException("WorkerDao is not initialized");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        return workerDao.getWorkerByEmail(email);
    }

    public Worker createWorker(Worker worker) {
        if (workerDao == null) {
            throw new IllegalStateException("WorkerDao is not initialized");
        }
        if (worker == null) {
            throw new IllegalArgumentException("Worker cannot be null");
        }
        return workerDao.createWorker(worker);
    }

    public Worker updateWorker(Worker worker) {
        if (workerDao == null) {
            throw new IllegalStateException("WorkerDao is not initialized");
        }
        if (worker == null) {
            throw new IllegalArgumentException("Worker cannot be null");
        }
        return workerDao.updateWorker(worker);
    }

    public void deleteWorker(Long id) {
        if (workerDao == null) {
            throw new IllegalStateException("WorkerDao is not initialized");
        }
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Worker ID is invalid");
        }
        workerDao.deleteWorker(id);
    }

    // Customer CRUD
    public List<Customer> getCustomers() {
        if (customerDao == null) {
            throw new IllegalStateException("UserDao is not initialized");
        }
        return customerDao.findAll();
    }

    public Customer getCustomerById(Long id) {
        if (userDao == null) {
            throw new IllegalStateException("UserDao is not initialized");
        }
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Customer ID is invalid");
        }
        return customerDao.getCustomerById(id);
    }

    public Customer createCustomer(Customer customer) {
        if (customerDao == null) {
            throw new IllegalStateException("CustomerDao is not initialized");
        }
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        return customerDao.createCustomer(customer);
    }

    public Customer updateCustomer(Customer customer) {
        if (customerDao == null) {
            throw new IllegalStateException("CustomerDao is not initialized");
        }
        if (customer == null || customer.getId() == null) {
            throw new IllegalArgumentException("Customer or customer ID cannot be null");
        }
        return customerDao.updateCustomer(customer);
    }

    public void deleteCustomer(Long id) {
        if (customerDao == null) {
            throw new IllegalStateException("CustomerDao is not initialized");
        }
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Customer ID is invalid");
        }
        customerDao.deleteCustomer(id);
    }

    // Admin CRUD
    public List<Admin> getAdmins() {
        if (adminDao == null) {
            throw new IllegalStateException("AdminDao is not initialized");
        }
        return adminDao.findAll();
    }

    public Admin getAdminByEmail(String email) {
        if (adminDao == null) {
            throw new IllegalStateException("AdminDao is not initialized");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        return adminDao.getAdminByEmail(email);
    }

    public Admin createAdmin(Admin admin) {
        if (adminDao == null) {
            throw new IllegalStateException("AdminDao is not initialized");
        }
        if (admin == null) {
            throw new IllegalArgumentException("Admin cannot be null");
        }
        return adminDao.createAdmin(admin);
    }

    public Admin updateAdmin(Admin admin) {
        if (adminDao == null) {
            throw new IllegalStateException("AdminDao is not initialized");
        }
        if (admin == null) {
            throw new IllegalArgumentException("Admin cannot be null");
        }
        return adminDao.updateAdmin(admin);
    }

    public void deleteAdmin(Long id) {
        if (adminDao == null) {
            throw new IllegalStateException("AdminDao is not initialized");
        }
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Admin ID is invalid");
        }
        adminDao.deleteAdmin(id);
    }

    // Create Task for Worker
    public Task createTaskForWorker(Long supervisorId, Long workerId, Long reportId, MaintenanceStatus status,
            Instant assignedAt) {
        if (taskDao == null)
            throw new IllegalStateException("TaskDao is not initialized");
        if (supervisorId == null || workerId == null || reportId == null || status == null || assignedAt == null)
            throw new IllegalArgumentException("Arguments cannot be null");

        Admin supervisor = adminDao.getAdminById(supervisorId.toString());
        Worker worker = workerDao.getWorkerById(workerId.toString());
        MaintenanceReport report = maintenanceService.getReportById(reportId);

        if (supervisor == null)
            throw new IllegalArgumentException("Supervisor not found");
        if (worker == null)
            throw new IllegalArgumentException("Worker not found");
        if (report == null)
            throw new IllegalArgumentException("Report not found");

        Task task = new Task(supervisor, worker, report);
        return taskDao.createTask(task);
    }
}
