package com.smartvend.app.dao.impl;

import com.smartvend.app.dao.InventoryDao;
import com.smartvend.app.model.vendingmachine.Inventory;
import com.smartvend.app.model.vendingmachine.Item;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public class InventoryDaoImpl implements InventoryDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Inventory getMachineInventory(String machineId) {
        return entityManager.createQuery(
                "SELECT i FROM Inventory i WHERE i.machine.id = :machineId", Inventory.class)
                .setParameter("machineId", machineId)
                .getSingleResult();
    }

    @Override
    public void removeItemFromInventory(String machineId, Item item) {
        Inventory inventory = getMachineInventory(machineId);
        if (inventory != null) {
            inventory.removeItem(item);
            entityManager.merge(inventory);
        } else {
            throw new IllegalArgumentException("Inventory not found for machine: " + machineId);
        }
    }

    @Override
    public void addItemToInventory(String machineId, Item item) {
        Inventory inventory = getMachineInventory(machineId);
        if (inventory != null) {
            inventory.addItem(item);
            entityManager.merge(inventory);
        } else {
            throw new IllegalArgumentException("Inventory not found for machine: " + machineId);
        }
    }

    @Override
    public void updateItemInInventory(Inventory inventory) {
        entityManager.merge(inventory);
    }
}
