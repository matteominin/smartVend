package com.smartvend.app.dao;

import java.util.List;

import com.smartvend.app.model.vendingmachine.Item;

public interface ItemDao {
    List<Item> getInventoryItems(long inventoryId);

    Item updateItem(Item item);

    Item getItemById(long itemId);
}
