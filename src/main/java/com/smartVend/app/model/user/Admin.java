package com.smartvend.app.model.user;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Admin implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Admin() {
        // Default constructor for JPA
    }

    public Admin(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
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
    public String getEmail() {
        return user != null ? user.getEmail() : null;
    }

    public void setEmail(String email) {
        if (user != null) {
            user.setEmail(email);
        }
    }

    public String getName() {
        return user != null ? user.getName() : null;
    }
    public void setName(String name) {
        if (user != null) user.setName(name);
    }

    public String getSurname() {
        return user != null ? user.getSurname() : null;
    }
    public void setSurname(String surname) {
        if (user != null) user.setSurname(surname);
    }

    public String getPassword() {
        return user != null ? user.getPassword() : null;
    }
    public void setPassword(String password) {
        if (user != null) user.setPassword(password);
    }


}
