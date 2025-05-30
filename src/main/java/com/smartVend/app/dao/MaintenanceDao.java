package com.smartvend.app.dao;

import com.smartvend.app.model.maintenance.MaintenanceReport;

public interface MaintenanceDao {
    void createReport(MaintenanceReport report);
}
