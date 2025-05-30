package com.smartvend.app.model.vendingmachine;

import jakarta.persistence.*;
import java.io.Serializable;
@Entity
public class VendingMachine implements Serializable {
    @Id
    public String modelNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public MachineType type;

    public VendingMachine() {
    }

    public VendingMachine(String modelNumber, MachineType type) {
        this.modelNumber = modelNumber;
        this.type = type;
    }
}
