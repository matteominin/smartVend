package com.smartvend.app.dao.impl;

import com.smartvend.app.dao.TaskDao;
import com.smartvend.app.model.maintenance.Task;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class TaskDaoImpl implements TaskDao {

    private final EntityManagerFactory emf;

    public TaskDaoImpl(EntityManagerFactory entityManagerFactory) {
        this.emf = entityManagerFactory;
    }

    @Override
    public Task getTaskById(long taskId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Task.class, taskId);
        } finally {
            em.close();
        }
    }

    @Override
    public Task createTask(Task task) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(task);
            em.getTransaction().commit();
            return task;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Task updateTask(Task task) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Task merged = em.merge(task);
            em.getTransaction().commit();
            return merged;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Task> getTasksForWorker(long workerId) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Task> query = em.createQuery(
                    "SELECT t FROM Task t WHERE t.worker.id = :workerId", Task.class);
            query.setParameter("workerId", workerId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
