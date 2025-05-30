package com.smartvend.app.model.maintenance;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import com.smartvend.app.model.vendingmachine.VendingMachine;

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
    public VendingMachine machine;

    public MaintenanceReport() {
    }

    public MaintenanceReport(String issueDescription, Instant issueDate, VendingMachine machine) {
        this.issueDescription = issueDescription;
        this.issueDate = issueDate;
        this.machine = machine;
    }
}
