package com.smartVend.app.model.user;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
public class Worker implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    public User user;

    @Column(nullable = false)
    public boolean isActive;

    public Worker() {}
    public Worker(User user, boolean isActive) {
        this.user = user;
        this.isActive = isActive;
    }
}
