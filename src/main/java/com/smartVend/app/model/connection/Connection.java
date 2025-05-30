package com.smartvend.app.model.connection;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
public class Connection implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "customer_id", nullable = false)
    public Long customerId;

    @Column(name = "machine_id", nullable = false)
    public String machineId;

    @Column(nullable = false)
    public Instant start;

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
}