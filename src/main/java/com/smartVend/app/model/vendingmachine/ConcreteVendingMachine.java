package com.smartvend.app.model.vendingmachine;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
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

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    public Date lastMaintenance;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    public Date createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public MachineStatus status;

    @OneToMany(mappedBy = "machine", cascade = CascadeType.ALL)
    public List<Inventory> items;

    public ConcreteVendingMachine() {
    }

    public ConcreteVendingMachine(String serialNumber, VendingMachine vendingMachine, String location, int capacity,
            Date lastMaintenance, Date createdAt, MachineStatus status, List<Inventory> items) {
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
