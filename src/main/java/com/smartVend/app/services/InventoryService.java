package com.smartvend.app.services;

import java.util.List;

import com.smartvend.app.dao.impl.InventoryDaoImpl;
import com.smartvend.app.dao.impl.ItemDaoImpl;
import com.smartvend.app.model.vendingmachine.Inventory;
import com.smartvend.app.model.vendingmachine.Item;

public class InventoryService {
    private InventoryDaoImpl inventoryDao;
    private ItemDaoImpl itemDao;

    public InventoryService(InventoryDaoImpl inventoryDao, ItemDaoImpl itemDao) {
        this.inventoryDao = inventoryDao;
        this.itemDao = itemDao;
    }

    public void addItemToInventory(String machineId, Long itemId) {
        if (inventoryDao.getMachineInventory(machineId) == null) {
            throw new IllegalArgumentException("Inventory not found for machine: " + machineId);
        }
        Item item = itemDao.getItemById(itemId);
        if (item == null) {
            throw new IllegalArgumentException("Item not found with ID: " + itemId);
        }

        inventoryDao.addItemToInventory(machineId, item);
    }

    public void removeItemFromInventory(String machineId, Long itemId) {
        if (inventoryDao.getMachineInventory(machineId) == null) {
            throw new IllegalArgumentException("Inventory not found for machine: " + machineId);
        }
        Item item = itemDao.getItemById(itemId);
        if (item == null) {
            throw new IllegalArgumentException("Item not found with ID: " + itemId);
        }
        inventoryDao.removeItemFromInventory(machineId, item);
    }

    public Inventory getInventory(String machineId) {
        if (inventoryDao.getMachineInventory(machineId) == null) {
            throw new IllegalArgumentException("Inventory not found for machine: " + machineId);
        }
        return inventoryDao.getMachineInventory(machineId);
    }

    public List<Item> getItemsInInventory(String machineId) {
        Inventory inventory = inventoryDao.getMachineInventory(machineId);
        if (inventory == null) {
            throw new IllegalArgumentException("Inventory not found for machine: " + machineId);
        }
        List<Item> items = itemDao.getInventoryItems(inventory.id);
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("No items found in inventory for machine: " + machineId);
        }
        return items;
    }
}
