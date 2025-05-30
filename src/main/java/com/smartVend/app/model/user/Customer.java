package com.smartvend.app.model.user;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
public class Customer implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    public User user;

    @Column(nullable = false)
    public double balance;

    public Customer() {
        // Default constructor for JPA
    }

    public Customer(Long id, User user, double balance) {
        this.id = id;
        this.user = user;
        this.balance = balance;
    }

    public Customer(User user, double balance) {
        this.user = user;
        this.balance = balance;
    }

    public Long getId() {
        return this.id;
    }

    public double getBalance() {
        return this.balance;
    }

    public void setBalance(double balance) {
        if (balance < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }
        this.balance = balance;
    }
}
