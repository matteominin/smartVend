package com.smartvend.app.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.smartvend.app.model.maintenance.MaintenanceReport;
import com.smartvend.app.dao.impl.ConcreteVendingMachineDaoImpl;
import com.smartvend.app.dao.impl.ItemDaoImpl;
import com.smartvend.app.dao.impl.MaintenanceDaoImpl;
import com.smartvend.app.model.vendingmachine.ConcreteVendingMachine;
import com.smartvend.app.model.vendingmachine.Item;
import com.smartvend.app.model.vendingmachine.ItemType;
import com.smartvend.app.model.vendingmachine.MachineStatus;

public class MachinesServiceTest {
    @Mock
    private ItemDaoImpl itemDao;
    @Mock
    private ConcreteVendingMachineDaoImpl machineDao;
    @Mock
    private MaintenanceDaoImpl maintenanceDao;

    @InjectMocks
    private MachineService machineService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test deployItem with valid parameters")
    public void testDeployItemWithValidParameters() {
        when(itemDao.getItemById(1L))
                .thenReturn(new Item(1L, "Soda", "Refreshing drink", 500, 10, 1.50, 5, ItemType.Bottle));

        String result = machineService.deployItem("machineId", 1L);
        assertEquals("Item Soda in position 5 successfully deployed to machine machineId", result);
    }

    @Test
    @DisplayName("Test deployItem with invalid item ID")
    public void testDeployItemWithInvalidItemId() {
        when(itemDao.getItemById(0L)).thenReturn(null);

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> {
            machineService.deployItem("machineId", 0L);
        });
    }

    @Test
    @DisplayName("Test deployItem with null machine ID")
    public void testDeployItemWithNullMachineId() {
        when(itemDao.getItemById(1L))
                .thenReturn(new Item(1L, "Soda", "Refreshing drink", 500, 10, 1.50, 5, ItemType.Bottle));

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> {
            machineService.deployItem(null, 1L);
        });
    }

    @Test
    @DisplayName("Test reportIssue with valid report")
    public void testReportIssueWithValidReport() {
        MaintenanceReport report = new MaintenanceReport(
                "Issue description",
                null,
                new ConcreteVendingMachine("SN123", null, "Main Hall", 50, null, null));

        when(maintenanceDao.createReport(report)).thenReturn(report);

        MaintenanceReport result = machineService.reportIssue(report);
        assertEquals(report, result);
    }

    @Test
    @DisplayName("Test reportIssue with null report")
    public void testReportIssueWithNullReport() {
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> {
            machineService.reportIssue(null);
        });
    }

    @Test
    @DisplayName("Test runSelfDiagnostic with valid machine ID")
    public void testRunSelfDiagnosticWithValidMachineId() {
        ConcreteVendingMachine machine = new ConcreteVendingMachine("SN123", null, "Main Hall", 50, null, null);
        when(machineDao.findById("SN123")).thenReturn(machine);

        MaintenanceReport result = machineService.runSelfDiagnostic("SN123", false);
        assertEquals(null, result);
    }

    @Test

    @DisplayName("Test runSelfDiagnostic with valid machine ID")
    public void testRunSelfDiagnosticWithIssue() {
        ConcreteVendingMachine machine = new ConcreteVendingMachine("SN123", null,
                "Main Hall", 50, null, null);
        when(machineDao.findById("SN123")).thenReturn(machine);

        MaintenanceReport result = machineService.runSelfDiagnostic("SN123", true);
        assertEquals("Self-diagnostic issue detected", result.getIssueDescription());
    }

    @Test
    @DisplayName("Test getStatus with valid machine ID")
    public void testGetStatusWithValidMachineId() {
        ConcreteVendingMachine machine = new ConcreteVendingMachine("123", null, "Location A", 50,
                MachineStatus.Operative, null);
        when(machineDao.findById("123")).thenReturn(machine);
        MachineStatus status = machineService.getStatus("123");
        assertEquals(MachineStatus.Operative, status);
    }
}