package com.smartvend.app.model.transaction;

import java.io.Serializable;
import com.smartvend.app.model.vendingmachine.ConcreteVendingMachine;
import com.smartvend.app.model.vendingmachine.Item;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

@Entity
public class TransactionItem implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "machine_id")
    private ConcreteVendingMachine machine;

    @OneToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(nullable = false)
    private double amount;

    @ManyToOne
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

    public TransactionItem() {
        // Default constructor for JPA
    }

    public TransactionItem(ConcreteVendingMachine machine, Item item, int amount) {
        this.machine = machine;
        this.item = item;
        this.amount = amount;
    }

    // You might also want to add a constructor that includes the transaction
    public TransactionItem(ConcreteVendingMachine machine, Item item, int price, Transaction transaction) {
        this.machine = machine;
        this.item = item;
        this.transaction = transaction;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConcreteVendingMachine getMachine() {
        return machine;
    }

    public void setMachine(ConcreteVendingMachine machine) {
        this.machine = machine;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }
}