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
        // Assumiamo che Task abbia: @ManyToOne Worker worker -> worker.id
        return entityManager.createQuery(
                "SELECT t FROM Task t WHERE t.worker.id = :workerId", Task.class)
                .setParameter("workerId", workerId)
                .getResultList();
    }

    @Override
    @Transactional
    public void updateTask(Task task) {
        entityManager.merge(task);
    }
}
