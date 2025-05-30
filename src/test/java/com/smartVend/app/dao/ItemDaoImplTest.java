package com.smartvend.app.dao;

import com.smartvend.app.dao.impl.ItemDaoImpl;
import com.smartvend.app.model.vendingmachine.Item;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ItemDaoImplTest {

    private EntityManager entityManager;
    private ItemDaoImpl itemDao;

    @BeforeEach
    void setUp() {
        entityManager = mock(EntityManager.class);
        itemDao = new ItemDaoImpl();

        // Reflection hack per settare entityManager (puoi sostituire con setter se preferisci)
        try {
            var field = ItemDaoImpl.class.getDeclaredField("entityManager");
            field.setAccessible(true);
            field.set(itemDao, entityManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getInventoryItems_returnsItems() {
        String inventoryId = "123";
        TypedQuery<Item> query = mock(TypedQuery.class);
        List<Item> expected = Collections.singletonList(mock(Item.class));

        when(entityManager.createQuery(anyString(), eq(Item.class))).thenReturn(query);
        when(query.setParameter(eq("inventoryId"), eq(inventoryId))).thenReturn(query);
        when(query.getResultList()).thenReturn(expected);

        List<Item> actual = itemDao.getInventoryItems(inventoryId);
        assertEquals(expected, actual);

        verify(entityManager).createQuery(contains("inventoryId"), eq(Item.class));
        verify(query).setParameter("inventoryId", inventoryId);
        verify(query).getResultList();
    }
}
