package com.smartvend.app.model.vendingmachine;

import java.util.Date;

public class Item {
    private String id;
    private String name;
    private String description;
    private int size;
    private int quantity;
    private double price;
    private Date createdAt;
    private ItemType type;

    public Item() {
    }

    public Item(String id, String name, String description, int size, int quantity,
            double price, Date createdAt, ItemType type) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.size = size;
        this.quantity = quantity;
        this.price = price;
        this.createdAt = createdAt;
        this.type = type;
    }

    // Getter e setter qui...
}
