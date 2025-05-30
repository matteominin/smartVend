package com.smartvend.app.dao.impl;

import java.util.List;

import com.smartvend.app.dao.ItemDao;
import com.smartvend.app.model.vendingmachine.Item;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public class ItemDaoImpl implements ItemDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Item> getInventoryItems(String inventoryId) {
        // Si assume che Item abbia una relazione con Inventory (ad esempio, un campo inventory.id)
        return entityManager.createQuery(
                "SELECT i FROM Item i WHERE i.inventory.id = :inventoryId", Item.class)
            .setParameter("inventoryId", inventoryId)
            .getResultList();
    }
}
