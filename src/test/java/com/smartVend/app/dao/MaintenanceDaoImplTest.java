package com.smartvend.app.dao;

import com.smartvend.app.dao.impl.MaintenanceDaoImpl;
import com.smartvend.app.model.maintenance.MaintenanceReport;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class MaintenanceDaoImplTest {

    private EntityManager entityManager;
    private MaintenanceDaoImpl maintenanceDao;

    @BeforeEach
    void setUp() {
        entityManager = mock(EntityManager.class);
        maintenanceDao = new MaintenanceDaoImpl();
        // Per test, iniettiamo l'entityManager con reflection o setter (dipende da come lo gestisci)
        // Qui esempio semplice con reflection (solo per test):
        try {
            var field = MaintenanceDaoImpl.class.getDeclaredField("entityManager");
            field.setAccessible(true);
            field.set(maintenanceDao, entityManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createReport_persistsReport() {
        MaintenanceReport report = mock(MaintenanceReport.class);
        maintenanceDao.createReport(report);
        verify(entityManager).persist(report);
    }
}
