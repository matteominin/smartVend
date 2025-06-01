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

    public Worker getWorker(long workerId) {
        if (workerId <= 0) {
            throw new IllegalArgumentException("Worker ID must be positive");
        }
        return workerService.getWorkerById(workerId);
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