package com.smartvend.app.model.maintenance;

import java.util.Date;
import com.smartvend.app.model.vendingmachine.ConcreteVendingMachine;

public class MaintenanceReport {
    private String id;
    private String issueDescription;
    private Date issueDate;
    private ConcreteVendingMachine machine;

    public MaintenanceReport() {
    }

    public MaintenanceReport(String id, String issueDescription, Date issueDate, ConcreteVendingMachine machine) {
        this.id = id;
        this.issueDescription = issueDescription;
        this.issueDate = issueDate;
        this.machine = machine;
    }

    // Getter e setter qui...
}
