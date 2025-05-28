package com.smartVend.app.model;

import java.util.Date;

public class MaintenanceReport {
    private String id;
    private String issueDescription;
    private Date issueDate;
    private ConcreteVendingMachine machine;

    public MaintenanceReport() {}

    public MaintenanceReport(String id, String issueDescription, Date issueDate, ConcreteVendingMachine machine) {
        this.id = id;
        this.issueDescription = issueDescription;
        this.issueDate = issueDate;
        this.machine = machine;
    }

    // Getter e setter qui...
}
