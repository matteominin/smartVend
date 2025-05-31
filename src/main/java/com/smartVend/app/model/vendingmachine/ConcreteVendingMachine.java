package com.smartvend.app.model.vendingmachine;

import java.io.Serializable;
import java.time.Instant;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class ConcreteVendingMachine implements Serializable {
    @Id
    @Column(nullable = false, unique = true)
    private String serialNumber;
    @ManyToOne
    @JoinColumn(name = "vending_machine_model")
    private VendingMachine vendingMachine;
    @Column(nullable = false)
    private String location;
    @Column(nullable = false)
    private int capacity;
    @Column(nullable = false)
    private Instant lastMaintenance;
    @Column(nullable = false)
    private Instant createdAt;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MachineStatus status;
    @OneToMany(mappedBy = "machine", cascade = CascadeType.ALL)
    private Inventory inventory;
    public ConcreteVendingMachine() {
        // Default constructor for JPA
        this.createdAt = Instant.now();
        this.lastMaintenance = Instant.now();
        this.status = MachineStatus.Operative;
    }

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

    public MachineStatus getStatus() {
        return status;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public VendingMachine getVendingMachine() {
        return vendingMachine;
    }

    public void setVendingMachine(VendingMachine vendingMachine) {
        this.vendingMachine = vendingMachine;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Instant getLastMaintenance() {
        return lastMaintenance;
    }

    public void setLastMaintenance(Instant lastMaintenance) {
        this.lastMaintenance = lastMaintenance;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setStatus(MachineStatus status) {
        this.status = status;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
    
}
