package com.smartvend.app.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.smartvend.app.dao.ConnectionDao;
import com.smartvend.app.dao.CustomerDao;
import com.smartvend.app.dao.InventoryDao;
import com.smartvend.app.dao.ItemDao;
import com.smartvend.app.dao.ConcreteVendingMachineDao;

import com.smartvend.app.model.user.User;
import com.smartvend.app.model.vendingmachine.ConcreteVendingMachine;
import com.smartvend.app.model.vendingmachine.Inventory;
import com.smartvend.app.model.vendingmachine.MachineStatus;
import com.smartvend.app.model.vendingmachine.MachineType;
import com.smartvend.app.model.vendingmachine.VendingMachine;
import com.smartvend.app.model.user.Customer;
import com.smartvend.app.model.connection.Connection;
import com.smartvend.app.model.vendingmachine.Item;
import com.smartvend.app.model.vendingmachine.ItemType;

public class CustomerServiceTest {
    @Mock
    private CustomerDao customerDao;
    @Mock
    private ConnectionDao connectionDao;
    @Mock
    private InventoryDao inventoryDao;
    @Mock
    private ItemDao itemDao;
    @Mock
    private ConcreteVendingMachineDao concreteVendingMachineDao;

    @InjectMocks
    private CustomerService customerService;

    // Common mock objects that might be used across tests
    private Customer mockCustomer;
    private ConcreteVendingMachine mockConcreteVendingMachine;
    private Inventory mockInventory;
    private Item mockItem1;
    private Item mockItem2;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        mockCustomer = new Customer(new User(1L, "email@example.com", "Jhon", "Doe", "password"), 100.0);

        VendingMachine baseVendingMachine = new VendingMachine("model", MachineType.PizzoneFarcito);
        mockConcreteVendingMachine = new ConcreteVendingMachine(
                "SN123",
                baseVendingMachine,
                "Location",
                30,
                MachineStatus.Operative,
                null);

        mockItem1 = new Item(
                30L,
                "Soda",
                "Description",
                1,
                10,
                1.50,
                3,
                ItemType.Bottle);

        mockItem2 = new Item(
                31L,
                "Chips",
                "Description",
                1,
                5,
                2.00,
                4,
                ItemType.Snack);

        mockInventory = new Inventory(5L, mockConcreteVendingMachine, List.of(mockItem1, mockItem2), 40);
    }

    // --- Tests for connect method ---
    @Test
    @DisplayName("Test connect method success")
    public void testConnectSuccess() {
        long customerId = mockCustomer.getId();
        String machineId = mockConcreteVendingMachine.getId(); // Machine ID is String

        when(customerDao.getUserById(customerId)).thenReturn(mockCustomer);
        when(concreteVendingMachineDao.getMachineById(machineId)).thenReturn(mockConcreteVendingMachine);

        Connection createdConnection = new Connection(customerId, machineId, Instant.now());
        when(connectionDao.createConnection(any(Connection.class))).thenReturn(createdConnection);

        Connection resultConnection = customerService.connect(customerId, machineId);

        assertNotNull(resultConnection);
        assertEquals(customerId, resultConnection.getCustomerId());
        assertEquals(machineId, resultConnection.getMachineId());
        assertNotNull(resultConnection.getConnectionTime());

        verify(customerDao, times(1)).getUserById(customerId);
        verify(concreteVendingMachineDao, times(1)).getMachineById(machineId);
        verify(connectionDao, times(1)).createConnection(any(Connection.class));
        verifyNoMoreInteractions(customerDao, connectionDao, inventoryDao, itemDao, concreteVendingMachineDao);
    }

    @Test
    @DisplayName("Test connect method when customer not found")
    void testConnectCustomerNotFound() {
        long customerId = 99L; // Non-existent customer ID
        String machineId = mockConcreteVendingMachine.getId(); // Machine ID is String

        when(customerDao.getUserById(customerId)).thenReturn(null); // Customer not found

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            customerService.connect(customerId, machineId);
        });

        assertEquals("Customer not found", thrown.getMessage());
        verify(customerDao, times(1)).getUserById(customerId);
        verify(concreteVendingMachineDao, never()).getMachineById(anyString()); // Machine check should not happen
        verify(connectionDao, never()).createConnection(any(Connection.class)); // Connection not created
        verifyNoMoreInteractions(customerDao, connectionDao, inventoryDao, itemDao, concreteVendingMachineDao);
    }

    @Test
    @DisplayName("Test connect method when machine not found")
    void testConnectMachineNotFound() {
        long customerId = mockCustomer.getId();
        String machineId = "non-existent";

        when(customerDao.getUserById(customerId)).thenReturn(mockCustomer);
        when(concreteVendingMachineDao.getMachineById(machineId)).thenReturn(null);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            customerService.connect(customerId, machineId);
        });

        assertEquals("Machine not found", thrown.getMessage());
        verify(customerDao, times(1)).getUserById(customerId);
        verify(concreteVendingMachineDao, times(1)).getMachineById(machineId);
        verify(connectionDao, never()).createConnection(any(Connection.class));
        verifyNoMoreInteractions(customerDao, connectionDao, inventoryDao, itemDao, concreteVendingMachineDao);
    }

    // --- Tests for getInvenotry method ---
    @Test
    @DisplayName("Test getInventory method success with items found")
    void testGetInventorySuccess() {
        Connection tempConnection = new Connection(mockCustomer.getId(), mockConcreteVendingMachine.getId(),
                Instant.now());

        List<Item> expectedItems = Arrays.asList(mockItem1, mockItem2);

        when(inventoryDao.getMachineInventory(tempConnection.getMachineId())).thenReturn(mockInventory);
        when(itemDao.getInventoryItems(mockInventory.getId())).thenReturn(expectedItems);

        // Act
        List<Item> actualItems = customerService.getInvenotry(tempConnection);

        // Assert
        assertNotNull(actualItems);
        assertFalse(actualItems.isEmpty());
        assertEquals(2, actualItems.size());
        assertTrue(actualItems.contains(mockItem1));
        assertTrue(actualItems.contains(mockItem2));

        // Verify interactions
        verify(inventoryDao, times(1)).getMachineInventory(tempConnection.getMachineId());
        verify(itemDao, times(1)).getInventoryItems(mockInventory.getId());
        verifyNoMoreInteractions(customerDao, connectionDao, inventoryDao, itemDao, concreteVendingMachineDao);
    }

    @Test
    @DisplayName("Test getInventory method when inventory not found")
    void testGetInventoryNotFound() {
        Connection tempConnection = new Connection(mockCustomer.getId(), mockConcreteVendingMachine.getId(),
                Instant.now());
        // Mock behavior: inventoryDao.getMachineInventory now takes String machineId
        when(inventoryDao.getMachineInventory(tempConnection.getMachineId())).thenReturn(null);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            customerService.getInvenotry(tempConnection);
        });

        assertEquals("Inventory not found", thrown.getMessage());
        verify(inventoryDao, times(1)).getMachineInventory(tempConnection.getMachineId());
        verify(itemDao, never()).getInventoryItems(anyLong()); // Inventory ID is Long, so anyLong() is correct here
        verifyNoMoreInteractions(customerDao, connectionDao, inventoryDao, itemDao, concreteVendingMachineDao);
    }

    @Test
    @DisplayName("Test getInventory method when no items found in inventory")
    void testGetInventoryNoItemsFound() {
        Connection tempConnection = new Connection(mockCustomer.getId(), mockConcreteVendingMachine.getId(),
                Instant.now());
        // Mock behavior: inventoryDao.getMachineInventory now takes String machineId
        when(inventoryDao.getMachineInventory(tempConnection.getMachineId())).thenReturn(mockInventory);
        when(itemDao.getInventoryItems(mockInventory.getId())).thenReturn(Collections.emptyList());

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            customerService.getInvenotry(tempConnection);
        });

        assertEquals("No items found in inventory", thrown.getMessage());
        verify(inventoryDao, times(1)).getMachineInventory(tempConnection.getMachineId());
        verify(itemDao, times(1)).getInventoryItems(mockInventory.getId());
        verifyNoMoreInteractions(customerDao, connectionDao, inventoryDao, itemDao, concreteVendingMachineDao);
    }

    @Test
    @DisplayName("Test getInventory method when itemDao returns null for items")
    void testGetInventoryItemDaoReturnsNull() {
        Connection tempConnection = new Connection(mockCustomer.getId(), mockConcreteVendingMachine.getId(),
                Instant.now());
        // Mock behavior: inventoryDao.getMachineInventory now takes String machineId
        when(inventoryDao.getMachineInventory(tempConnection.getMachineId())).thenReturn(mockInventory);
        when(itemDao.getInventoryItems(mockInventory.getId())).thenReturn(null);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            customerService.getInvenotry(tempConnection);
        });

        assertEquals("No items found in inventory", thrown.getMessage());
        verify(inventoryDao, times(1)).getMachineInventory(tempConnection.getMachineId());
        verify(itemDao, times(1)).getInventoryItems(mockInventory.getId());
        verifyNoMoreInteractions(customerDao, connectionDao, inventoryDao, itemDao, concreteVendingMachineDao);
    }

    // --- Tests for checkBalance method ---
    @Test
    @DisplayName("Test checkBalance method success with sufficient balance")
    void testCheckBalanceSuccess() {
        long customerId = mockCustomer.getId();
        double amount = 50.0;

        when(customerDao.getUserById(customerId)).thenReturn(mockCustomer);

        double balance = customerService.checkBalance(customerId, amount);

        assertEquals(mockCustomer.getBalance(), balance);

        verify(customerDao, times(1)).getUserById(customerId);
        verifyNoMoreInteractions(customerDao, connectionDao, inventoryDao, itemDao, concreteVendingMachineDao);
    }

    @Test
    @DisplayName("Test checkBalance method when user not found")
    void testCheckBalanceUserNotFound() {
        long customerId = 99L;
        double amount = 10.0;

        when(customerDao.getUserById(customerId)).thenReturn(null);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            customerService.checkBalance(customerId, amount);
        });

        assertEquals("User not found", thrown.getMessage());
        verify(customerDao, times(1)).getUserById(customerId);
        verifyNoMoreInteractions(customerDao, connectionDao, inventoryDao, itemDao, concreteVendingMachineDao);
    }

    @Test
    @DisplayName("Test checkBalance method with insufficient balance")
    void testCheckBalanceInsufficientBalance() {
        long customerId = mockCustomer.getId();
        double amount = 150.0;

        when(customerDao.getUserById(customerId)).thenReturn(mockCustomer);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            customerService.checkBalance(customerId, amount);
        });

        assertEquals("Insufficient balance", thrown.getMessage());
        verify(customerDao, times(1)).getUserById(customerId);
        verifyNoMoreInteractions(customerDao, connectionDao, inventoryDao, itemDao, concreteVendingMachineDao);
    }
}
