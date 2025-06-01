package com.smartvend.app.dao;

import com.smartvend.app.dao.impl.MaintenanceDaoImpl;
import com.smartvend.app.model.maintenance.MaintenanceReport;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MaintenanceDaoImplTest {

    /*──────────────────────────── UNIT TESTS (Mockito) ───────────────────────────*/
    @Nested
    class Unit {

        @Mock EntityManagerFactory emf;
        @Mock EntityManager em;
        @Mock EntityTransaction tx;
        @InjectMocks MaintenanceDaoImpl dao;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
            when(emf.createEntityManager()).thenReturn(em);
            when(em.getTransaction()).thenReturn(tx);
        }

        @Test
        void createReport_persistsAndReturns() {
            MaintenanceReport report = new MaintenanceReport("Test issue", Instant.now(), null);
            doNothing().when(em).persist(report);
            when(tx.isActive()).thenReturn(false);

            MaintenanceReport result = dao.createReport(report);

            verify(em).persist(report);
            verify(tx).begin();
            verify(tx).commit();
            assertSame(report, result);
        }

        @Test
        void createReport_rollsBackOnException() {
            MaintenanceReport report = new MaintenanceReport("Test rollback", Instant.now(), null);
            doThrow(new RuntimeException("fail")).when(em).persist(report);
            when(tx.isActive()).thenReturn(true);

            assertThrows(RuntimeException.class, () -> dao.createReport(report));
            verify(tx).rollback();
        }
    }

    /*───────────────────────── INTEGRATION TESTS (H2 DB) ─────────────────────────*/
    @Nested
    class Integration {

        private static EntityManagerFactory emf;
        private MaintenanceDaoImpl dao;

        @BeforeAll
        static void startPU() { emf = Persistence.createEntityManagerFactory("test-pu"); }

        @AfterAll
        static void stopPU() { if (emf != null) emf.close(); }

        @BeforeEach
        void setup() { dao = new MaintenanceDaoImpl(emf); }

        @Test
        void integration_createReport_persistsAndFinds() {
            MaintenanceReport report = new MaintenanceReport("Macchina rotta", Instant.now(), null);

            dao.createReport(report);

            assertNotNull(report.getId());

            // Recupera dal database per verifica
            EntityManager em = emf.createEntityManager();
            MaintenanceReport found = em.find(MaintenanceReport.class, report.getId());
            em.close();

            assertNotNull(found);
            assertEquals("Macchina rotta", found.getIssueDescription());
        }
    }
}
