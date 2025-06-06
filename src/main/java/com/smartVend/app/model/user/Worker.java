package com.smartvend.app.model.user;

import java.io.Serializable;

import com.smartvend.app.model.user.User.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class Worker implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    public boolean isActive = true;

    public Worker() {
        // Default constructor for JPA
    }

    public Worker(long id, User user) {
        this.id = id;
        this.user = user;
        this.user.role = Role.Worker;
        this.isActive = true;
    }

    public Worker(User user) {
        this.user = user;
        this.user.role = Role.Worker;
        this.isActive = true;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getFullName() {
        return user.getName() + " " + user.getSurname();
    }
}
