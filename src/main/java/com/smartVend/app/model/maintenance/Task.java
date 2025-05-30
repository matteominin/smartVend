package com.smartvend.app.model.maintenance;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import com.smartvend.app.model.user.Worker;

@Entity
public class Task implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "worker_id")
    public Worker worker;

    @ManyToOne
    @JoinColumn(name = "supervisor_id")
    public Worker supervisor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public MaintenanceStatus status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    public Date assignedAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "report_id")
    public MaintenanceReport report;

    public Task() {
    }

    public Task(Worker worker, Worker supervisor, MaintenanceStatus status, Date assignedAt, MaintenanceReport report) {
        this.worker = worker;
        this.supervisor = supervisor;
        this.status = status;
        this.assignedAt = assignedAt;
        this.report = report;
    }
}
