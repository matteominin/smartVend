package com.smartvend.app.model.maintenance;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;

import com.smartvend.app.model.vendingmachine.ConcreteVendingMachine;

@Entity
public class MaintenanceReport implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    public String issueDescription;

    @Column(nullable = false)
    public Instant issueDate;

    @ManyToOne
    @JoinColumn(name = "machine_id")
    public ConcreteVendingMachine machine;

    public MaintenanceReport() {
        // Default constructor for JPA
        this.issueDate = Instant.now();
    }

    public MaintenanceReport(String issueDescription, Instant issueDate, ConcreteVendingMachine machine) {
        this.issueDescription = issueDescription;
        this.issueDate = issueDate;
        this.machine = machine;
    }

    public String getMachineId() {
        return machine != null ? machine.getId() : null;
    }
}
