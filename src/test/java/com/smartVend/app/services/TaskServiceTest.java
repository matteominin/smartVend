package com.smartvend.app.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.smartvend.app.dao.impl.TaskDaoImpl;
import com.smartvend.app.model.maintenance.MaintenanceStatus;
import com.smartvend.app.model.maintenance.Task;
import com.smartvend.app.model.user.Worker;

class TaskServiceTest {

    private TaskDaoImpl taskDao;
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskDao = mock(TaskDaoImpl.class);
        taskService = new TaskService(taskDao);
    }

    @Test
    void constructor_shouldThrowException_whenTaskDaoIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new TaskService(null));
    }

    @Test
    void getTaskDetails_shouldReturnDetails_whenTaskExistsAndWorkerAssigned() {
        Task task = mock(Task.class);
        Worker worker = mock(Worker.class);

        when(taskDao.getTaskById(1L)).thenReturn(task);
        when(task.getId()).thenReturn(1L);
        when(task.getDescription()).thenReturn("Fix vending machine");
        when(task.getStatus()).thenReturn(MaintenanceStatus.Assigned);
        when(task.getWorker()).thenReturn(worker);
        when(worker.getFullName()).thenReturn("John Doe");

        String details = taskService.getTaskDetails(1L);

        assertTrue(details.contains("Task ID: 1"));
        assertTrue(details.contains("Description: Fix vending machine"));
        assertTrue(details.contains("Status: Assigned"));
        assertTrue(details.contains("Assigned Worker: John Doe"));
    }

    @Test
    void getTaskDetails_shouldReturnDetails_whenTaskExistsAndWorkerNotAssigned() {
        Task task = mock(Task.class);

        when(taskDao.getTaskById(2L)).thenReturn(task);
        when(task.getId()).thenReturn(2L);
        when(task.getDescription()).thenReturn("Clean machine");
        when(task.getStatus()).thenReturn(MaintenanceStatus.InProgress);
        when(task.getWorker()).thenReturn(null);

        String details = taskService.getTaskDetails(2L);

        assertTrue(details.contains("Task ID: 2"));
        assertTrue(details.contains("Description: Clean machine"));
        assertTrue(details.contains("Status: InProgress"));
        assertTrue(details.contains("Assigned Worker: Not assigned"));
    }

    @Test
    void getTaskDetails_shouldThrowException_whenTaskNotFound() {
        when(taskDao.getTaskById(3L)).thenReturn(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> taskService.getTaskDetails(3L));
        assertEquals("Task not found", exception.getMessage());
    }
}
