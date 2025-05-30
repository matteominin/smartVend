package com.smartvend.app.model.vendingmachine;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
public class Inventory implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "machine_id")
    public ConcreteVendingMachine machine;

    @ManyToOne
    @JoinColumn(name = "item_id")
    public List<Item> items;

    @Column(nullable = false)
    public int occupiedSpace;

    public Inventory() {
        // Default constructor for JPA
    }

    public Inventory(long id, ConcreteVendingMachine machine, List<Item> items, int occupiedSpace) {
        this.id = id;
        this.machine = machine;
        this.items = items;
        this.occupiedSpace = occupiedSpace;
    }

    public Inventory(ConcreteVendingMachine machine, List<Item> items, int occupiedSpace) {
        this.machine = machine;
        this.items = items;
        this.occupiedSpace = occupiedSpace;
    }

    public Long getId() {
        return id;
    }
}
