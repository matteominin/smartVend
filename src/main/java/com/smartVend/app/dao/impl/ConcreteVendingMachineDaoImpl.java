package com.smartvend.app.dao.impl;

import java.util.List;

import com.smartvend.app.dao.ConcreteVendingMachineDao;
import com.smartvend.app.model.vendingmachine.ConcreteVendingMachine;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public class ConcreteVendingMachineDaoImpl implements ConcreteVendingMachineDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ConcreteVendingMachine> findAll() {
        return entityManager.createQuery("SELECT v FROM ConcreteVendingMachine v", ConcreteVendingMachine.class)
                .getResultList();
    }

    @Override
    public ConcreteVendingMachine findById(String id) {
        return entityManager.find(ConcreteVendingMachine.class, id);
    }

    @Override
    public ConcreteVendingMachine createMachine(ConcreteVendingMachine machine) {
        entityManager.persist(machine);
        return machine;
    }

    @Override
    public ConcreteVendingMachine updateMachine(ConcreteVendingMachine machine) {
        return entityManager.merge(machine);
    }

    @Override
    public void deleteMachine(String id) {
        ConcreteVendingMachine machine = entityManager.find(ConcreteVendingMachine.class, id);
        if (machine != null) {
            entityManager.remove(machine);
        }
    }
}
