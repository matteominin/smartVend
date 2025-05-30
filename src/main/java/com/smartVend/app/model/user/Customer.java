package com.smartVend.app.model.user;

import jakarta.persistence.*;

@Entity
public class Customer implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    public User user;

    @Column(nullable = false)
    public double balance;

    public Customer() {}
    public Customer(User user, double balance) {
        this.user = user;
        this.balance = balance;
    }
}
