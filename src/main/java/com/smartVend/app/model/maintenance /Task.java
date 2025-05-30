package com.smartVend.app.model;

import java.util.Date;

public class Task {
    private String id;
    private Date assignedAt;
    private Worker worker;
    private Admin supervisor;
    private MaintenanceStatus status;
    private MaintenanceReport report;

    public Task() {}

    public Task(String id, Date assignedAt, Worker worker, Admin supervisor, MaintenanceStatus status, MaintenanceReport report) {
        this.id = id;
        this.assignedAt = assignedAt;
        this.worker = worker;
        this.supervisor = supervisor;
        this.status = status;
        this.report = report;
    }

    // Getter e setter qui...
}
