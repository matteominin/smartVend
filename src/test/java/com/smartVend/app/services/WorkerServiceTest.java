package com.smartvend.app.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.Instant;
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

}