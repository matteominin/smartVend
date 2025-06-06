package com.smartvend.app.model.maintenance;

import java.io.Serializable;
import java.time.Instant;

import com.smartvend.app.model.user.Admin;
import com.smartvend.app.model.user.Worker;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

@Entity
public class Task implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "worker_id")
    private Worker worker;

    @ManyToOne
    @JoinColumn(name = "supervisor_id")
    private Admin supervisor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaintenanceStatus status;

    @OneToOne(optional = false)
    @JoinColumn(name = "report_id")
    private MaintenanceReport report;

    @Column(nullable = false)
    private Instant assignedAt;

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

    public void setId(Long id) {
        this.id = id;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    public Admin getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(Admin supervisor) {
        this.supervisor = supervisor;
    }

    public Instant getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(Instant assignedAt) {
        this.assignedAt = assignedAt;
    }

    public MaintenanceReport getReport() {
        return report;
    }

    public void setReport(MaintenanceReport report) {
        this.report = report;
    }

}
