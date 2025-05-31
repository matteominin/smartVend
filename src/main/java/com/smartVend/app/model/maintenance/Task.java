package com.smartvend.app.model.maintenance;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;

import com.smartvend.app.model.user.Admin;
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
    public Admin supervisor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public MaintenanceStatus status;

    @Column(nullable = false)
    public Instant assignedAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "report_id")
    public MaintenanceReport report;

    public Task() {
        // Default constructor for JPA
        this.assignedAt = Instant.now();
    }

    public Task(long id, Worker worker, Admin supervisor, MaintenanceStatus status, Instant assignedAt,
            MaintenanceReport report) {
        this.id = id;
        this.worker = worker;
        this.supervisor = supervisor;
        this.status = status;
        this.assignedAt = assignedAt;
        this.report = report;
    }

    public Task(Admin supervisor, Worker worker,
            MaintenanceReport report) {
        this.worker = worker;
        this.supervisor = supervisor;
        this.status = MaintenanceStatus.Assigned;
        this.assignedAt = Instant.now();
        this.report = report;
    }

    public void setStatus(MaintenanceStatus status) {
        this.status = status;
    }

    public Long getId() {
        return this.id;
    }

    public Worker getWorker() {
        return worker;
    }

    public String getDescription() {
        return report.getIssueDescription();
    }

    public MaintenanceStatus getStatus() {
        return status;
    }
}
