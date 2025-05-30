package com.smartvend.app.dao;

import com.smartvend.app.dao.impl.InventoryDaoImpl;
import com.smartvend.app.model.vendingmachine.Inventory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

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
        TypedQuery<Inventory> query = mock(TypedQuery.class);
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
}
