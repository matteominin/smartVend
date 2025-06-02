package com.smartvend.app.model.connection;

import java.io.Serializable;
import java.time.Instant;

import com.smartvend.app.model.user.User;
import com.smartvend.app.model.vendingmachine.ConcreteVendingMachine;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class Connection implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne
    @JoinColumn(name = "machine_id", nullable = false)
    private ConcreteVendingMachine machine;

    @Column(nullable = false)
    private Instant start;

    public Connection() {
        this.start = Instant.now();
    }

    public Connection(User user, ConcreteVendingMachine machine, Instant start) {
        this.user = user;
        this.machine = machine;
        this.start = start;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return user.getId();
    }

    public void setUserId(User user) {
        this.user = user;
    }

    public String getMachineId() {
        return machine.getId();
    }

    public void setMachineId(ConcreteVendingMachine machine) {
        this.machine = machine;
    }

    public Instant getConnectionTime() {
        return start;
    }

    public void setConnectionTime(Instant start) {
        this.start = start;
    }
}
