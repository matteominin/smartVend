package com.smartVend.app.model.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class Customer extends User {
    @Column(nullable = false)
    public double balance;

    public Customer() {}
    public Customer(String id, String email, String name, String surname, String hashedPassword, double balance) {
        super(id, email, name, surname, hashedPassword);
        this.balance = balance;
    }
}