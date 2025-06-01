package com.smartvend.app.dao;

import com.smartvend.app.dao.impl.ConcreteVendingMachineDaoImpl;
import com.smartvend.app.model.vendingmachine.ConcreteVendingMachine;
import com.smartvend.app.model.vendingmachine.MachineStatus;
import jakarta.persistence.*;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ConcreteVendingMachineDaoImplTest {

    // ─────────────── UNIT TESTS (Mockito) ───────────────
    @Mock
    private EntityManagerFactory entityManagerFactory;
    @Mock
    private EntityManager entityManager;
    @Mock
    private EntityTransaction transaction;

    @InjectMocks
    private ConcreteVendingMachineDaoImpl vendingMachineDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
        when(entityManager.getTransaction()).thenReturn(transaction);

        vendingMachineDao = new ConcreteVendingMachineDaoImpl(entityManagerFactory);
    }

    @Test void findAll_empty() {
        @SuppressWarnings("unchecked")
        TypedQuery<ConcreteVendingMachine> q = (TypedQuery<ConcreteVendingMachine>) mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(ConcreteVendingMachine.class))).thenReturn(q);
        when(q.getResultList()).thenReturn(Collections.emptyList());

        List<ConcreteVendingMachine> res = vendingMachineDao.findAll();
        assertTrue(res.isEmpty());
        verify(entityManager).close();
    }

    @Test void findAll_oneItem() {
        @SuppressWarnings("unchecked")
        TypedQuery<ConcreteVendingMachine> q = (TypedQuery<ConcreteVendingMachine>) mock(TypedQuery.class);
        ConcreteVendingMachine m = mock(ConcreteVendingMachine.class);
        when(entityManager.createQuery(anyString(), eq(ConcreteVendingMachine.class))).thenReturn(q);
        when(q.getResultList()).thenReturn(List.of(m));

        List<ConcreteVendingMachine> res = vendingMachineDao.findAll();
        assertEquals(1, res.size());
        assertSame(m, res.get(0));
        verify(entityManager).close();
    }

    @Test void findById_found() {
        ConcreteVendingMachine m = mock(ConcreteVendingMachine.class);
        when(entityManager.find(ConcreteVendingMachine.class, "ID123")).thenReturn(m);

        assertSame(m, vendingMachineDao.findById("ID123"));
        verify(entityManager).close();
    }

    @Test void findById_notFound() {
        when(entityManager.find(ConcreteVendingMachine.class, "ID999")).thenReturn(null);

        assertNull(vendingMachineDao.findById("ID999"));
        verify(entityManager).close();
    }

    @Test void createMachine_persists() {
        ConcreteVendingMachine m = mock(ConcreteVendingMachine.class);
        ConcreteVendingMachine res = vendingMachineDao.createMachine(m);

        verify(transaction).begin();
        verify(entityManager).persist(m);
        verify(transaction).commit();
        verify(entityManager).close();
        assertSame(m, res);
    }

    @Test void updateMachine_merges() {
        ConcreteVendingMachine m = mock(ConcreteVendingMachine.class);
        ConcreteVendingMachine merged = mock(ConcreteVendingMachine.class);
        when(entityManager.merge(m)).thenReturn(merged);

        ConcreteVendingMachine res = vendingMachineDao.updateMachine(m);

        verify(transaction).begin();
        verify(entityManager).merge(m);
        verify(transaction).commit();
        verify(entityManager).close();
        assertSame(merged, res);
    }

    @Test void deleteMachine_exists() {
        ConcreteVendingMachine m = mock(ConcreteVendingMachine.class);
        when(entityManager.find(ConcreteVendingMachine.class, "DEL")).thenReturn(m);

        vendingMachineDao.deleteMachine("DEL");

        verify(transaction).begin();
        verify(entityManager).remove(m);
        verify(transaction).commit();
        verify(entityManager).close();
    }

    @Test void deleteMachine_notExists() {
        when(entityManager.find(ConcreteVendingMachine.class, "NONE")).thenReturn(null);

        vendingMachineDao.deleteMachine("NONE");

        verify(transaction).begin();
        verify(entityManager, never()).remove(any());
        verify(transaction).commit();
        verify(entityManager).close();
    }

    @Test void findByStatus() {
        @SuppressWarnings("unchecked")
        TypedQuery<ConcreteVendingMachine> q = (TypedQuery<ConcreteVendingMachine>) mock(TypedQuery.class);
        when(entityManager.createQuery(contains("WHERE v.status = :status"), eq(ConcreteVendingMachine.class))).thenReturn(q);
        when(q.setParameter(eq("status"), any())).thenReturn(q);
        when(q.getResultList()).thenReturn(List.of(mock(ConcreteVendingMachine.class)));

        assertFalse(vendingMachineDao.findByStatus(MachineStatus.Operative).isEmpty());
        verify(entityManager).close();
    }

    @Test void findByLocation() {
        @SuppressWarnings("unchecked")
        TypedQuery<ConcreteVendingMachine> q = (TypedQuery<ConcreteVendingMachine>) mock(TypedQuery.class);
        when(entityManager.createQuery(contains("WHERE v.location = :location"), eq(ConcreteVendingMachine.class))).thenReturn(q);
        when(q.setParameter(eq("location"), any())).thenReturn(q);
        when(q.getResultList()).thenReturn(List.of(mock(ConcreteVendingMachine.class)));

        assertFalse(vendingMachineDao.findByLocation("TestPlace").isEmpty());
        verify(entityManager).close();
    }

    // ─────────────── INTEGRATION TESTS (H2) ───────────────
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
