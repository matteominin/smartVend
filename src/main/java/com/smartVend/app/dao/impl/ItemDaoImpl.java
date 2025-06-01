package com.smartvend.app.dao.impl;

import com.smartvend.app.dao.ItemDao;
import com.smartvend.app.model.vendingmachine.Item;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class ItemDaoImpl implements ItemDao {

    private final EntityManagerFactory emf;

    public ItemDaoImpl(EntityManagerFactory entityManagerFactory) {
        this.emf = entityManagerFactory;
    }

    @Override
    public List<Item> getInventoryItems(long inventoryId) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Item> query = em.createQuery(
                    "SELECT i FROM Item i WHERE i.inventory.id = :inventoryId", Item.class);
            query.setParameter("inventoryId", inventoryId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Item getItemById(long itemId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Item.class, itemId);
        } finally {
            em.close();
        }
    }

    @Override
    public Item updateItem(Item item) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Item merged = em.merge(item);
            em.getTransaction().commit();
            return merged;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}
