package com.smartVend.app.model.transaction;

import jakarta.persistence.*;
import java.io.Serializable;
import com.smartVend.app.model.vendingmachine.VendingMachine;
import com.smartVend.app.model.vendingmachine.Item;

@Entity
public class TransactionItem implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "transaction_id")
    public Transaction transaction;

    @ManyToOne
    @JoinColumn(name = "machine_id")
    public VendingMachine machine;

    @ManyToOne
    @JoinColumn(name = "item_id")
    public Item item;

    @Column(nullable = false)
    public int amount;

    public TransactionItem() {}

    public TransactionItem(Transaction transaction, VendingMachine machine, Item item, int amount) {
        this.transaction = transaction;
        this.machine = machine;
        this.item = item;
        this.amount = amount;
    }
}
