package com.smartVend.app.model.user;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
public class User implements Serializable {
    @Id
    public String id;
    @Column(nullable = false, unique = true)
    public String email;
    @Column(nullable = false)
    public String name;
    @Column(nullable = false)
    public String surname;
    @Column(nullable = false)
    public String hashedPassword;

    public User() {}
    public User(String id, String email, String name, String surname, String hashedPassword) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.hashedPassword = hashedPassword;
    }
}
