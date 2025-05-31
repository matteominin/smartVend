package com.smartvend.app.dao.impl;

import java.util.List;

import com.smartvend.app.dao.TaskDao;
import com.smartvend.app.model.maintenance.Task;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

public class TaskDaoImpl implements TaskDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Task getTaskById(long taskId) {
        return entityManager.find(Task.class, Long.valueOf(taskId));
    }

    @Override
    public List<Task> getTasksForWorker(long workerId) {
        return entityManager.createQuery(
                "SELECT t FROM Task t WHERE t.worker.id = :workerId", Task.class)
                .setParameter("workerId", workerId)
                .getResultList();
    }

    @Override
    @Transactional
    public Task updateTask(Task task) {
        return entityManager.merge(task);
    }

    @Override
    @Transactional
    public Task createTask(Task task) {
        entityManager.persist(task);
        return task;
    }
}
