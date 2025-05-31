package com.smartvend.app.dao;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.smartvend.app.dao.impl.ItemDaoImpl;
import com.smartvend.app.model.vendingmachine.Item;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

class ItemDaoImplTest {

    private EntityManager entityManager;
    private ItemDaoImpl itemDao;

    @BeforeEach
    void setUp() {
        entityManager = mock(EntityManager.class);
        itemDao = new ItemDaoImpl();

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
        long inventoryId = 123;

        @SuppressWarnings("unchecked")
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

    @Test
    void getInventoryItems_returnsEmptyList() {
        long inventoryId = 888;

        @SuppressWarnings("unchecked")
        TypedQuery<Item> query = mock(TypedQuery.class);

        when(entityManager.createQuery(anyString(), eq(Item.class))).thenReturn(query);
        when(query.setParameter(eq("inventoryId"), eq(inventoryId))).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        List<Item> actual = itemDao.getInventoryItems(inventoryId);
        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

    @Test
    void getItemById_returnsItem() {
        Item item = mock(Item.class);
        when(entityManager.find(Item.class, 42L)).thenReturn(item);

        Item result = itemDao.getItemById(42L);

        assertNotNull(result);
        assertEquals(item, result);
    }

    @Test
    void getItemById_returnsNullIfNotFound() {
        when(entityManager.find(Item.class, 99L)).thenReturn(null);

        Item result = itemDao.getItemById(99L);

        assertNull(result);
    }

    @Test
    void updateItem_mergesItem() {
        Item item = mock(Item.class);

        itemDao.updateItem(item);

        verify(entityManager).merge(item);
    }
}
