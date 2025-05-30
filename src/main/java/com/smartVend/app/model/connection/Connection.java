package com.smartvend.app.model.connection;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import com.smartvend.app.model.user.User;
import com.smartvend.app.model.vendingmachine.VendingMachine;

@Entity
public class Connection implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    public User user;

    @ManyToOne
    @JoinColumn(name = "machine_id")
    public VendingMachine machine;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    public Date start;

    public Connection() {
    }

    public Connection(User user, VendingMachine machine, Date start) {
        this.user = user;
        this.machine = machine;
        this.start = start;
    }
}
