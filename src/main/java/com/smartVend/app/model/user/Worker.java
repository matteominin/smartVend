package com.smartVend.app.model.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class Worker extends User {

    @Column(nullable = false)
    public boolean isActive;

    public Worker() {}

    public Worker(String id, String email, String name, String surname, String hashedPassword, boolean isActive) {
        super(id, email, name, surname, hashedPassword);
        this.isActive = isActive;
    }
}
