package com.smartvend.app.model.user;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Worker implements Serializable {
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(nullable = false)
    public boolean isActive = true;

    public Worker() {
        // Default constructor for JPA
    }

    public Worker(User user) {
        this.user = user;
    }

    public Long getId() {
        return user.getId();
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
