package com.smartvend.app.dao;

import java.util.List;

import com.smartvend.app.model.maintenance.Task;

public interface TaskDao {
    Task getTaskById(long taskId);

    List<Task> getTasksForWorker(long workerId);

    void updateTask(Task task);
}
