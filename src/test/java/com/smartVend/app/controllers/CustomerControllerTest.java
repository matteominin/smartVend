package com.smartvend.app.controllers;

import com.smartvend.app.model.connection.Connection;
import com.smartvend.app.model.vendingmachine.Item;
import com.smartvend.app.services.CustomerService;
import com.smartvend.app.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerControllerTest {

    private UserService userService;
    private CustomerService customerService;
    private CustomerController customerController;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        customerService = mock(CustomerService.class);
        customerController = new CustomerController(userService, customerService);
    }

    @Test
    void testConnect() {
        String machineId = "machine123";
        long userId = 42L;
        Connection connection = mock(Connection.class);

        when(customerService.connect(userId, machineId)).thenReturn(connection);

        Connection result = customerController.connect(machineId, userId);

        assertEquals(connection, result);
        verify(customerService).connect(userId, machineId);
    }

    @Test
    void testGetInventory() {
        Connection connection = mock(Connection.class);
        List<Item> items = Arrays.asList(mock(Item.class), mock(Item.class));

        when(customerService.getInvenotry(connection)).thenReturn(items);

        List<Item> result = customerController.getInventory(connection);

        assertEquals(items, result);
        verify(customerService).getInvenotry(connection);
    }

    @Test
    void testBuyItem() {
        long connectionId = 1L;
        List<Long> itemIds = Arrays.asList(10L, 20L);

        customerController.buyItem(connectionId, itemIds);

        verify(customerService).buyItem(connectionId, itemIds);
    }

    @Test
    void testRechargeBalance() {
        long customerId = 5L;
        double amount = 100.0;

        customerController.rechargeBalance(customerId, amount);

        verify(customerService).updateBalance(customerId, amount);
    }

    @Test
    void testDisconnect() {
        Connection connection = mock(Connection.class);

        customerController.disconnect(connection);

        verify(customerService).disconnect(connection);
    }
}
