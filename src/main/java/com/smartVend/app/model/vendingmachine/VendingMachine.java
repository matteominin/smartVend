package com.smartvend.app.model.vendingmachine;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;

@Entity
public class VendingMachine implements Serializable {
    @Id
    private String modelNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MachineType type;

    public VendingMachine() {
        // Default constructor for JPA
    }

    public VendingMachine(String modelNumber, MachineType type) {
        this.modelNumber = modelNumber;
        this.type = type;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    public MachineType getType() {
        return type;
    }

    public void setType(MachineType type) {
        this.type = type;
    }

}
