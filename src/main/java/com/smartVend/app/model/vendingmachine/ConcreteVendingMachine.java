package com.smartvend.app.model.vendingmachine;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;

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
    public List<Inventory> items;

    public ConcreteVendingMachine() {
    }

    public ConcreteVendingMachine(String serialNumber, VendingMachine vendingMachine, String location, int capacity,
            Instant lastMaintenance, Instant createdAt, MachineStatus status, List<Inventory> items) {
        this.serialNumber = serialNumber;
        this.vendingMachine = vendingMachine;
        this.location = location;
        this.capacity = capacity;
        this.lastMaintenance = lastMaintenance;
        this.createdAt = createdAt;
        this.status = status;
        this.items = items;
    }
}
