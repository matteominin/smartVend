package com.smartvend.app.dao.impl;

import com.smartvend.app.dao.MaintenanceDao;
import com.smartvend.app.model.maintenance.MaintenanceReport;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class MaintenanceDaoImpl implements MaintenanceDao {

    private final EntityManagerFactory emf;

    public MaintenanceDaoImpl(EntityManagerFactory entityManagerFactory) {
        this.emf = entityManagerFactory;
    }

    @Override
    public MaintenanceReport createReport(MaintenanceReport report) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(report);
            em.getTransaction().commit();
            return report;
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
