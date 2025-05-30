package com.smartvend.app.dao.impl;

import com.smartvend.app.dao.InventoryDao;
import com.smartvend.app.model.vendingmachine.Inventory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public class InventoryDaoImpl implements InventoryDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Inventory getMachineInventory(String machineId) {
        // Assumiamo che Inventory abbia un campo "machine.id"
        return entityManager.createQuery(
                "SELECT i FROM Inventory i WHERE i.machine.id = :machineId", Inventory.class)
            .setParameter("machineId", machineId)
            .getSingleResult();
    }
}
