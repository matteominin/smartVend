package com.smartvend.app.services;

import java.util.List;

import com.smartvend.app.dao.impl.TaskDaoImpl;
import com.smartvend.app.dao.impl.WorkerDaoImpl;
import com.smartvend.app.model.maintenance.MaintenanceStatus;
import com.smartvend.app.model.maintenance.Task;
import com.smartvend.app.model.user.Worker;

public class WorkerService {
    private TaskDaoImpl taskDao;
    private WorkerDaoImpl workerDao;

    public WorkerService(WorkerDaoImpl workerDao, TaskDaoImpl taskDao) {
        this.taskDao = taskDao;
        this.workerDao = workerDao;
    }

    public Worker getWorkerById(long workerId) {
        Worker worker = workerDao.getWorkerById(workerId);
        if (worker == null) {
            throw new IllegalArgumentException("Worker not found");
        }
        return worker;
    }

    public List<Task> getWorkerTasks(long workerId) {
        return taskDao.getTasksForWorker(workerId);
    }

    public boolean changeTaskStatus(long taskId, MaintenanceStatus status) {
        Task task = taskDao.getTaskById(taskId);

        if (task == null) {
            throw new IllegalArgumentException("Task not found");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        if (task.getStatus() == MaintenanceStatus.Completed) {
            throw new IllegalStateException("Cannot change status of a completed task");
        }

        task.setStatus(status);
        Task updatedTask = taskDao.updateTask(task);
        return updatedTask != null;
    }
}
