package com.smartvend.app.controllers;

import com.smartvend.app.services.MachineService;
import com.smartvend.app.model.maintenance.MaintenanceReport;
import com.smartvend.app.model.vendingmachine.MachineStatus;

public class MachineController {

    private MachineService machineService;

    public MachineController(MachineService machineService) {
        this.machineService = machineService;
    }

    public void deployItem(String machineId, Long itemId) {
        machineService.deployItem(machineId, itemId);
    }

    public MachineStatus getStatus(String machineId) {
        return machineService.getStatus(machineId);
    }

    public MaintenanceReport runDiagnostic(String machineId) {
        boolean hasIssue = Math.random() < 0.1;
        return machineService.runSelfDiagnostic(machineId, hasIssue);
    }
}
