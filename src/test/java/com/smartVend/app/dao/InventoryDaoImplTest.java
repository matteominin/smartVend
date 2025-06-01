package com.smartvend.app.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.smartvend.app.dao.impl.InventoryDaoImpl;
import com.smartvend.app.model.vendingmachine.Inventory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

class InventoryDaoImplTest {

    @Mock
    private EntityManagerFactory entityManagerFactory;
    @Mock
    private EntityManager entityManager;
    @Mock
    private EntityTransaction transaction;

    @InjectMocks
    private InventoryDaoImpl inventoryDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
        when(entityManager.getTransaction()).thenReturn(transaction);
    }

    @Test
    void getMachineInventory_returnsInventory() {
        String machineId = "MACHINE123";
        @SuppressWarnings("unchecked")
        TypedQuery<Inventory> query = (TypedQuery<Inventory>) mock(TypedQuery.class);
        Inventory expected = mock(Inventory.class);

        when(entityManager.createQuery(anyString(), eq(Inventory.class))).thenReturn(query);
        when(query.setParameter(eq("machineId"), eq(machineId))).thenReturn(query);
        when(query.getSingleResult()).thenReturn(expected);

        Inventory actual = inventoryDao.getMachineInventory(machineId);
        assertEquals(expected, actual);

        verify(entityManager).createQuery(contains("machineId"), eq(Inventory.class));
        verify(query).setParameter("machineId", machineId);
        verify(query).getSingleResult();
    }

    @Test
    void getMachineInventory_throwsExceptionIfNotFound() {
        String machineId = "NOT_EXIST";
        @SuppressWarnings("unchecked")
        TypedQuery<Inventory> query = (TypedQuery<Inventory>) mock(TypedQuery.class);

        when(entityManager.createQuery(anyString(), eq(Inventory.class))).thenReturn(query);
        when(query.setParameter(eq("machineId"), eq(machineId))).thenReturn(query);
        when(query.getSingleResult()).thenThrow(new NoResultException());

        assertThrows(NoResultException.class, () -> inventoryDao.getMachineInventory(machineId));
    }
}
