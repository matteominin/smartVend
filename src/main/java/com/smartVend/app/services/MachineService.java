package com.smartvend.app.services;

import com.smartvend.app.dao.impl.ItemDaoImpl;

import java.time.Instant;

import com.smartvend.app.dao.impl.ConcreteVendingMachineDaoImpl;
import com.smartvend.app.dao.impl.MaintenanceDaoImpl;
import com.smartvend.app.model.maintenance.MaintenanceReport;
import com.smartvend.app.model.vendingmachine.ConcreteVendingMachine;
import com.smartvend.app.model.vendingmachine.Item;
import com.smartvend.app.model.vendingmachine.MachineStatus;

public class MachineService {

    private ItemDaoImpl itemDao;
    private ConcreteVendingMachineDaoImpl machineDao;
    private MaintenanceDaoImpl maintenanceDao;

    public MachineService(ItemDaoImpl itemDao, ConcreteVendingMachineDaoImpl machineDao,
            MaintenanceDaoImpl maintenanceDao) {
        this.itemDao = itemDao;
        this.machineDao = machineDao;
        this.maintenanceDao = maintenanceDao;
    }

    public String deployItem(String machineId, long itemId) {
        if (machineDao == null) {
            throw new IllegalStateException("MachineDao is not initialized");
        }
        if (itemId <= 0) {
            throw new IllegalArgumentException("Invalid item ID");
        }
        if (machineId == null || machineId.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid machine ID");
        }

        Item item = itemDao.getItemById(itemId);

        return "Item " + item.getName() + " in position " + item.getPosition() + " successfully deployed to machine "
                + machineId;
    }

    public MaintenanceReport reportIssue(MaintenanceReport report) {
        if (maintenanceDao == null) {
            throw new IllegalStateException("MaintenanceDao is not initialized");
        }
        if (report == null) {
            throw new IllegalArgumentException("Maintenance report cannot be null");
        }
        if (report.getMachineId() == null || report.getMachineId().trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid machine ID in report");
        }

        maintenanceDao.createReport(report);
        return report;
    }

    public MaintenanceReport runSelfDiagnostic(String machineId, boolean hasIssue) {
        if (machineDao == null || maintenanceDao == null) {
            throw new IllegalStateException("Required DAOs are not initialized");
        }
        ConcreteVendingMachine machine = machineDao.findById(machineId);
        if (machine == null) {
            throw new IllegalArgumentException("Machine with ID " + machineId + " does not exist");
        }

        if (hasIssue) {
            MaintenanceReport report = new MaintenanceReport(
                    "Self-diagnostic issue detected",
                    Instant.now(),
                    machine);

            this.reportIssue(report);
            return report;
        }
        return null;
    }

    public MachineStatus getStatus(String machineId) {
        if (machineDao == null) {
            throw new IllegalStateException("MachineDao is not initialized");
        }
        ConcreteVendingMachine machine = machineDao.findById(machineId);
        if (machine == null) {
            throw new IllegalArgumentException("Machine with ID " + machineId + " does not exist");
        }
        MachineStatus status = machine.getStatus();

        return status;
    }
}