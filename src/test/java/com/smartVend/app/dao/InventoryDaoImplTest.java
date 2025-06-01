package com.smartvend.app.dao;

import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.smartvend.app.dao.impl.InventoryDaoImpl;
import com.smartvend.app.model.vendingmachine.ConcreteVendingMachine;
import com.smartvend.app.model.vendingmachine.Inventory;
import com.smartvend.app.model.vendingmachine.Item;
import com.smartvend.app.model.vendingmachine.ItemType;
import com.smartvend.app.model.vendingmachine.MachineStatus;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

class InventoryDaoImplTest {

    /*
     * ──────────────────────────── UNIT TESTS (Mockito) ───────────────────────────
     */
    @Nested
    class Unit {

        @Mock
        EntityManagerFactory emf;
        @Mock
        EntityManager em;
        @Mock
        EntityTransaction tx;
        @InjectMocks
        InventoryDaoImpl dao;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
            when(emf.createEntityManager()).thenReturn(em);
            when(em.getTransaction()).thenReturn(tx);
        }

        @Test
        void getMachineInventory_returnsInventory() {
            Inventory inventory = new Inventory();
            @SuppressWarnings("unchecked")
            TypedQuery<Inventory> query = mock(TypedQuery.class);

            when(em.createQuery(anyString(), eq(Inventory.class))).thenReturn(query);
            when(query.setParameter("machineId", "abc")).thenReturn(query);
            when(query.getSingleResult()).thenReturn(inventory);

            Inventory result = dao.getMachineInventory("abc");
            assertSame(inventory, result);
            verify(query).getSingleResult();
        }

        @Test
        void removeItemFromInventory_removesAndMerges() {
            Inventory inventory = mock(Inventory.class);
            Item item = mock(Item.class);
            @SuppressWarnings("unchecked")
            TypedQuery<Inventory> query = mock(TypedQuery.class);

            when(em.createQuery(anyString(), eq(Inventory.class))).thenReturn(query);
            when(query.setParameter("machineId", "macchina1")).thenReturn(query);
            when(query.getSingleResult()).thenReturn(inventory);

            dao.removeItemFromInventory("macchina1", item);

            verify(inventory).removeItem(item);
            verify(em).merge(inventory);
            verify(tx).begin();
            verify(tx).commit();
        }

        @Test
        void removeItemFromInventory_throwsIfInventoryNotFound_andRollsBack() {
            Item item = mock(Item.class);
            @SuppressWarnings("unchecked")
            TypedQuery<Inventory> query = mock(TypedQuery.class);

            when(em.createQuery(anyString(), eq(Inventory.class))).thenReturn(query);
            when(query.setParameter("machineId", "notfound")).thenReturn(query);
            when(query.getSingleResult()).thenThrow(new NoResultException());
            when(tx.isActive()).thenReturn(true);

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> dao.removeItemFromInventory("notfound", item));
            assertTrue(ex.getMessage().contains("Inventory not found"));
            verify(tx).rollback();
        }

        @Test
        void removeItemFromInventory_rollsBackOnOtherException() {
            Item item = mock(Item.class);
            @SuppressWarnings("unchecked")
            TypedQuery<Inventory> query = mock(TypedQuery.class);

            when(em.createQuery(anyString(), eq(Inventory.class))).thenReturn(query);
            when(query.setParameter("machineId", "fail")).thenReturn(query);
            when(query.getSingleResult()).thenThrow(new RuntimeException("Other error"));
            when(tx.isActive()).thenReturn(true);

            assertThrows(RuntimeException.class,
                    () -> dao.removeItemFromInventory("fail", item));
            verify(tx).rollback();
        }

        @Test
        void addItemToInventory_addsAndMerges() {
            Inventory inventory = mock(Inventory.class);
            Item item = mock(Item.class);
            @SuppressWarnings("unchecked")
            TypedQuery<Inventory> query = mock(TypedQuery.class);

            when(em.createQuery(anyString(), eq(Inventory.class))).thenReturn(query);
            when(query.setParameter("machineId", "macchina2")).thenReturn(query);
            when(query.getSingleResult()).thenReturn(inventory);

            dao.addItemToInventory("macchina2", item);

            verify(inventory).addItem(item);
            verify(em).merge(inventory);
            verify(tx).begin();
            verify(tx).commit();
        }

        @Test
        void addItemToInventory_throwsIfInventoryNotFound_andRollsBack() {
            Item item = mock(Item.class);
            @SuppressWarnings("unchecked")
            TypedQuery<Inventory> query = mock(TypedQuery.class);

            when(em.createQuery(anyString(), eq(Inventory.class))).thenReturn(query);
            when(query.setParameter("machineId", "notfound")).thenReturn(query);
            when(query.getSingleResult()).thenThrow(new NoResultException());
            when(tx.isActive()).thenReturn(true);

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> dao.addItemToInventory("notfound", item));
            assertTrue(ex.getMessage().contains("Inventory not found"));
            verify(tx).rollback();
        }

        @Test
        void addItemToInventory_rollsBackOnOtherException() {
            Item item = mock(Item.class);
            @SuppressWarnings("unchecked")
            TypedQuery<Inventory> query = mock(TypedQuery.class);

            when(em.createQuery(anyString(), eq(Inventory.class))).thenReturn(query);
            when(query.setParameter("machineId", "fail")).thenReturn(query);
            when(query.getSingleResult()).thenThrow(new RuntimeException("Other error"));
            when(tx.isActive()).thenReturn(true);

            assertThrows(RuntimeException.class,
                    () -> dao.addItemToInventory("fail", item));
            verify(tx).rollback();
        }

        @Test
        void updateItemInInventory_mergesAndCommits() {
            Inventory inventory = new Inventory();
            when(tx.isActive()).thenReturn(false);

            dao.updateItemInInventory(inventory);

            verify(em).merge(inventory);
            verify(tx).begin();
            verify(tx).commit();
        }

        @Test
        void updateItemInInventory_rollsBackOnException() {
            Inventory inventory = new Inventory();
            doThrow(new RuntimeException("fail")).when(em).merge(inventory);
            when(tx.isActive()).thenReturn(true);

            assertThrows(RuntimeException.class, () -> dao.updateItemInInventory(inventory));
            verify(tx).rollback();
        }
    }

    /*
     * ───────────────────────── INTEGRATION TESTS (H2 DB) ─────────────────────────
     */
    @Nested
    class Integration {

        private static EntityManagerFactory emf;
        private InventoryDaoImpl dao;

        @BeforeAll
        static void startPU() {
            emf = Persistence.createEntityManagerFactory("test-pu");
        }

        @AfterAll
        static void stopPU() {
            if (emf != null)
                emf.close();
        }

        @BeforeEach
        void setup() {
            dao = new InventoryDaoImpl(emf);
        }

        @Test
        void integration_CRUD_flow() {
            // Prepara una ConcreteVendingMachine, Inventory, Item e collega tutto
            // correttamente
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();

            ConcreteVendingMachine machine = new ConcreteVendingMachine(
                    "ABC123", null, "Milano", 30, MachineStatus.Operative, null);
            // Aggiungi lastMaintenance (obbligatorio!)
            machine.setLastMaintenance(java.time.Instant.now());
            em.persist(machine);

            Inventory inventory = new Inventory();
            inventory.setMachine(machine);
            inventory.setOccupiedSpace(0);
            em.persist(inventory);

            // Collega anche dal lato machine
            machine.setInventory(inventory);

            // Crea un item associato a questo inventory
            Item item = new Item(
                    0L, "Mars", "Barretta", 100, 5, 1.2, 1, ItemType.Snack);
            item.setInventory(inventory);
            em.persist(item);

            // Aggiungi item all'inventory (relazione bidirezionale)
            inventory.addItem(item);

            em.getTransaction().commit();
            em.close();

            // GET: l'inventory della macchina
            Inventory loaded = dao.getMachineInventory(machine.getId());
            assertNotNull(loaded);
            assertEquals(machine.getId(), loaded.getMachine().getId());

            // UPDATE (modifica occupiedSpace e aggiorna)
            loaded.setOccupiedSpace(10);
            dao.updateItemInInventory(loaded);

            // REMOVE: rimuovi item
            dao.removeItemFromInventory(machine.getId(), item);

            // ADD: riaggiungi
            dao.addItemToInventory(machine.getId(), item);
        }
    }
}
