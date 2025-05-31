package com.smartvend.app.controllers;

import com.smartvend.app.model.maintenance.MaintenanceStatus;
import com.smartvend.app.model.maintenance.Task;
import com.smartvend.app.model.user.Worker;
import com.smartvend.app.services.TaskService;
import com.smartvend.app.services.UserService;
import com.smartvend.app.services.WorkerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WorkerControllerTest {

    private UserService userService;
    private WorkerService workerService;
    private TaskService taskService;
    private WorkerController workerController;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        workerService = mock(WorkerService.class);
        taskService = mock(TaskService.class);
        workerController = new WorkerController(userService, workerService, taskService);
    }

    @Test
    void constructor_throwsException_whenWorkerServiceIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new WorkerController(userService, null, taskService));
    }

    @Test
    void getTasks_returnsTasks_whenWorkerIdIsValid() {
        long workerId = 1L;
        Task task1 = mock(Task.class);
        Task task2 = mock(Task.class);
        List<Task> tasks = Arrays.asList(task1, task2);

        when(workerService.getWorkerTasks(workerId)).thenReturn(tasks);

        List<Task> result = workerController.getTasks(workerId);

        assertEquals(tasks, result);
        verify(workerService).getWorkerTasks(workerId);
    }

    @Test
    void getTasks_throwsException_whenWorkerIdIsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> workerController.getTasks(0));
        assertThrows(IllegalArgumentException.class, () -> workerController.getTasks(-5));
    }

    @Test
    void getTaskDetails_returnsDetails_whenTaskIdIsValid() {
        long taskId = 2L;
        String details = "Task ID: 2\nDescription: Test Task\nStatus: InProgress\nAssigned Worker: John Doe\n";
        Task mockTask = mock(Task.class);
        Worker mockWorker = mock(Worker.class);

        when(mockTask.getId()).thenReturn(2L);
        when(mockTask.getDescription()).thenReturn("Test Task");
        when(mockTask.getStatus()).thenReturn(MaintenanceStatus.InProgress);
        when(taskService.getTaskDetails(taskId)).thenReturn(details);
        when(taskService.getTaskDetails(taskId)).thenReturn(details);
        when(mockWorker.getFullName()).thenReturn("John Doe");
        when(mockTask.getWorker()).thenReturn(mockWorker);
        when(taskService.getTaskDetails(taskId)).thenReturn(details);
        when(taskService.getTaskDetails(taskId)).thenReturn(details);

        Object result = workerController.getTaskDetails(taskId);

        assertEquals(details, result);
        verify(taskService).getTaskDetails(taskId);
    }

    @Test
    void getTaskDetails_throwsException_whenTaskIdIsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> workerController.getTaskDetails(0));
        assertThrows(IllegalArgumentException.class, () -> workerController.getTaskDetails(-10));
    }

    @Test
    void taskCompleted_returnsTrue_whenTaskIsCompletedSuccessfully() {
        long taskId = 3L;
        when(workerService.changeTaskStatus(taskId, MaintenanceStatus.Completed)).thenReturn(true);

        boolean result = workerController.taskCompleted(taskId);

        assertTrue(result);
        verify(workerService).changeTaskStatus(taskId, MaintenanceStatus.Completed);
    }

    @Test
    void taskCompleted_returnsFalse_whenTaskCompletionFails() {
        long taskId = 4L;
        when(workerService.changeTaskStatus(taskId, MaintenanceStatus.Completed)).thenReturn(false);

        boolean result = workerController.taskCompleted(taskId);

        assertFalse(result);
        verify(workerService).changeTaskStatus(taskId, MaintenanceStatus.Completed);
    }

    @Test
    void taskCompleted_throwsException_whenTaskIdIsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> workerController.taskCompleted(0));
        assertThrows(IllegalArgumentException.class, () -> workerController.taskCompleted(-1));
    }
}
