package com.smartvend.app.services;

import com.smartvend.app.dao.impl.InventoryDaoImpl;
import com.smartvend.app.dao.impl.ItemDaoImpl;
import com.smartvend.app.model.vendingmachine.Inventory;
import com.smartvend.app.model.vendingmachine.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InventoryServiceTest {

    private InventoryDaoImpl inventoryDao;
    private ItemDaoImpl itemDao;
    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        inventoryDao = mock(InventoryDaoImpl.class);
        itemDao = mock(ItemDaoImpl.class);
        inventoryService = new InventoryService(inventoryDao, itemDao);
    }

    @Test
    void addItemToInventory_success() {
        String machineId = "machine1";
        Long itemId = 1L;
        Inventory inventory = new Inventory();
        Item item = new Item();

        when(inventoryDao.getMachineInventory(machineId)).thenReturn(inventory);
        when(itemDao.getItemById(itemId)).thenReturn(item);

        inventoryService.addItemToInventory(machineId, itemId);

        verify(inventoryDao).addItemToInventory(machineId, item);
    }

    @Test
    void addItemToInventory_inventoryNotFound() {
        String machineId = "machine1";
        Long itemId = 1L;

        when(inventoryDao.getMachineInventory(machineId)).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> inventoryService.addItemToInventory(machineId, itemId));
        assertTrue(ex.getMessage().contains("Inventory not found"));
    }

    @Test
    void addItemToInventory_itemNotFound() {
        String machineId = "machine1";
        Long itemId = 1L;
        Inventory inventory = new Inventory();

        when(inventoryDao.getMachineInventory(machineId)).thenReturn(inventory);
        when(itemDao.getItemById(itemId)).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> inventoryService.addItemToInventory(machineId, itemId));
        assertTrue(ex.getMessage().contains("Item not found"));
    }

    @Test
    void removeItemFromInventory_success() {
        String machineId = "machine1";
        Long itemId = 1L;
        Inventory inventory = new Inventory();
        Item item = new Item();

        when(inventoryDao.getMachineInventory(machineId)).thenReturn(inventory);
        when(itemDao.getItemById(itemId)).thenReturn(item);

        inventoryService.removeItemFromInventory(machineId, itemId);

        verify(inventoryDao).removeItemFromInventory(machineId, item);
    }

    @Test
    void removeItemFromInventory_inventoryNotFound() {
        String machineId = "machine1";
        Long itemId = 1L;

        when(inventoryDao.getMachineInventory(machineId)).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> inventoryService.removeItemFromInventory(machineId, itemId));
        assertTrue(ex.getMessage().contains("Inventory not found"));
    }

    @Test
    void removeItemFromInventory_itemNotFound() {
        String machineId = "machine1";
        Long itemId = 1L;
        Inventory inventory = new Inventory();

        when(inventoryDao.getMachineInventory(machineId)).thenReturn(inventory);
        when(itemDao.getItemById(itemId)).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> inventoryService.removeItemFromInventory(machineId, itemId));
        assertTrue(ex.getMessage().contains("Item not found"));
    }

    @Test
    void getInventory_success() {
        String machineId = "machine1";
        Inventory inventory = new Inventory();

        when(inventoryDao.getMachineInventory(machineId)).thenReturn(inventory);

        Inventory result = inventoryService.getInventory(machineId);

        assertEquals(inventory, result);
    }

    @Test
    void getInventory_notFound() {
        String machineId = "machine1";

        when(inventoryDao.getMachineInventory(machineId)).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> inventoryService.getInventory(machineId));
        assertTrue(ex.getMessage().contains("Inventory not found"));
    }

    @Test
    void getItemsInInventory_success() {
        String machineId = "machine1";
        Inventory inventory = new Inventory();
        inventory.id = 42L;
        Item item1 = new Item();
        Item item2 = new Item();
        List<Item> items = Arrays.asList(item1, item2);

        when(inventoryDao.getMachineInventory(machineId)).thenReturn(inventory);
        when(itemDao.getInventoryItems(inventory.id)).thenReturn(items);

        List<Item> result = inventoryService.getItemsInInventory(machineId);

        assertEquals(items, result);
    }

    @Test
    void getItemsInInventory_inventoryNotFound() {
        String machineId = "machine1";

        when(inventoryDao.getMachineInventory(machineId)).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> inventoryService.getItemsInInventory(machineId));
        assertTrue(ex.getMessage().contains("Inventory not found"));
    }

    @Test
    void getItemsInInventory_noItemsFound() {
        String machineId = "machine1";
        Inventory inventory = new Inventory();
        inventory.id = 42L;

        when(inventoryDao.getMachineInventory(machineId)).thenReturn(inventory);
        when(itemDao.getInventoryItems(inventory.id)).thenReturn(Collections.emptyList());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> inventoryService.getItemsInInventory(machineId));
        assertTrue(ex.getMessage().contains("No items found"));
    }
}
