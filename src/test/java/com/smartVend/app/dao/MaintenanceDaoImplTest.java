package com.smartvend.app.dao;

import com.smartvend.app.dao.impl.MaintenanceDaoImpl;
import com.smartvend.app.model.maintenance.MaintenanceReport;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class MaintenanceDaoImplTest {

    @Mock
    private EntityManagerFactory entityManagerFactory;
    @Mock
    private EntityManager entityManager;
    @Mock
    private EntityTransaction transaction;

    @InjectMocks
    private MaintenanceDaoImpl maintenanceDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
        when(entityManager.getTransaction()).thenReturn(transaction);
    }

    @Test
    void createReport_persistsReport() {
        MaintenanceReport report = mock(MaintenanceReport.class);
        maintenanceDao.createReport(report);
        verify(entityManager).persist(report);
    }
}
