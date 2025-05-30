package com.smartvend.app.model.transaction;

import jakarta.persistence.*;
import java.io.Serializable;
import com.smartvend.app.model.vendingmachine.ConcreteVendingMachine;
import com.smartvend.app.model.vendingmachine.Item;

@Entity
public class TransactionItem implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "machine_id")
    public ConcreteVendingMachine machine;

    @ManyToOne
    @JoinColumn(name = "item_id")
    public Item item;

    @Column(nullable = false)
    public int amount;

    public TransactionItem() {
    }

    public TransactionItem(ConcreteVendingMachine machine, Item item, int amount) {
        this.machine = machine;
        this.item = item;
        this.amount = amount;
    }
}
