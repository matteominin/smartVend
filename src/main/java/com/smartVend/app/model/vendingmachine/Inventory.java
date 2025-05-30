package com.smartVend.app.model.vendingmachine;

import jakarta.persistence.*;
import java.io.Serializable;

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
    public Item item;

    @Column(nullable = false)
    public int occupiedSpace;

    public Inventory() {}

    public Inventory(ConcreteVendingMachine machine, Item item, int occupiedSpace) {
        this.machine = machine;
        this.item = item;
        this.occupiedSpace = occupiedSpace;
    }
}
