package com.smartVend.app.model.connection;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
public class Connection implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    public String userId;

    @Column(nullable = false)
    public String machineId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    public Date start;

    public Connection() {}

    public Connection(String userId, String machineId, Date start) {
        this.userId = userId;
        this.machineId = machineId;
        this.start = start;
    }
}
