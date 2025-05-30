package com.smartvend.app.model.vendingmachine;

import java.util.Date;

public class ConcreteVendingMachine {
    private String serialNumber;
    private String location;
    private int capacity;
    private Date lastMaintenance;
    private Date createdAt;
    private MachineStatus status;
    private Inventory inventory;

    public ConcreteVendingMachine() {
    }

    public ConcreteVendingMachine(String serialNumber, String location, int capacity,
            Date lastMaintenance, Date createdAt,
            MachineStatus status, Inventory inventory) {
        this.serialNumber = serialNumber;
        this.location = location;
        this.capacity = capacity;
        this.lastMaintenance = lastMaintenance;
        this.createdAt = createdAt;
        this.status = status;
        this.inventory = inventory;
    }

    // Getter e setter qui...
}
