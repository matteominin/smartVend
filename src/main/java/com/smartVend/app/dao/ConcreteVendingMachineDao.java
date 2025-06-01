package com.smartvend.app.dao;

import java.util.List;

import com.smartvend.app.model.vendingmachine.ConcreteVendingMachine;
import com.smartvend.app.model.vendingmachine.MachineStatus;

public interface ConcreteVendingMachineDao {
    ConcreteVendingMachine createMachine(ConcreteVendingMachine machine);
    ConcreteVendingMachine findById(String id);
    List<ConcreteVendingMachine> findAll();
    ConcreteVendingMachine updateMachine(ConcreteVendingMachine machine);
    void deleteMachine(String id);

    // Query extra
    List<ConcreteVendingMachine> findByStatus(MachineStatus status);
    List<ConcreteVendingMachine> findByLocation(String location);
}
