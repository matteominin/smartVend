package com.smartvend.app.dao.impl;

import java.util.List;

import com.smartvend.app.dao.ItemDao;
import com.smartvend.app.model.vendingmachine.Item;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public class ItemDaoImpl implements ItemDao {

    @PersistenceContext
    private EntityManager entityManager;

    public Item getItemById(long itemId) {
        return entityManager.find(Item.class, itemId);
    }

    public void updateItem(Item item) {
        entityManager.merge(item);
    }

    public List<Item> getInventoryItems(long inventoryId) {
        return entityManager.createQuery(
                "SELECT i FROM Item i WHERE i.inventory.id = :inventoryId", Item.class)
                .setParameter("inventoryId", inventoryId)
                .getResultList();
    }
}
