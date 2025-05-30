package com.smartvend.app.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.smartvend.app.model.maintenance.MaintenanceReport;
import com.smartvend.app.model.maintenance.MaintenanceStatus;
import com.smartvend.app.dao.TaskDao;
import com.smartvend.app.model.vendingmachine.MachineType;
import com.smartvend.app.model.vendingmachine.VendingMachine;
import com.smartvend.app.model.user.*;
import com.smartvend.app.model.maintenance.Task;

public class WorkerServiceTest {
    @Mock
    private TaskDao taskDao;

    @InjectMocks
    private WorkerService workerService;

    private Worker workerMock;
    private Admin adminMock;
    private VendingMachine vendingMachineMock;
    private MaintenanceReport maintenanceReportMock;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        workerMock = new Worker(
                new User(1L, "email1@example.com", "workerName", "workerSurname", "workerPassword"));
        adminMock = new Admin(
                new User(2L, "email2@example.com", "adminName", "adminSurname", "adminPassword"));
        vendingMachineMock = new VendingMachine("123", MachineType.Snack);
        maintenanceReportMock = new MaintenanceReport("Test issue", Instant.now(), vendingMachineMock);
    }

    // Test for getWorkerTasks method
    @Test
    @DisplayName("Test getWorkerTasks method success")
    public void testGetWorkerTasksSuccess() {
        Task task1 = new Task(workerMock, adminMock, MaintenanceStatus.Assigned, Instant.now(),
                maintenanceReportMock);

        Task task2 = new Task(workerMock, adminMock, MaintenanceStatus.InProgress, Instant.now().plusSeconds(3600),
                maintenanceReportMock);

        List<Task> tasks = List.of(task1, task2);
        when(taskDao.getWorkerTasks(workerMock.getId())).thenReturn(tasks);

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

        when(taskDao.getWorkerTasks(workerId)).thenReturn(expectedTasks);

        Collection<Task> actualTasks = workerService.getWorkerTasks(workerId);

        assertNotNull(actualTasks);
        assertTrue(actualTasks.isEmpty());
        assertEquals(0, actualTasks.size());

        verify(taskDao, times(1)).getWorkerTasks(workerId);
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
}