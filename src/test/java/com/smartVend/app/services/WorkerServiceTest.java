package com.smartvend.app.services;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.smartvend.app.dao.impl.TaskDaoImpl;
import com.smartvend.app.dao.impl.WorkerDaoImpl;
import com.smartvend.app.model.maintenance.MaintenanceReport;
import com.smartvend.app.model.maintenance.MaintenanceStatus;
import com.smartvend.app.model.maintenance.Task;
import com.smartvend.app.model.user.Admin;
import com.smartvend.app.model.user.User;
import com.smartvend.app.model.user.Worker;
import com.smartvend.app.model.vendingmachine.ConcreteVendingMachine;

public class WorkerServiceTest {
    @Mock
    private TaskDaoImpl taskDao;

    @InjectMocks
    private WorkerService workerService;

    private Worker workerMock;
    private Admin adminMock;
    private ConcreteVendingMachine vendingMachineMock;
    private MaintenanceReport maintenanceReportMock;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        workerMock = new Worker(2L,
                new User(1L, "email1@example.com", "workerName", "workerSurname", "workerPassword"));
        adminMock = new Admin(
                new User(2L, "email2@example.com", "adminName", "adminSurname", "adminPassword"));
        vendingMachineMock = new ConcreteVendingMachine();
        maintenanceReportMock = new MaintenanceReport("Test issue", Instant.now(), vendingMachineMock);
    }

    // Test for getWorkerTasks method
    @Test
    @DisplayName("Test getWorkerTasks method success")
    public void testGetWorkerTasksSuccess() {
        Task task1 = mock(Task.class);

        Task task2 = mock(Task.class);

        List<Task> tasks = List.of(task1, task2);
        when(taskDao.getTasksForWorker(workerMock.getId())).thenReturn(tasks);

        List<Task> result = workerService.getWorkerTasks(workerMock.getId());

        assertNotNull(result);
        assertFalse(result.isEmpty(), "The returned task collection should not be empty.");
        assertEquals(2, result.size(), "The returned task list should contain two tasks.");
        assertTrue(result.contains(task1), "The result should contain task1.");
        assertTrue(result.contains(task2), "The result should contain task2.");
    }

    @Test
    @DisplayName("Test getWorkerTasks method success with no tasks found")
    void testGetWorkerTasksNoTasksFound() {
        long workerId = workerMock.getId();
        List<Task> expectedTasks = List.of();

        when(taskDao.getTasksForWorker(workerId)).thenReturn(expectedTasks);

        Collection<Task> actualTasks = workerService.getWorkerTasks(workerId);

        assertNotNull(actualTasks);
        assertTrue(actualTasks.isEmpty());
        assertEquals(0, actualTasks.size());

        verify(taskDao, times(1)).getTasksForWorker(workerId);
        verifyNoMoreInteractions(taskDao);
    }

    // Test for changeTaskStatus method
    @Test
    @DisplayName("Test changeTaskStatus method success")
    public void testChangeTaskStatusSuccess() {
        Task task = new Task(1L, workerMock, adminMock, MaintenanceStatus.Assigned, Instant.now(),
                maintenanceReportMock);
        MaintenanceStatus newStatus = MaintenanceStatus.InProgress;

        when(taskDao.getTaskById(task.getId())).thenReturn(task);

        workerService.changeTaskStatus(task.getId(), newStatus);

        assertEquals(newStatus, task.getStatus(), "The task status should be updated.");
        verify(taskDao, times(1)).updateTask(task);
    }

    @Test
    @DisplayName("Test changeTaskStatus method with task not found")
    public void testChangeTaskStatusWithTaskNotFound() {
        long taskId = 1L;
        MaintenanceStatus newStatus = MaintenanceStatus.InProgress;

        when(taskDao.getTaskById(taskId)).thenReturn(null);

        try {
            workerService.changeTaskStatus(taskId, newStatus);
        } catch (IllegalArgumentException e) {
            assertEquals("Task not found", e.getMessage());
        }

        verify(taskDao, times(1)).getTaskById(taskId);
        verifyNoMoreInteractions(taskDao);
    }

    @Test
    @DisplayName("Test changeTaskStatus method with null status")
    public void testChangeTaskStatusWithNullStatus() {
        Task task = new Task(1L, workerMock, adminMock, MaintenanceStatus.Assigned, Instant.now(),
                maintenanceReportMock);

        when(taskDao.getTaskById(task.getId())).thenReturn(task);

        try {
            workerService.changeTaskStatus(task.getId(), null);
        } catch (IllegalArgumentException e) {
            assertEquals("Status cannot be null", e.getMessage());
        }

        verify(taskDao, times(1)).getTaskById(task.getId());
        verifyNoMoreInteractions(taskDao);
    }

    @Test
    @DisplayName("Test changeTaskStatus method with completed task")
    public void testChangeTaskStatusWithCompletedTask() {
        Task task = new Task(1L, workerMock, adminMock, MaintenanceStatus.Completed, Instant.now(),
                maintenanceReportMock);

        when(taskDao.getTaskById(task.getId())).thenReturn(task);

        try {
            workerService.changeTaskStatus(task.getId(), MaintenanceStatus.InProgress);
        } catch (IllegalStateException e) {
            assertEquals("Cannot change status of a completed task", e.getMessage());
        }

        verify(taskDao, times(1)).getTaskById(task.getId());
        verifyNoMoreInteractions(taskDao);
    }

    @Test
    @DisplayName("createWorkerFromUser: crea Worker da User valido")
    void testCreateWorkerFromUser() {
        // Arrange
        User user = new User(42L, "worker@javabrew.it", "Test", "Worker", "pw");
        Worker expected = new Worker(user);

        WorkerDaoImpl workerDaoMock = mock(WorkerDaoImpl.class);
        WorkerService localWorkerService = new WorkerService(workerDaoMock, taskDao);

        when(workerDaoMock.createWorker(org.mockito.ArgumentMatchers.any(Worker.class))).thenReturn(expected);

        // Act
        Worker created = localWorkerService.createWorkerFromUser(user);

        // Assert
        assertNotNull(created);
        assertEquals(user, created.getUser());
        assertEquals(User.Role.Worker, created.getUser().getRole());
        assertTrue(created.isActive());

        verify(workerDaoMock, times(1)).createWorker(org.mockito.ArgumentMatchers.any(Worker.class));
    }

    @Test
    @DisplayName("createWorkerFromUser: se User null -> IllegalArgumentException")
    void testCreateWorkerFromUserNull() {
        WorkerDaoImpl workerDaoMock = mock(WorkerDaoImpl.class);
        WorkerService localWorkerService = new WorkerService(workerDaoMock, taskDao);

        assertThrows(IllegalArgumentException.class, () -> {
            localWorkerService.createWorkerFromUser(null);
        });

        verify(workerDaoMock, org.mockito.Mockito.never()).createWorker(org.mockito.ArgumentMatchers.any());
    }


    
}