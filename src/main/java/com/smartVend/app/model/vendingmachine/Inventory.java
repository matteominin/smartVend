package com.smartvend.app.model.vendingmachine;

import java.util.List;

public class Inventory {
    private String id;
    private int occupiedSpace;
    private List<Item> items;

    public Inventory() {
    }

    public Inventory(String id, int occupiedSpace, List<Item> items) {
        this.id = id;
        this.occupiedSpace = occupiedSpace;
        this.items = items;
    }

    // Getter e setter qui...
}
