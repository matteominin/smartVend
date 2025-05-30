package com.smartvend.app.services;

import com.smartvend.app.model.connection.Connection;
import com.smartvend.app.model.transaction.Transaction;
import com.smartvend.app.model.user.Customer;
import com.smartvend.app.model.vendingmachine.Inventory;
import com.smartvend.app.model.vendingmachine.Item;

import java.time.Instant;
import java.util.List;

import com.smartvend.app.dao.ConcreteVendingMachineDao;
import com.smartvend.app.dao.ConnectionDao;
import com.smartvend.app.dao.CustomerDao;
import com.smartvend.app.dao.InventoryDao;
import com.smartvend.app.dao.ItemDao;

public class CustomerService {
    private CustomerDao customerDao;
    private ConnectionDao connectionDao;
    private InventoryDao inventoryDao;
    private ItemDao itemDao;
    private ConcreteVendingMachineDao concreteVendingMachineDao;
    private TransactionService transactionService;

    public CustomerService(CustomerDao customerDao, ConnectionDao connectionDao, InventoryDao inventoryDao,
            ItemDao itemDao, ConcreteVendingMachineDao concreteVendingMachineDao,
            TransactionService transactionService) {
        this.customerDao = customerDao;
        this.connectionDao = connectionDao;
        this.inventoryDao = inventoryDao;
        this.itemDao = itemDao;
        this.concreteVendingMachineDao = concreteVendingMachineDao;
        this.transactionService = transactionService;
    }

    public Connection connect(long customerId, String machineId) {
        if (customerDao.getUserById(customerId) == null) {
            throw new IllegalArgumentException("Customer not found");
        }
        if (concreteVendingMachineDao.getMachineById(machineId) == null) {
            throw new IllegalArgumentException("Machine not found");
        }
        Connection connection = connectionDao.createConnection(new Connection(customerId, machineId, Instant.now()));
        return connection;
    }

    public List<Item> getInvenotry(Connection connection) {
        String machineId = connection.getMachineId();
        Inventory inventory = inventoryDao.getMachineInventory(machineId);
        if (inventory == null) {
            throw new IllegalArgumentException("Inventory not found");
        }

        List<Item> items = itemDao.getInventoryItems(inventory.id);
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("No items found in inventory");
        }
        return items;
    }

    public double checkBalance(long customerId, double amount) {
        Customer customer = customerDao.getUserById(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("User not found");
        }

        double balance = customer.getBalance();
        if (balance < amount) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        return balance;
    }

    public void updateBalance(long customerId, double amount) {
        Customer customer = customerDao.getUserById(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("User not found");
        }

        double newBalance = customer.getBalance() - amount;
        if (newBalance < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        customer.setBalance(newBalance);
        customerDao.updateCustomer(customer);
    }

    public List<Transaction> getTransactionHistory(long customerId) {
        Customer customer = customerDao.getUserById(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("User not found");
        }
        return transactionService.getCustomerTransactions(customerId);
    }
}
