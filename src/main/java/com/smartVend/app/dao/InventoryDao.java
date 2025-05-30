package com.smartvend.app.dao;

import com.smartvend.app.model.vendingmachine.Inventory;

public interface InventoryDao {
    Inventory getMachineInventory(String machineId);
}
