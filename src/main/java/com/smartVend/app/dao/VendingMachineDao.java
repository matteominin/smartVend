package com.smartvend.app.dao;

import java.util.List;

import com.smartvend.app.model.vendingmachine.ConcreteVendingMachine;

public interface VendingMachineDao {
    List<ConcreteVendingMachine> findAll();
}
