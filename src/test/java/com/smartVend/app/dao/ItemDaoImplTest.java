package com.smartvend.app.dao;

import com.smartvend.app.dao.impl.ItemDaoImpl;
import com.smartvend.app.model.vendingmachine.Item;
import com.smartvend.app.model.vendingmachine.Inventory;
import com.smartvend.app.model.vendingmachine.ItemType;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ItemDaoImplTest {

    /*──────────────────────────── UNIT TESTS (Mockito) ───────────────────────────*/
    @Nested
    class Unit {

        @Mock EntityManagerFactory emf;
        @Mock EntityManager em;
        @Mock EntityTransaction tx;
        @InjectMocks ItemDaoImpl dao;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
            when(emf.createEntityManager()).thenReturn(em);
            when(em.getTransaction()).thenReturn(tx);
        }

        @Test
        void getItemById_returnsItem() {
            Item item = buildItem();
            when(em.find(Item.class, 2L)).thenReturn(item);
            Item result = dao.getItemById(2L);
            assertEquals(item, result);
        }

        @Test
        void getInventoryItems_returnsList() {
            long inventoryId = 5L;
            @SuppressWarnings("unchecked")
            TypedQuery<Item> query = (TypedQuery<Item>) mock(TypedQuery.class);
            Item item = buildItem();
            List<Item> expected = List.of(item);

            when(em.createQuery(anyString(), eq(Item.class))).thenReturn(query);
            when(query.setParameter(eq("inventoryId"), eq(inventoryId))).thenReturn(query);
            when(query.getResultList()).thenReturn(expected);

            List<Item> actual = dao.getInventoryItems(inventoryId);
            assertEquals(expected, actual);
            verify(em).createQuery(contains("inventoryId"), eq(Item.class));
            verify(query).setParameter("inventoryId", inventoryId);
            verify(query).getResultList();
        }

        @Test
        void getInventoryItems_returnsEmptyList() {
            long inventoryId = 11L;
            @SuppressWarnings("unchecked")
            TypedQuery<Item> query = (TypedQuery<Item>) mock(TypedQuery.class);

            when(em.createQuery(anyString(), eq(Item.class))).thenReturn(query);
            when(query.setParameter(eq("inventoryId"), eq(inventoryId))).thenReturn(query);
            when(query.getResultList()).thenReturn(Collections.emptyList());

            List<Item> actual = dao.getInventoryItems(inventoryId);
            assertNotNull(actual);
            assertTrue(actual.isEmpty());
        }

        @Test
        void updateItem_mergesItem() {
            Item item = buildItem();
            Item merged = buildItem();
            when(em.merge(item)).thenReturn(merged);
            when(tx.isActive()).thenReturn(false);

            Item result = dao.updateItem(item);

            verify(em).merge(item);
            verify(tx).begin();
            verify(tx).commit();
            assertEquals(merged, result);
        }

        // Utility per costruire Item fittizio
        private Item buildItem() {
            // id, name, description, volume, quantity, price, position, type
            return new Item(1L, "CocaCola", "Bibita", 500, 10, 1.5, 1, ItemType.Bottle);
        }
    }

    /*───────────────────────── INTEGRATION TESTS (H2 DB) ─────────────────────────*/
    @Nested
    class Integration {

        private static EntityManagerFactory emf;
        private ItemDaoImpl dao;

        @BeforeAll
        static void startPU() { emf = Persistence.createEntityManagerFactory("test-pu"); }

        @AfterAll
        static void stopPU() { if (emf != null) emf.close(); }

        @BeforeEach
        void setup() { dao = new ItemDaoImpl(emf); }

        @Test
        void integration_CRUD_flow() {
            // Crea e persisti inventory
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();

            Inventory inventory = new Inventory();
            em.persist(inventory);

            // Crea item con il costruttore giusto e poi setta l'inventory!
            Item item = new Item(0L, "Acqua", "Naturale", 500, 50, 1.0, 2, ItemType.Bottle);
            item.setInventory(inventory);
            em.persist(item);

            em.getTransaction().commit();
            em.close();

            // GET BY ID
            Item found = dao.getItemById(item.getId());
            assertNotNull(found);
            assertEquals("Acqua", found.getName());

            // GET INVENTORY ITEMS
            List<Item> items = dao.getInventoryItems(inventory.getId());
            assertFalse(items.isEmpty());
            assertEquals("Acqua", items.get(0).getName());

            // UPDATE
            found.setPrice(2.0);
            Item updated = dao.updateItem(found);
            assertEquals(2.0, updated.getPrice());
        }
    }
}
