package com.smartvend.app.controllers;

import com.smartvend.app.model.connection.Connection;
import com.smartvend.app.model.transaction.Transaction;
import com.smartvend.app.model.user.Customer;
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

    public Customer getCustomerByUserId(long userId) {
        return customerService.getCustomerByUserId(userId);
    }

    public List<Item> getInventory(Connection connection) {
        return customerService.getInvenotry(connection);
    }

    public void buyItem(long connectionId, List<Long> itemIds) {
        customerService.buyItem(connectionId, itemIds);
    }

    public void rechargeBalance(long customerId, double amount) {
        customerService.rechargeBalance(customerId, amount);
    }

    public List<Transaction> getTransactionHistory(long customerId) {
        Customer customer = customerService.getCustomerById(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("User not found");
        }
        return customerService.getTransactionHistory(customerId);
    }

    public void disconnect(Connection connection) {
        customerService.disconnect(connection);
    }
}
