package com.smartvend.app.dao;

import java.util.List;

import com.smartvend.app.model.maintenance.Task;

public interface TaskDao {
    Task getTaskById(String taskId);
    List<Task> getTasksForWorker(String workerId);
    void updateTask(Task task);
}
