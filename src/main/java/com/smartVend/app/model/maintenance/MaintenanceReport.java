package com.smartvend.app.model.maintenance;

import java.io.Serializable;
import java.time.Instant;

import com.smartvend.app.model.vendingmachine.ConcreteVendingMachine;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class MaintenanceReport implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String issueDescription;

    @Column(nullable = false)
    private Instant issueDate;

    @ManyToOne
    @JoinColumn(name = "machine_id")
    private ConcreteVendingMachine machine;

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

    public String getIssueDescription() {
        return issueDescription;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setIssueDescription(String issueDescription) {
        this.issueDescription = issueDescription;
    }

    public Instant getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Instant issueDate) {
        this.issueDate = issueDate;
    }

    public ConcreteVendingMachine getMachine() {
        return machine;
    }

    public void setMachine(ConcreteVendingMachine machine) {
        this.machine = machine;
    }

}
