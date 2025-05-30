package com.smartVend.app.model.user;

import jakarta.persistence.*;
import java.io.Serializable;


@Entity
public class Admin implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    public User user;

    public Admin() {}
    public Admin(User user) {
        this.user = user;
    }
}
