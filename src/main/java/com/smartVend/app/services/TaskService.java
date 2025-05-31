package com.smartvend.app.services;

import com.smartvend.app.model.maintenance.Task;
import com.smartvend.app.dao.impl.TaskDaoImpl;

public class TaskService {
    TaskDaoImpl taskDao;

    public TaskService(TaskDaoImpl taskDao) {
        if (taskDao == null) {
            throw new IllegalArgumentException("TaskDao cannot be null");
        }
        this.taskDao = taskDao;
    }

    public String getTaskDetails(Long taskId) {
        Task task = taskDao.getTaskById(taskId);

        if (task == null) {
            throw new IllegalArgumentException("Task not found");
        }

        StringBuilder details = new StringBuilder();
        details.append("Task ID: ").append(task.getId()).append("\n");
        details.append("Description: ").append(task.getDescription()).append("\n");
        details.append("Status: ").append(task.getStatus()).append("\n");
        details.append("Assigned Worker: ")
                .append(task.getWorker() != null ? task.getWorker().getFullName() : "Not assigned")
                .append("\n");

        return details.toString();
    }
}
