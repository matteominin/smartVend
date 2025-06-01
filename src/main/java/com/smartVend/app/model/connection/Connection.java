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

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "machine_id", nullable = false)
    private String machineId;

    @Column(nullable = false)
    private Instant start;

    public Connection() {
        this.start = Instant.now();
    }

    public Connection(Long userId, String machineId, Instant start) {
        this.userId = userId;
        this.machineId = machineId;
        this.start = start;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getMachineId() { return machineId; }
    public void setMachineId(String machineId) { this.machineId = machineId; }

    public Instant getConnectionTime() { return start; }
    public void setConnectionTime(Instant start) { this.start = start; }

    
}
