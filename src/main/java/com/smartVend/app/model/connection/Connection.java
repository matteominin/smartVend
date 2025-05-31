package com.smartvend.app.model.connection;

import java.io.Serializable;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Connection implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "customer_id", nullable = false)
    private Long customerId;
    @Column(name = "machine_id", nullable = false)
    private String machineId;
    @Column(nullable = false)
    private Instant start;
    public Connection() {
        // Default constructor for JPA
        this.start = Instant.now();
    }

    public Connection(Long customerId, String machineId, Instant start) {
        this.customerId = customerId;
        this.machineId = machineId;
        this.start = start;
    }

    public Long getId() {
        return id;
    }

    public String getMachineId() {
        return machineId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public Instant getConnectionTime() {
        return start;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public Instant getStart() {
        return start;
    }

    public void setStart(Instant start) {
        this.start = start;
    }
    
}