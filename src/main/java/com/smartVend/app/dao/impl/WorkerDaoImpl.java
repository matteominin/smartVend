package com.smartvend.app.dao.impl;

import com.smartvend.app.dao.WorkerDao;
import com.smartvend.app.model.user.Worker;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class WorkerDaoImpl implements WorkerDao {
    private final EntityManagerFactory emf;

    public WorkerDaoImpl(EntityManagerFactory entityManagerFactory) {
        this.emf = entityManagerFactory;
    }

    @Override
    public Worker getWorkerById(Long workerId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Worker.class, workerId);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Worker> findAllWorkers() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT w FROM Worker w", Worker.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Worker getWorkerByEmail(String email) {
        EntityManager em = emf.createEntityManager();
        try {
            List<Worker> results = em.createQuery(
                    "SELECT w FROM Worker w WHERE w.email = :email", Worker.class)
                    .setParameter("email", email)
                    .getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
    }

    @Override
    public Worker createWorker(Worker worker) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(worker);
            em.getTransaction().commit();
            return worker;
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
    public Worker updateWorker(Worker worker) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Worker merged = em.merge(worker);
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
    public void deleteWorker(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Worker worker = em.find(Worker.class, id);
            if (worker != null) {
                em.remove(worker);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}