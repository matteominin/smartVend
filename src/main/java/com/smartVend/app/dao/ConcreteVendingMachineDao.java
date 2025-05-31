package com.smartvend.app.dao;

import java.util.List;

import com.smartvend.app.model.vendingmachine.ConcreteVendingMachine;

public interface ConcreteVendingMachineDao {
    ConcreteVendingMachine createMachine(ConcreteVendingMachine machine);

    ConcreteVendingMachine findById(String id);

    List<ConcreteVendingMachine> findAll();

    ConcreteVendingMachine updateMachine(ConcreteVendingMachine machine);

    void deleteMachine(String id);
}
