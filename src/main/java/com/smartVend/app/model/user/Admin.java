package com.smartVend.app.model.user;

import jakarta.persistence.Entity;


@Entity
public class Admin extends User {
    public Admin() {}
    public Admin(String id, String email, String name, String surname, String hashedPassword) {
        super(id, email, name, surname, hashedPassword);
    }
}
