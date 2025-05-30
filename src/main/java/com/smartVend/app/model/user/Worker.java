package com.smartvend.app.model.user;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
public class Worker implements Serializable {
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    public User user;

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
}
