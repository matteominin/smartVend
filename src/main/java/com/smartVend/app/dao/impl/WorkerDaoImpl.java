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
    public List<Worker> findAllActive() {
        return entityManager.createQuery(
                "SELECT w FROM Worker w WHERE w.isActive = true", Worker.class)
                .getResultList();
    }
}
