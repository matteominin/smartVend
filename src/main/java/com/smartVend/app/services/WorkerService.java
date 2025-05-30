package com.smartvend.app.services;

import java.util.List;
import com.smartvend.app.model.maintenance.MaintenanceStatus;
import com.smartvend.app.dao.TaskDao;
import com.smartvend.app.model.maintenance.Task;

public class WorkerService {
    private TaskDao taskDao;

    WorkerService(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    public List<Task> getWorkerTasks(long workerId) {
        return taskDao.getTasksForWorker(workerId);
    }

    public void changeTaskStatus(long taskId, MaintenanceStatus status) {
        Task task = taskDao.getTaskById(taskId);

        if (task == null) {
            throw new IllegalArgumentException("Task not found");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        if (task.status == MaintenanceStatus.Completed) {
            throw new IllegalStateException("Cannot change status of a completed task");
        }

        task.setStatus(status);
        taskDao.updateTask(task);
    }
}
