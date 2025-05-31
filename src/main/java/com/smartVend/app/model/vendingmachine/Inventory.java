package com.smartvend.app.model.vendingmachine;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Inventory implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "machine_id")
    private ConcreteVendingMachine machine;
    @ManyToOne
    @JoinColumn(name = "item_id")
    private List<Item> items;
    @Column(nullable = false)
    private int occupiedSpace;
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

    public List<Item> getItems() {
        return items;
    }

    public void addItem(Item item) {
        if (this.items != null) {
            this.items.add(item);
        }
    }

    public void removeItem(Item item) {
        if (this.items != null) {
            this.items.remove(item);
        }
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

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public int getOccupiedSpace() {
        return occupiedSpace;
    }

    public void setOccupiedSpace(int occupiedSpace) {
        this.occupiedSpace = occupiedSpace;
    }
    
}
