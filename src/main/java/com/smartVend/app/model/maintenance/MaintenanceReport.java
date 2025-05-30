package com.smartvend.app.model.maintenance;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import com.smartvend.app.model.vendingmachine.VendingMachine;

@Entity
public class MaintenanceReport implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    public String issueDescription;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    public Date issueDate;

    @ManyToOne
    @JoinColumn(name = "machine_id")
    public VendingMachine machine;

    public MaintenanceReport() {
    }

    public MaintenanceReport(String issueDescription, Date issueDate, VendingMachine machine) {
        this.issueDescription = issueDescription;
        this.issueDate = issueDate;
        this.machine = machine;
    }
}
