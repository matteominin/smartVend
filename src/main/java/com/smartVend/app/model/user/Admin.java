package com.smartvend.app.model.user;

import java.io.Serializable;

import com.smartvend.app.model.user.User.Role;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class Admin implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Admin() {
        // Default constructor for JPA
    }

    public Admin(long id, User user) {
        this.id = id;
        this.user = user;
        this.user.role = Role.Admin;
    }

    public Admin(User user) {
        this.user = user;
        this.user.role = Role.Admin;
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
        return user.email;
    }

    public void setEmail(String email) {
        user.email = email;
    }

    public String getName() {
        return user.name;
    }

    public void setName(String name) {
        user.name = name;
    }

    public String getSurname() {
        return user.surname;
    }

    public void setSurname(String surname) {
        user.surname = surname;
    }

    public String getPassword() {
        return user.password;
    }
}
