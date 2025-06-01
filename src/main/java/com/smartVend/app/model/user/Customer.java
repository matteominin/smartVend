package com.smartvend.app.model.user;

import java.io.Serializable;

import com.smartvend.app.model.user.User.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Customer implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;
    @Column(nullable = false)
    private double balance;

    public Customer() {
        // Default constructor for JPA
    }

    public Customer(Long id, User user, double balance) {
        this.id = id;
        this.user = user;
        this.balance = balance;
        this.user.role = Role.Customer;
    }

    public Customer(User user, double balance) {
        this.user = user;
        this.balance = balance;
        this.user.role = Role.Customer;
    }

    public Long getId() {
        return this.id;
    }

    public double getBalance() {
        return this.balance;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setBalance(double balance) {
        if (balance < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }
        this.balance = balance;
    }
}
