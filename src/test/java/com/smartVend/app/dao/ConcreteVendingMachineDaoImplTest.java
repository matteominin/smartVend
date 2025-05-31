package com.smartvend.app.dao;

import com.smartvend.app.dao.impl.ConcreteVendingMachineDaoImpl;
import com.smartvend.app.model.vendingmachine.ConcreteVendingMachine;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ConcreteVendingMachineDaoImplTest {

    private EntityManager entityManager;
    private ConcreteVendingMachineDaoImpl vendingMachineDao;

    @BeforeEach
    void setUp() {
        entityManager = mock(EntityManager.class);
        vendingMachineDao = new ConcreteVendingMachineDaoImpl();
        // Reflection to inject EntityManager
        try {
            var field = ConcreteVendingMachineDaoImpl.class.getDeclaredField("entityManager");
            field.setAccessible(true);
            field.set(vendingMachineDao, entityManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void findAll_returnsEmptyList() {
        @SuppressWarnings("unchecked")
        TypedQuery<ConcreteVendingMachine> query = (TypedQuery<ConcreteVendingMachine>) mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(ConcreteVendingMachine.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        List<ConcreteVendingMachine> result = vendingMachineDao.findAll();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_returnsListWithMachines() {
        @SuppressWarnings("unchecked")
        TypedQuery<ConcreteVendingMachine> query = (TypedQuery<ConcreteVendingMachine>) mock(TypedQuery.class);
        ConcreteVendingMachine machine = mock(ConcreteVendingMachine.class);
        when(entityManager.createQuery(anyString(), eq(ConcreteVendingMachine.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(machine));

        List<ConcreteVendingMachine> result = vendingMachineDao.findAll();
        assertEquals(1, result.size());
        assertEquals(machine, result.get(0));
    }

    @Test
    void findById_returnsMachine() {
        ConcreteVendingMachine machine = mock(ConcreteVendingMachine.class);
        when(entityManager.find(ConcreteVendingMachine.class, "ID123")).thenReturn(machine);

        ConcreteVendingMachine result = vendingMachineDao.findById("ID123");
        assertSame(machine, result);
    }

    @Test
    void findById_returnsNullIfNotFound() {
        when(entityManager.find(ConcreteVendingMachine.class, "ID999")).thenReturn(null);

        ConcreteVendingMachine result = vendingMachineDao.findById("ID999");
        assertNull(result);
    }

    @Test
    void createMachine_persistsAndReturnsMachine() {
        ConcreteVendingMachine machine = mock(ConcreteVendingMachine.class);

        ConcreteVendingMachine result = vendingMachineDao.createMachine(machine);
        verify(entityManager).persist(machine);
        assertSame(machine, result);
    }

    @Test
    void updateMachine_mergesAndReturnsMachine() {
        ConcreteVendingMachine machine = mock(ConcreteVendingMachine.class);
        ConcreteVendingMachine merged = mock(ConcreteVendingMachine.class);
        when(entityManager.merge(machine)).thenReturn(merged);

        ConcreteVendingMachine result = vendingMachineDao.updateMachine(machine);
        verify(entityManager).merge(machine);
        assertSame(merged, result);
    }

    @Test
    void deleteMachine_removesIfExists() {
        ConcreteVendingMachine machine = mock(ConcreteVendingMachine.class);
        when(entityManager.find(ConcreteVendingMachine.class, "TODELETE")).thenReturn(machine);

        vendingMachineDao.deleteMachine("TODELETE");
        verify(entityManager).remove(machine);
    }

    @Test
    void deleteMachine_doesNothingIfNotFound() {
        when(entityManager.find(ConcreteVendingMachine.class, "NOTFOUND")).thenReturn(null);

        vendingMachineDao.deleteMachine("NOTFOUND");
        verify(entityManager, never()).remove(any());
    }
}
