package com.smartvend.app.dao;

import com.smartvend.app.dao.impl.ConcreteVendingMachineDaoImpl;
import com.smartvend.app.model.vendingmachine.ConcreteVendingMachine;
import com.smartvend.app.model.vendingmachine.MachineStatus;
import jakarta.persistence.*;
import org.junit.jupiter.api.*;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ConcreteVendingMachineDaoImplTest {

    // ──────────────────────────────── UNIT TESTS (Mockito) ────────────────────────────────
    private EntityManagerFactory emfMock;
    private EntityManager emMock;
    private EntityTransaction txMock;
    private ConcreteVendingMachineDaoImpl dao;

    @Nested
    class Unit {
        @BeforeEach
        void init() {
            emfMock = mock(EntityManagerFactory.class);
            emMock  = mock(EntityManager.class);
            txMock  = mock(EntityTransaction.class);

            when(emfMock.createEntityManager()).thenReturn(emMock);
            when(emMock.getTransaction()).thenReturn(txMock);

            dao = new ConcreteVendingMachineDaoImpl(emfMock);
        }

        @Test  void findAll_empty() {
            @SuppressWarnings("unchecked")
            TypedQuery<ConcreteVendingMachine> q = (TypedQuery<ConcreteVendingMachine>) mock(TypedQuery.class);
            when(emMock.createQuery(anyString(), eq(ConcreteVendingMachine.class))).thenReturn(q);
            when(q.getResultList()).thenReturn(Collections.emptyList());

            List<ConcreteVendingMachine> res = dao.findAll();
            assertTrue(res.isEmpty());
            verify(emMock).close();
        }

        @Test  void findAll_oneItem() {
            @SuppressWarnings("unchecked")
            TypedQuery<ConcreteVendingMachine> q = (TypedQuery<ConcreteVendingMachine>) mock(TypedQuery.class);
            ConcreteVendingMachine m = mock(ConcreteVendingMachine.class);
            when(emMock.createQuery(anyString(), eq(ConcreteVendingMachine.class))).thenReturn(q);
            when(q.getResultList()).thenReturn(List.of(m));

            List<ConcreteVendingMachine> res = dao.findAll();
            assertEquals(1, res.size());
            assertSame(m, res.get(0));
            verify(emMock).close();
        }

        @Test  void findById_found() {
            ConcreteVendingMachine m = mock(ConcreteVendingMachine.class);
            when(emMock.find(ConcreteVendingMachine.class, "ID123")).thenReturn(m);

            assertSame(m, dao.findById("ID123"));
            verify(emMock).close();
        }

        @Test  void findById_notFound() {
            when(emMock.find(ConcreteVendingMachine.class, "ID999")).thenReturn(null);

            assertNull(dao.findById("ID999"));
            verify(emMock).close();
        }

        @Test  void createMachine_persists() {
            ConcreteVendingMachine m = mock(ConcreteVendingMachine.class);
            ConcreteVendingMachine res = dao.createMachine(m);

            verify(txMock).begin();
            verify(emMock).persist(m);
            verify(txMock).commit();
            verify(emMock).close();
            assertSame(m, res);
        }

        @Test  void updateMachine_merges() {
            ConcreteVendingMachine m = mock(ConcreteVendingMachine.class);
            ConcreteVendingMachine merged = mock(ConcreteVendingMachine.class);
            when(emMock.merge(m)).thenReturn(merged);

            ConcreteVendingMachine res = dao.updateMachine(m);

            verify(txMock).begin();
            verify(emMock).merge(m);
            verify(txMock).commit();
            verify(emMock).close();
            assertSame(merged, res);
        }

        @Test  void deleteMachine_exists() {
            ConcreteVendingMachine m = mock(ConcreteVendingMachine.class);
            when(emMock.find(ConcreteVendingMachine.class, "DEL")).thenReturn(m);

            dao.deleteMachine("DEL");

            verify(txMock).begin();
            verify(emMock).remove(m);
            verify(txMock).commit();
            verify(emMock).close();
        }

        @Test  void deleteMachine_notExists() {
            when(emMock.find(ConcreteVendingMachine.class, "NONE")).thenReturn(null);

            dao.deleteMachine("NONE");

            verify(txMock).begin();
            verify(emMock, never()).remove(any());
            verify(txMock).commit();
            verify(emMock).close();
        }

        @Test  void findByStatus() {
            @SuppressWarnings("unchecked")
            TypedQuery<ConcreteVendingMachine> q = (TypedQuery<ConcreteVendingMachine>) mock(TypedQuery.class);
            when(emMock.createQuery(contains("WHERE v.status = :status"), eq(ConcreteVendingMachine.class))).thenReturn(q);
            when(q.setParameter(eq("status"), any())).thenReturn(q);
            when(q.getResultList()).thenReturn(List.of(mock(ConcreteVendingMachine.class)));

            assertFalse(dao.findByStatus(MachineStatus.Operative).isEmpty());
            verify(emMock).close();
        }

        @Test  void findByLocation() {
            @SuppressWarnings("unchecked")
            TypedQuery<ConcreteVendingMachine> q = (TypedQuery<ConcreteVendingMachine>) mock(TypedQuery.class);
            when(emMock.createQuery(contains("WHERE v.location = :location"), eq(ConcreteVendingMachine.class))).thenReturn(q);
            when(q.setParameter(eq("location"), any())).thenReturn(q);
            when(q.getResultList()).thenReturn(List.of(mock(ConcreteVendingMachine.class)));

            assertFalse(dao.findByLocation("TestPlace").isEmpty());
            verify(emMock).close();
        }
    }

    // ──────────────────────────────── INTEGRATION TESTS (H2) ────────────────────────────────
    private static EntityManagerFactory emf;
    private ConcreteVendingMachineDaoImpl intDao;

    @BeforeAll
    static void startPU() {
        emf = Persistence.createEntityManagerFactory("test-pu");
    }

    @AfterAll
    static void closePU() {
        if (emf != null) emf.close();
    }

    @BeforeEach
    void initDao() {
        intDao = new ConcreteVendingMachineDaoImpl(emf);
    }

    @Test
    void integration_CRUD_andQueries() {
        // CREATE
        ConcreteVendingMachine m = new ConcreteVendingMachine();
        m.setSerialNumber("INTEG-1");
        m.setLocation("Rome");
        m.setCapacity(50);
        m.setLastMaintenance(Instant.now());
        m.setCreatedAt(Instant.now());
        m.setStatus(MachineStatus.Operative);

        intDao.createMachine(m);

        // READ
        ConcreteVendingMachine loaded = intDao.findById("INTEG-1");
        assertNotNull(loaded);
        assertEquals("Rome", loaded.getLocation());

        // UPDATE
        loaded.setLocation("Milan");
        intDao.updateMachine(loaded);
        assertEquals("Milan", intDao.findById("INTEG-1").getLocation());

        // LIST & QUERIES
        assertFalse(intDao.findAll().isEmpty());
        assertFalse(intDao.findByStatus(MachineStatus.Operative).isEmpty());
        assertFalse(intDao.findByLocation("Milan").isEmpty());

        // DELETE
        intDao.deleteMachine("INTEG-1");
        assertNull(intDao.findById("INTEG-1"));
    }
}
