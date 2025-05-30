package com.smartvend.app.model.transaction;

import com.smartvend.app.model.vendingmachine.Item;;

public class TransactionItem {
    private String id;
    private double totalPrice;
    private Item item;

    public TransactionItem() {
    }

    public TransactionItem(String id, double totalPrice, Item item) {
        this.id = id;
        this.totalPrice = totalPrice;
        this.item = item;
    }

    // Getter e setter qui...
}
