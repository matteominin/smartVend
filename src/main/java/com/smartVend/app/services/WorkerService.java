package com.smartvend.app.services;

import java.util.List;

import com.smartvend.app.dao.impl.TaskDaoImpl;
import com.smartvend.app.dao.impl.WorkerDaoImpl;
import com.smartvend.app.model.maintenance.MaintenanceStatus;
import com.smartvend.app.model.maintenance.Task;
import com.smartvend.app.model.user.User;
import com.smartvend.app.model.user.Worker;

public class WorkerService {
    private WorkerDaoImpl workerDao;
    private TaskDaoImpl taskDao;
    private UserService userService;

    public WorkerService(WorkerDaoImpl workerDao, UserService userService, TaskDaoImpl taskDao) {
        this.userService = userService;
        this.workerDao = workerDao;
        this.taskDao = taskDao;
    }

    public Worker logIn(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        Worker worker = workerDao.getWorkerByEmail(email);
        if (worker == null) {
            throw new IllegalArgumentException("Worker not found with email: " + email);
        }
        if (!worker.getUser().getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid password for worker with email: " + email);
        }
        return worker;
    }

    public Worker signUp(String email, String name, String surname, String password) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (workerDao.getWorkerByEmail(email) != null) {
            throw new IllegalArgumentException("A worker with this email already exists");
        }
        User user = userService.signUp(email, name, surname, password);
        if (user == null) {
            throw new IllegalArgumentException("Failed to create user for worker");
        }

        Worker worker = new Worker(user);
        return workerDao.createWorker(worker);
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
