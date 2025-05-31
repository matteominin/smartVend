package com.smartvend.app.dao;

import com.smartvend.app.model.maintenance.MaintenanceReport;

public interface MaintenanceDao {
    MaintenanceReport createReport(MaintenanceReport report);
}
