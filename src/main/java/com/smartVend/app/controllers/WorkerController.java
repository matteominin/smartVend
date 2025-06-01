package com.smartvend.app.controllers;

import java.util.List;

import com.smartvend.app.model.maintenance.MaintenanceStatus;
import com.smartvend.app.model.maintenance.Task;
import com.smartvend.app.model.user.Worker;
import com.smartvend.app.services.TaskService;
import com.smartvend.app.services.WorkerService;

public class WorkerController {
    private WorkerService workerService;
    private TaskService taskService;

    public WorkerController(WorkerService workerService, TaskService taskService) {
        if (workerService == null) {
            throw new IllegalArgumentException("WorkerService cannot be null");
        }
        this.workerService = workerService;
        this.taskService = taskService;
    }

    public Worker login(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return workerService.logIn(email, password);
    }

    public Worker signUp(Worker worker) {
        if (worker == null) {
            throw new IllegalArgumentException("Worker cannot be null");
        }
        return workerService.signUp(worker.getUser().getEmail(), worker.getUser().getName(),
                worker.getUser().getSurname(), worker.getUser().getPassword());
    }

    public List<Task> getTasks(long workerId) {
        if (workerId <= 0) {
            throw new IllegalArgumentException("Worker ID must be positive");
        }
        return workerService.getWorkerTasks(workerId);
    }

    public Object getTaskDetails(long taskId) {
        if (taskId <= 0) {
            throw new IllegalArgumentException("Task ID must be positive");
        }
        return taskService.getTaskDetails(taskId);
    }

    public boolean taskCompleted(long taskId) {
        if (taskId <= 0) {
            throw new IllegalArgumentException("Task ID must be positive");
        }
        return workerService.changeTaskStatus(taskId, MaintenanceStatus.Completed);
    }
}