package com.smartvend.app.dao;

import com.smartvend.app.model.vendingmachine.Inventory;
import com.smartvend.app.model.vendingmachine.Item;

public interface InventoryDao {
    Inventory getMachineInventory(String machineId);

    void updateItemInInventory(Inventory inventory);

    void addItemToInventory(String machineId, Item item);

    void removeItemFromInventory(String machineId, Item item);
}
