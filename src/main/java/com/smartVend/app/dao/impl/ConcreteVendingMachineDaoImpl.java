package com.smartvend.app.dao.impl;

import java.util.List;

import com.smartvend.app.dao.ConcreteVendingMachineDao;
import com.smartvend.app.model.vendingmachine.ConcreteVendingMachine;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class ConcreteVendingMachineDaoImpl implements ConcreteVendingMachineDao {

    private final EntityManagerFactory emf;

    public ConcreteVendingMachineDaoImpl(EntityManagerFactory entityManagerFactory) {
        this.emf = entityManagerFactory;
    }

    @Override
    public List<ConcreteVendingMachine> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT v FROM ConcreteVendingMachine v", ConcreteVendingMachine.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public ConcreteVendingMachine findById(String id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(ConcreteVendingMachine.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public ConcreteVendingMachine createMachine(ConcreteVendingMachine machine) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(machine);
            em.getTransaction().commit();
            return machine;
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
    public ConcreteVendingMachine updateMachine(ConcreteVendingMachine machine) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            ConcreteVendingMachine merged = em.merge(machine);
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

    @Override
    public void deleteMachine(String id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            ConcreteVendingMachine machine = em.find(ConcreteVendingMachine.class, id);
            if (machine != null) {
                em.remove(machine);
            }
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
