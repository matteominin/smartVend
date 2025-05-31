package com.smartvend.app.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.smartvend.app.dao.impl.InventoryDaoImpl;
import com.smartvend.app.model.vendingmachine.Inventory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

class InventoryDaoImplTest {

    private EntityManager entityManager;
    private InventoryDaoImpl inventoryDao;

    @BeforeEach
    void setUp() {
        entityManager = mock(EntityManager.class);
        inventoryDao = new InventoryDaoImpl();

        // Setta l'EntityManager via reflection (oppure aggiungi un setter)
        try {
            var field = InventoryDaoImpl.class.getDeclaredField("entityManager");
            field.setAccessible(true);
            field.set(inventoryDao, entityManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
