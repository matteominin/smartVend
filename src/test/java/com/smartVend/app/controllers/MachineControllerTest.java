package com.smartvend.app.controllers;

import com.smartvend.app.model.maintenance.MaintenanceReport;
import com.smartvend.app.model.vendingmachine.MachineStatus;
import com.smartvend.app.services.MachineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MachineControllerTest {

    private MachineService machineService;
    private MachineController machineController;

    @BeforeEach
    void setUp() {
        machineService = mock(MachineService.class);
        machineController = new MachineController(machineService);
    }

    @Test
    void testDeployItemDelegatesToService() {
        String machineId = "machine-1";
        Long itemId = 42L;

        machineController.deployItem(machineId, itemId);

        verify(machineService, times(1)).deployItem(machineId, itemId);
    }

    @Test
    void testGetStatusReturnsServiceResult() {
        String machineId = "machine-2";
        MachineStatus status = mock(MachineStatus.class);
        when(machineService.getStatus(machineId)).thenReturn(status);

        MachineStatus result = machineController.getStatus(machineId);

        assertSame(status, result);
        verify(machineService, times(1)).getStatus(machineId);
    }

    @Test
    void testRunDiagnosticDelegatesToService() {
        String machineId = "machine-3";
        MaintenanceReport report = mock(MaintenanceReport.class);
        // We don't know the value of hasIssue, so use anyBoolean()
        when(machineService.runSelfDiagnostic(eq(machineId), anyBoolean())).thenReturn(report);

        MaintenanceReport result = machineController.runDiagnostic(machineId);

        assertSame(report, result);
        ArgumentCaptor<Boolean> hasIssueCaptor = ArgumentCaptor.forClass(Boolean.class);
        verify(machineService, times(1)).runSelfDiagnostic(eq(machineId), hasIssueCaptor.capture());
        // hasIssue is random, just check it's a boolean
        assertNotNull(hasIssueCaptor.getValue());
    }
}
