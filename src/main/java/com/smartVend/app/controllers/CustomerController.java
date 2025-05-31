package com.smartvend.app.controllers;

import com.smartvend.app.model.connection.Connection;
import com.smartvend.app.model.vendingmachine.Item;
import com.smartvend.app.services.CustomerService;
import com.smartvend.app.services.UserService;

import java.util.List;

public class CustomerController extends UserController {
    private CustomerService customerService;

    public CustomerController(UserService userService, CustomerService customerService) {

        super(userService);
        this.customerService = customerService;
    }

    public Connection connect(String machineId, long userId) {
        return customerService.connect(userId, machineId);
    }

    public List<Item> getInventory(Connection connection) {
        return customerService.getInvenotry(connection);
    }

    public void buyItem(long connectionId, List<Long> itemIds) {
        customerService.buyItem(connectionId, itemIds);
    }

    public void rechargeBalance(long customerId, double amount) {
        customerService.updateBalance(customerId, amount);
    }

    public void disconnect(Connection connection) {
        customerService.disconnect(connection);
    }
}
