package com.smartvend.app.dao;

import java.util.List;

import com.smartvend.app.model.vendingmachine.Item;

public interface ItemDao {
    List<Item> getInventoryItems(String inventoryId);
}
