package com.smartvend.app.services;

import com.smartvend.app.dao.impl.AdminDaoImpl;
import com.smartvend.app.dao.impl.ConcreteVendingMachineDaoImpl;
import com.smartvend.app.dao.impl.UserDaoImpl;
import com.smartvend.app.dao.impl.WorkerDaoImpl;
import com.smartvend.app.dao.impl.CustomerDaoImpl;
import com.smartvend.app.dao.impl.TaskDaoImpl;
import com.smartvend.app.model.maintenance.MaintenanceReport;
import com.smartvend.app.model.maintenance.MaintenanceStatus;
import com.smartvend.app.model.maintenance.Task;
import com.smartvend.app.model.user.Admin;
import com.smartvend.app.model.user.Customer;
import com.smartvend.app.model.user.User;
import com.smartvend.app.model.user.Worker;
import com.smartvend.app.model.vendingmachine.ConcreteVendingMachine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AdminServiceTest {

    private ConcreteVendingMachineDaoImpl machineDao;
    private UserDaoImpl userDao;
    private WorkerDaoImpl workerDao;
    private CustomerDaoImpl customerDao;
    private AdminDaoImpl adminDao;
    private TaskDaoImpl taskDao;
    private MaintenanceService maintenanceService;
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        machineDao = mock(ConcreteVendingMachineDaoImpl.class);
        userDao = mock(UserDaoImpl.class);
        workerDao = mock(WorkerDaoImpl.class);
        customerDao = mock(CustomerDaoImpl.class);
        adminDao = mock(AdminDaoImpl.class);
        taskDao = mock(TaskDaoImpl.class);
        maintenanceService = mock(MaintenanceService.class);
        adminService = new AdminService(
                machineDao, userDao, workerDao, customerDao, adminDao, taskDao, maintenanceService);
    }

    @Test
    void testGetMachinesReturnsList() {
        ConcreteVendingMachine machine1 = mock(ConcreteVendingMachine.class);
        ConcreteVendingMachine machine2 = mock(ConcreteVendingMachine.class);
        when(machineDao.findAll()).thenReturn(Arrays.asList(machine1, machine2));

        List<ConcreteVendingMachine> result = adminService.getMachines();

        assertEquals(2, result.size());
        verify(machineDao).findAll();
    }

    @Test
    void testGetMachinesThrowsIfDaoNull() {
        adminService = new AdminService(null, userDao, workerDao, customerDao, adminDao, taskDao, maintenanceService);
        assertThrows(IllegalStateException.class, () -> adminService.getMachines());
    }

    @Test
    void testGetMachineByIdReturnsMachine() {
        ConcreteVendingMachine machine = mock(ConcreteVendingMachine.class);
        when(machineDao.findById("id1")).thenReturn(machine);

        ConcreteVendingMachine result = adminService.getMachineById("id1");

        assertEquals(machine, result);
        verify(machineDao).findById("id1");
    }

    @Test
    void testGetMachineByIdThrowsIfDaoNull() {
        adminService = new AdminService(null, userDao, workerDao, customerDao, adminDao, taskDao, maintenanceService);
        assertThrows(IllegalStateException.class, () -> adminService.getMachineById("id1"));
    }

    @Test
    void testCreateMachineReturnsCreatedMachine() {
        ConcreteVendingMachine machine = mock(ConcreteVendingMachine.class);
        when(machineDao.createMachine(machine)).thenReturn(machine);

        ConcreteVendingMachine result = adminService.createMachine(machine);

        assertEquals(machine, result);
        verify(machineDao).createMachine(machine);
    }

    @Test
    void testCreateMachineThrowsIfDaoNull() {
        adminService = new AdminService(null, userDao, workerDao, customerDao, adminDao, taskDao, maintenanceService);
        ConcreteVendingMachine machine = mock(ConcreteVendingMachine.class);
        assertThrows(IllegalStateException.class, () -> adminService.createMachine(machine));
    }

    @Test
    void testCreateMachineThrowsIfMachineNull() {
        assertThrows(IllegalArgumentException.class, () -> adminService.createMachine(null));
    }

    @Test
    void testUpdateMachineReturnsUpdatedMachine() {
        ConcreteVendingMachine machine = mock(ConcreteVendingMachine.class);
        when(machine.getId()).thenReturn("id1");
        when(machineDao.updateMachine(machine)).thenReturn(machine);

        ConcreteVendingMachine result = adminService.updateMachine(machine);

        assertEquals(machine, result);
        verify(machineDao).updateMachine(machine);
    }

    @Test
    void testUpdateMachineThrowsIfDaoNull() {
        adminService = new AdminService(null, userDao, workerDao, customerDao, adminDao, taskDao, maintenanceService);
        ConcreteVendingMachine machine = mock(ConcreteVendingMachine.class);
        when(machine.getId()).thenReturn("id1");
        assertThrows(IllegalStateException.class, () -> adminService.updateMachine(machine));
    }

    @Test
    void testUpdateMachineThrowsIfMachineNull() {
        assertThrows(IllegalArgumentException.class, () -> adminService.updateMachine(null));
    }

    @Test
    void testUpdateMachineThrowsIfMachineIdNull() {
        ConcreteVendingMachine machine = mock(ConcreteVendingMachine.class);
        when(machine.getId()).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> adminService.updateMachine(machine));
    }

    @Test
    void testDeleteMachineCallsDao() {
        adminService.deleteMachine("id1");
        verify(machineDao).deleteMachine("id1");
    }

    @Test
    void testDeleteMachineThrowsIfDaoNull() {
        adminService = new AdminService(null, userDao, workerDao, customerDao, adminDao, taskDao, maintenanceService);
        assertThrows(IllegalStateException.class, () -> adminService.deleteMachine("id1"));
    }

    @Test
    void testDeleteMachineThrowsIfIdNull() {
        assertThrows(IllegalArgumentException.class, () -> adminService.deleteMachine(null));
    }

    @Test
    void testDeleteMachineThrowsIfIdEmpty() {
        assertThrows(IllegalArgumentException.class, () -> adminService.deleteMachine("  "));
    }

    @Test
    void testGetUsersReturnsList() {
        User user1 = mock(User.class);
        User user2 = mock(User.class);
        when(userDao.findAll()).thenReturn(Arrays.asList(user1, user2));

        List<User> result = adminService.getUsers();

        assertEquals(2, result.size());
        verify(userDao).findAll();
    }

    @Test
    void testGetUsersThrowsIfDaoNull() {
        adminService = new AdminService(machineDao, null, workerDao, customerDao, adminDao, taskDao,
                maintenanceService);
        assertThrows(IllegalStateException.class, () -> adminService.getUsers());
    }

    @Test
    void testGetUserByEmailReturnsUser() {
        User user = mock(User.class);
        when(userDao.getUserByEmail("test@example.com")).thenReturn(user);

        User result = adminService.getUserByEmail("test@example.com");

        assertEquals(user, result);
        verify(userDao).getUserByEmail("test@example.com");
    }

    @Test
    void testGetUserByEmailThrowsIfDaoNull() {
        adminService = new AdminService(machineDao, null, workerDao, customerDao, adminDao, taskDao,
                maintenanceService);
        assertThrows(IllegalStateException.class, () -> adminService.getUserByEmail("test@example.com"));
    }

    @Test
    void testGetUserByEmailThrowsIfEmailNull() {
        assertThrows(IllegalArgumentException.class, () -> adminService.getUserByEmail(null));
    }

    @Test
    void testGetUserByEmailThrowsIfEmailEmpty() {
        assertThrows(IllegalArgumentException.class, () -> adminService.getUserByEmail("  "));
    }

    @Test
    void testCreateUserReturnsCreatedUser() {
        User user = mock(User.class);
        when(userDao.createUser(user)).thenReturn(user);

        User result = adminService.createUser(user);

        assertEquals(user, result);
        verify(userDao).createUser(user);
    }

    @Test
    void testCreateUserThrowsIfDaoNull() {
        adminService = new AdminService(machineDao, null, workerDao, customerDao, adminDao, taskDao,
                maintenanceService);
        User user = mock(User.class);
        assertThrows(IllegalStateException.class, () -> adminService.createUser(user));
    }

    @Test
    void testCreateUserThrowsIfUserNull() {
        assertThrows(IllegalArgumentException.class, () -> adminService.createUser(null));
    }

    @Test
    void testUpdateUserReturnsUpdatedUser() {
        User user = mock(User.class);
        when(userDao.updateUser(user)).thenReturn(user);

        User result = adminService.updateUser(user);

        assertEquals(user, result);
        verify(userDao).updateUser(user);
    }

    @Test
    void testUpdateUserThrowsIfDaoNull() {
        adminService = new AdminService(machineDao, null, workerDao, customerDao, adminDao, taskDao,
                maintenanceService);
        User user = mock(User.class);
        assertThrows(IllegalStateException.class, () -> adminService.updateUser(user));
    }

    @Test
    void testUpdateUserThrowsIfUserNull() {
        assertThrows(IllegalArgumentException.class, () -> adminService.updateUser(null));
    }

    @Test
    void testDeleteUserCallsDao() {
        adminService.deleteUser(1L);
        verify(userDao).deleteUser(1L);
    }

    @Test
    void testDeleteUserThrowsIfDaoNull() {
        adminService = new AdminService(machineDao, null, workerDao, customerDao, adminDao, taskDao,
                maintenanceService);
        assertThrows(IllegalStateException.class, () -> adminService.deleteUser(1L));
    }

    @Test
    void testDeleteUserThrowsIfIdNull() {
        assertThrows(IllegalArgumentException.class, () -> adminService.deleteUser(null));
    }

    @Test
    void testDeleteUserThrowsIfIdZero() {
        assertThrows(IllegalArgumentException.class, () -> adminService.deleteUser(0L));
    }

    @Test
    void testDeleteUserThrowsIfIdNegative() {
        assertThrows(IllegalArgumentException.class, () -> adminService.deleteUser(-5L));
    }

    @Test
    void testGetWorkersReturnsList() {
        Worker worker1 = mock(Worker.class);
        Worker worker2 = mock(Worker.class);
        when(workerDao.findAllWorkers()).thenReturn(Arrays.asList(worker1, worker2));

        List<Worker> result = adminService.getWorkers();

        assertEquals(2, result.size());
        verify(workerDao).findAllWorkers();
    }

    @Test
    void testGetWorkersThrowsIfDaoNull() {
        adminService = new AdminService(machineDao, userDao, null, customerDao, adminDao, taskDao, maintenanceService);
        assertThrows(IllegalStateException.class, () -> adminService.getWorkers());
    }

    @Test
    void testGetWorkerByEmailReturnsWorker() {
        Worker worker = mock(Worker.class);
        when(workerDao.getWorkerByEmail("worker@example.com")).thenReturn(worker);

        Worker result = adminService.getWorkerByEmail("worker@example.com");

        assertEquals(worker, result);
        verify(workerDao).getWorkerByEmail("worker@example.com");
    }

    @Test
    void testGetWorkerByEmailThrowsIfDaoNull() {
        adminService = new AdminService(machineDao, userDao, null, customerDao, adminDao, taskDao, maintenanceService);
        assertThrows(IllegalStateException.class, () -> adminService.getWorkerByEmail("worker@example.com"));
    }

    @Test
    void testGetWorkerByEmailThrowsIfEmailNull() {
        assertThrows(IllegalArgumentException.class, () -> adminService.getWorkerByEmail(null));
    }

    @Test
    void testGetWorkerByEmailThrowsIfEmailEmpty() {
        assertThrows(IllegalArgumentException.class, () -> adminService.getWorkerByEmail("   "));
    }

    @Test
    void testCreateWorkerReturnsCreatedWorker() {
        Worker worker = mock(Worker.class);
        when(workerDao.createWorker(worker)).thenReturn(worker);

        Worker result = adminService.createWorker(worker);

        assertEquals(worker, result);
        verify(workerDao).createWorker(worker);
    }

    @Test
    void testCreateWorkerThrowsIfDaoNull() {
        adminService = new AdminService(machineDao, userDao, null, customerDao, adminDao, taskDao, maintenanceService);
        Worker worker = mock(Worker.class);
        assertThrows(IllegalStateException.class, () -> adminService.createWorker(worker));
    }

    @Test
    void testCreateWorkerThrowsIfWorkerNull() {
        assertThrows(IllegalArgumentException.class, () -> adminService.createWorker(null));
    }

    @Test
    void testUpdateWorkerReturnsUpdatedWorker() {
        Worker worker = mock(Worker.class);
        when(workerDao.updateWorker(worker)).thenReturn(worker);

        Worker result = adminService.updateWorker(worker);

        assertEquals(worker, result);
        verify(workerDao).updateWorker(worker);
    }

    @Test
    void testUpdateWorkerThrowsIfDaoNull() {
        adminService = new AdminService(machineDao, userDao, null, customerDao, adminDao, taskDao, maintenanceService);
        Worker worker = mock(Worker.class);
        assertThrows(IllegalStateException.class, () -> adminService.updateWorker(worker));
    }

    @Test
    void testUpdateWorkerThrowsIfWorkerNull() {
        assertThrows(IllegalArgumentException.class, () -> adminService.updateWorker(null));
    }

    @Test
    void testDeleteWorkerCallsDao() {
        adminService.deleteWorker(2L);
        verify(workerDao).deleteWorker(2L);
    }

    @Test
    void testDeleteWorkerThrowsIfDaoNull() {
        adminService = new AdminService(machineDao, userDao, null, customerDao, adminDao, taskDao, maintenanceService);
        assertThrows(IllegalStateException.class, () -> adminService.deleteWorker(2L));
    }

    @Test
    void testDeleteWorkerThrowsIfIdNull() {
        assertThrows(IllegalArgumentException.class, () -> adminService.deleteWorker(null));
    }

    @Test
    void testDeleteWorkerThrowsIfIdZero() {
        assertThrows(IllegalArgumentException.class, () -> adminService.deleteWorker(0L));
    }

    @Test
    void testDeleteWorkerThrowsIfIdNegative() {
        assertThrows(IllegalArgumentException.class, () -> adminService.deleteWorker(-10L));
    }

    @Test
    void testCreateTaskForWorkerReturnsTask() {
        Long supervisorId = 1L;
        Long workerId = 2L;
        Long reportId = 3L;
        MaintenanceStatus status = MaintenanceStatus.Assigned;
        Instant AssignedAt = Instant.now();

        Admin supervisor = mock(Admin.class);
        Worker worker = mock(Worker.class);
        MaintenanceReport report = mock(MaintenanceReport.class);
        Task task = mock(Task.class);

        when(adminDao.getAdminById(supervisorId)).thenReturn(supervisor);
        when(workerDao.getWorkerById(workerId)).thenReturn(worker);
        when(maintenanceService.getReportById(reportId)).thenReturn(report);
        when(taskDao.createTask(any(Task.class))).thenReturn(task);

        Task result = adminService.createTaskForWorker(supervisorId, workerId, reportId, status, AssignedAt);

        assertEquals(task, result);
        verify(adminDao).getAdminById(supervisorId);
        verify(workerDao).getWorkerById(workerId);
        verify(maintenanceService).getReportById(reportId);
        verify(taskDao).createTask(any(Task.class));
    }

    @Test
    void testCreateTaskForWorkerThrowsIfTaskDaoNull() {
        adminService = new AdminService(machineDao, userDao, workerDao, customerDao, adminDao, null,
                maintenanceService);
        assertThrows(IllegalStateException.class,
                () -> adminService.createTaskForWorker(1L, 2L, 3L, MaintenanceStatus.Assigned, Instant.now()));
    }

    @Test
    void testCreateTaskForWorkerThrowsIfAnyArgumentNull() {
        assertThrows(IllegalArgumentException.class,
                () -> adminService.createTaskForWorker(null, 2L, 3L, MaintenanceStatus.Assigned, Instant.now()));
        assertThrows(IllegalArgumentException.class,
                () -> adminService.createTaskForWorker(1L, null, 3L, MaintenanceStatus.Assigned, Instant.now()));
        assertThrows(IllegalArgumentException.class,
                () -> adminService.createTaskForWorker(1L, 2L, null, MaintenanceStatus.Assigned, Instant.now()));
        assertThrows(IllegalArgumentException.class,
                () -> adminService.createTaskForWorker(1L, 2L, 3L, null, Instant.now()));
        assertThrows(IllegalArgumentException.class,
                () -> adminService.createTaskForWorker(1L, 2L, 3L, MaintenanceStatus.Assigned, null));
    }

    @Test
    void testCreateTaskForWorkerThrowsIfSupervisorNotFound() {
        Long supervisorId = 1L;
        Long workerId = 2L;
        Long reportId = 3L;
        MaintenanceStatus status = MaintenanceStatus.Assigned;
        Instant AssignedAt = Instant.now();

        when(adminDao.getAdminById(supervisorId)).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> adminService.createTaskForWorker(supervisorId, workerId, reportId, status, AssignedAt));
    }

    @Test
    void testCreateTaskForWorkerThrowsIfWorkerNotFound() {
        Long supervisorId = 1L;
        Long workerId = 2L;
        Long reportId = 3L;
        MaintenanceStatus status = MaintenanceStatus.Assigned;
        Instant AssignedAt = Instant.now();

        Admin supervisor = mock(Admin.class);
        when(adminDao.getAdminById(supervisorId)).thenReturn(supervisor);
        when(workerDao.getWorkerByUserId(workerId)).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> adminService.createTaskForWorker(supervisorId, workerId, reportId, status, AssignedAt));
    }

    @Test
    void testCreateTaskForWorkerThrowsIfReportNotFound() {
        Long supervisorId = 1L;
        Long workerId = 2L;
        Long reportId = 3L;
        MaintenanceStatus status = MaintenanceStatus.Assigned;
        Instant AssignedAt = Instant.now();

        Admin supervisor = mock(Admin.class);
        Worker worker = mock(Worker.class);

        when(adminDao.getAdminById(supervisorId)).thenReturn(supervisor);
        when(workerDao.getWorkerByUserId(workerId)).thenReturn(worker);
        when(maintenanceService.getReportById(reportId)).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> adminService.createTaskForWorker(supervisorId, workerId, reportId, status, AssignedAt));
    }

    // Customer CRUD tests

    @Test
    void testGetCustomersReturnsList() {
        Customer customer1 = mock(Customer.class);
        Customer customer2 = mock(Customer.class);
        when(customerDao.findAll()).thenReturn(Arrays.asList(customer1, customer2));

        List<Customer> result = adminService.getCustomers();

        assertEquals(2, result.size());
        verify(customerDao).findAll();
    }

    @Test
    void testGetCustomersThrowsIfDaoNull() {
        adminService = new AdminService(machineDao, userDao, workerDao, null, adminDao, taskDao, maintenanceService);
        assertThrows(IllegalStateException.class, () -> adminService.getCustomers());
    }

    @Test
    void testGetCustomerByIdReturnsCustomer() {
        Customer customer = mock(Customer.class);
        when(customerDao.getCustomerById(1L)).thenReturn(customer);

        Customer result = adminService.getCustomerById(1L);

        assertEquals(customer, result);
        verify(customerDao).getCustomerById(1L);
    }

    @Test
    void testGetCustomerByIdThrowsIfIdNull() {
        assertThrows(IllegalArgumentException.class, () -> adminService.getCustomerById(null));
    }

    @Test
    void testGetCustomerByIdThrowsIfIdZeroOrNegative() {
        assertThrows(IllegalArgumentException.class, () -> adminService.getCustomerById(0L));
        assertThrows(IllegalArgumentException.class, () -> adminService.getCustomerById(-1L));
    }

    @Test
    void testCreateCustomerReturnsCreatedCustomer() {
        Customer customer = mock(Customer.class);
        when(customerDao.createCustomer(customer)).thenReturn(customer);

        Customer result = adminService.createCustomer(customer);

        assertEquals(customer, result);
        verify(customerDao).createCustomer(customer);
    }

    @Test
    void testCreateCustomerThrowsIfDaoNull() {
        adminService = new AdminService(machineDao, userDao, workerDao, null, adminDao, taskDao, maintenanceService);
        Customer customer = mock(Customer.class);
        assertThrows(IllegalStateException.class, () -> adminService.createCustomer(customer));
    }

    @Test
    void testCreateCustomerThrowsIfCustomerNull() {
        assertThrows(IllegalArgumentException.class, () -> adminService.createCustomer(null));
    }

    @Test
    void testUpdateCustomerReturnsUpdatedCustomer() {
        Customer customer = mock(Customer.class);
        when(customer.getId()).thenReturn(1L);
        when(customerDao.updateCustomer(customer)).thenReturn(customer);

        Customer result = adminService.updateCustomer(customer);

        assertEquals(customer, result);
        verify(customerDao).updateCustomer(customer);
    }

    @Test
    void testUpdateCustomerThrowsIfDaoNull() {
        adminService = new AdminService(machineDao, userDao, workerDao, null, adminDao, taskDao, maintenanceService);
        Customer customer = mock(Customer.class);
        when(customer.getId()).thenReturn(1L);
        assertThrows(IllegalStateException.class, () -> adminService.updateCustomer(customer));
    }

    @Test
    void testUpdateCustomerThrowsIfCustomerNull() {
        assertThrows(IllegalArgumentException.class, () -> adminService.updateCustomer(null));
    }

    @Test
    void testUpdateCustomerThrowsIfCustomerIdNull() {
        Customer customer = mock(Customer.class);
        when(customer.getId()).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> adminService.updateCustomer(customer));
    }

    @Test
    void testDeleteCustomerCallsDao() {
        adminService.deleteCustomer(1L);
        verify(customerDao).deleteCustomer(1L);
    }

    @Test
    void testDeleteCustomerThrowsIfDaoNull() {
        adminService = new AdminService(machineDao, userDao, workerDao, null, adminDao, taskDao, maintenanceService);
        assertThrows(IllegalStateException.class, () -> adminService.deleteCustomer(1L));
    }

    @Test
    void testDeleteCustomerThrowsIfIdNull() {
        assertThrows(IllegalArgumentException.class, () -> adminService.deleteCustomer(null));
    }

    @Test
    void testDeleteCustomerThrowsIfIdZeroOrNegative() {
        assertThrows(IllegalArgumentException.class, () -> adminService.deleteCustomer(0L));
        assertThrows(IllegalArgumentException.class, () -> adminService.deleteCustomer(-1L));
    }

    // Admin CRUD tests

    @Test
    void testGetAdminsReturnsList() {
        Admin admin1 = mock(Admin.class);
        Admin admin2 = mock(Admin.class);
        when(adminDao.findAll()).thenReturn(Arrays.asList(admin1, admin2));

        List<Admin> result = adminService.getAdmins();

        assertEquals(2, result.size());
        verify(adminDao).findAll();
    }

    @Test
    void testGetAdminsThrowsIfDaoNull() {
        adminService = new AdminService(machineDao, userDao, workerDao, customerDao, null, taskDao, maintenanceService);
        assertThrows(IllegalStateException.class, () -> adminService.getAdmins());
    }

    @Test
    void testGetAdminByEmailReturnsAdmin() {
        Admin admin = mock(Admin.class);
        when(adminDao.getAdminByEmail("admin@example.com")).thenReturn(admin);

        Admin result = adminService.getAdminByEmail("admin@example.com");

        assertEquals(admin, result);
        verify(adminDao).getAdminByEmail("admin@example.com");
    }

    @Test
    void testGetAdminByEmailThrowsIfDaoNull() {
        adminService = new AdminService(machineDao, userDao, workerDao, customerDao, null, taskDao, maintenanceService);
        assertThrows(IllegalStateException.class, () -> adminService.getAdminByEmail("admin@example.com"));
    }

    @Test
    void testGetAdminByEmailThrowsIfEmailNull() {
        assertThrows(IllegalArgumentException.class, () -> adminService.getAdminByEmail(null));
    }

    @Test
    void testGetAdminByEmailThrowsIfEmailEmpty() {
        assertThrows(IllegalArgumentException.class, () -> adminService.getAdminByEmail("   "));
    }

    @Test
    void testCreateAdminReturnsCreatedAdmin() {
        Admin admin = mock(Admin.class);
        when(adminDao.createAdmin(admin)).thenReturn(admin);

        Admin result = adminService.createAdmin(admin);

        assertEquals(admin, result);
        verify(adminDao).createAdmin(admin);
    }

    @Test
    void testCreateAdminThrowsIfDaoNull() {
        adminService = new AdminService(machineDao, userDao, workerDao, customerDao, null, taskDao, maintenanceService);
        Admin admin = mock(Admin.class);
        assertThrows(IllegalStateException.class, () -> adminService.createAdmin(admin));
    }

    @Test
    void testCreateAdminThrowsIfAdminNull() {
        assertThrows(IllegalArgumentException.class, () -> adminService.createAdmin(null));
    }

    @Test
    void testUpdateAdminReturnsUpdatedAdmin() {
        Admin admin = mock(Admin.class);
        when(adminDao.updateAdmin(admin)).thenReturn(admin);

        Admin result = adminService.updateAdmin(admin);

        assertEquals(admin, result);
        verify(adminDao).updateAdmin(admin);
    }

    @Test
    void testUpdateAdminThrowsIfDaoNull() {
        adminService = new AdminService(machineDao, userDao, workerDao, customerDao, null, taskDao, maintenanceService);
        Admin admin = mock(Admin.class);
        assertThrows(IllegalStateException.class, () -> adminService.updateAdmin(admin));
    }

    @Test
    void testUpdateAdminThrowsIfAdminNull() {
        assertThrows(IllegalArgumentException.class, () -> adminService.updateAdmin(null));
    }

    @Test
    void testDeleteAdminCallsDao() {
        adminService.deleteAdmin(1L);
        verify(adminDao).deleteAdmin(1L);
    }

    @Test
    void testDeleteAdminThrowsIfDaoNull() {
        adminService = new AdminService(machineDao, userDao, workerDao, customerDao, null, taskDao, maintenanceService);
        assertThrows(IllegalStateException.class, () -> adminService.deleteAdmin(1L));
    }

    @Test
    void testDeleteAdminThrowsIfIdNull() {
        assertThrows(IllegalArgumentException.class, () -> adminService.deleteAdmin(null));
    }

    @Test
    void testDeleteAdminThrowsIfIdZeroOrNegative() {
        assertThrows(IllegalArgumentException.class, () -> adminService.deleteAdmin(0L));
        assertThrows(IllegalArgumentException.class, () -> adminService.deleteAdmin(-1L));
    }
}
