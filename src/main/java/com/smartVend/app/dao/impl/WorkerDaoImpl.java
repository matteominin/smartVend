package com.smartvend.app.dao.impl;

import com.smartvend.app.dao.WorkerDao;
import com.smartvend.app.model.user.Worker;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

public class WorkerDaoImpl implements WorkerDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Worker getWorkerById(String workerId) {
        return entityManager.find(Worker.class, workerId);
    }

    @Override
    public List<Worker> findAllWorkers() {
        return entityManager.createQuery(
                "SELECT w FROM Worker w", Worker.class)
                .getResultList();
    }

    @Override
    public Worker getWorkerByEmail(String email) {
        List<Worker> results = entityManager.createQuery(
                "SELECT w FROM Worker w WHERE w.email = :email", Worker.class)
                .setParameter("email", email)
                .getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public Worker createWorker(Worker worker) {
        entityManager.persist(worker);
        return worker;
    }

    @Override
    public Worker updateWorker(Worker worker) {
        return entityManager.merge(worker);
    }

    @Override
    public void deleteWorker(Long id) {
        Worker worker = entityManager.find(Worker.class, id);
        if (worker != null) {
            entityManager.remove(worker);
        }
    }
}