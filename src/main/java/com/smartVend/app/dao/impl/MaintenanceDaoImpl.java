package com.smartvend.app.dao.impl;

import com.smartvend.app.dao.MaintenanceDao;
import com.smartvend.app.model.maintenance.MaintenanceReport;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

public class MaintenanceDaoImpl implements MaintenanceDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public MaintenanceReport createReport(MaintenanceReport report) {
        entityManager.persist(report);
        return report;
    }
}
