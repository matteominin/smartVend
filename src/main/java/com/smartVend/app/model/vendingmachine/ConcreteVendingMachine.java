package com.smartvend.app.model.vendingmachine;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.Instant;

@Entity
public class ConcreteVendingMachine implements Serializable {
    @Id
    @Column(nullable = false, unique = true)
    public String serialNumber;

    @ManyToOne
    @JoinColumn(name = "vending_machine_model")
    public VendingMachine vendingMachine;

    @Column(nullable = false)
    public String location;

    @Column(nullable = false)
    public int capacity;

    @Column(nullable = false)
    public Instant lastMaintenance;

    @Column(nullable = false)
    public Instant createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public MachineStatus status;

    @OneToMany(mappedBy = "machine", cascade = CascadeType.ALL)
    public Inventory inventory;

    public ConcreteVendingMachine(String serialNumber, VendingMachine vendingMachine, String location, int capacity,
            MachineStatus status, Inventory inventory) {
        this.serialNumber = serialNumber;
        this.vendingMachine = vendingMachine;
        this.location = location;
        this.capacity = capacity;
        this.createdAt = Instant.now();
        this.status = status;
        this.inventory = inventory;
    }

    public String getId() {
        return serialNumber;
    }
}
