package com.smartvend.app.dao.impl;

import com.smartvend.app.dao.InventoryDao;
import com.smartvend.app.model.vendingmachine.Inventory;
import com.smartvend.app.model.vendingmachine.Item;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;

public class InventoryDaoImpl implements InventoryDao {

    private final EntityManagerFactory emf;

    public InventoryDaoImpl(EntityManagerFactory entityManagerFactory) {
        this.emf = entityManagerFactory;
    }

    @Override
    public Inventory getMachineInventory(String machineId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT i FROM Inventory i WHERE i.machine.id = :machineId", Inventory.class)
                    .setParameter("machineId", machineId)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public void removeItemFromInventory(String machineId, Item item) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Inventory inventory = em.createQuery(
                    "SELECT i FROM Inventory i WHERE i.machine.id = :machineId", Inventory.class)
                    .setParameter("machineId", machineId)
                    .getSingleResult();

            inventory.removeItem(item);
            em.merge(inventory);

            em.getTransaction().commit();
        } catch (NoResultException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new IllegalArgumentException("Inventory not found for machine: " + machineId, e);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void addItemToInventory(String machineId, Item item) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Inventory inventory = em.createQuery(
                    "SELECT i FROM Inventory i WHERE i.machine.id = :machineId", Inventory.class)
                    .setParameter("machineId", machineId)
                    .getSingleResult();

            inventory.addItem(item);
            em.merge(inventory);

            em.getTransaction().commit();
        } catch (NoResultException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new IllegalArgumentException("Inventory not found for machine: " + machineId, e);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void updateItemInInventory(Inventory inventory) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(inventory);
            em.getTransaction().commit();
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
