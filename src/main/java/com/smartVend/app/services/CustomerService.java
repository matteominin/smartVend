package com.smartvend.app.services;

import java.util.ArrayList;
import java.util.List;

import com.smartvend.app.dao.impl.ConcreteVendingMachineDaoImpl;
import com.smartvend.app.dao.impl.ConnectionDaoImpl;
import com.smartvend.app.dao.impl.CustomerDaoImpl;
import com.smartvend.app.dao.impl.InventoryDaoImpl;
import com.smartvend.app.dao.impl.ItemDaoImpl;
import com.smartvend.app.model.connection.Connection;
import com.smartvend.app.model.transaction.PaymentMethod;
import com.smartvend.app.model.transaction.Transaction;
import com.smartvend.app.model.transaction.TransactionItem;
import com.smartvend.app.model.user.Customer;
import com.smartvend.app.model.vendingmachine.ConcreteVendingMachine;
import com.smartvend.app.model.vendingmachine.Inventory;
import com.smartvend.app.model.vendingmachine.Item;

public class CustomerService {
    private CustomerDaoImpl customerDao;
    private ConnectionDaoImpl connectionDao;
    private InventoryDaoImpl inventoryDao;
    private ItemDaoImpl itemDao;
    private ConcreteVendingMachineDaoImpl concreteVendingMachineDao;
    private TransactionService transactionService;

    public CustomerService(CustomerDaoImpl customerDao, ConnectionDaoImpl connectionDao, InventoryDaoImpl inventoryDao,
            ItemDaoImpl itemDao, ConcreteVendingMachineDaoImpl concreteVendingMachineDao,
            TransactionService transactionService) {
        this.customerDao = customerDao;
        this.connectionDao = connectionDao;
        this.inventoryDao = inventoryDao;
        this.itemDao = itemDao;
        this.concreteVendingMachineDao = concreteVendingMachineDao;
        this.transactionService = transactionService;
    }

    public Connection connect(Long customerId, String machineId) {
        if (customerDao.getCustomerById(customerId) == null) {
            throw new IllegalArgumentException("Customer not found");
        }
        if (concreteVendingMachineDao.findById(machineId) == null) {
            throw new IllegalArgumentException("Machine not found");
        }
        Connection connection = connectionDao.createConnection(customerId, machineId);
        return connection;
    }

    public List<Item> getInvenotry(Connection connection) {
        String machineId = connection.getMachineId();
        Inventory inventory = inventoryDao.getMachineInventory(machineId);
        if (inventory == null) {
            throw new IllegalArgumentException("Inventory not found");
        }

        List<Item> items = itemDao.getInventoryItems(inventory.getId());
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("No items found in inventory");
        }
        return items;
    }

    public double checkBalance(long customerId, double amount) {
        Customer customer = customerDao.getCustomerById(customerId);
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
        Customer customer = customerDao.getCustomerById(customerId);
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
        Customer customer = customerDao.getCustomerById(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("User not found");
        }
        return transactionService.getCustomerTransactions(customerId);
    }

    public void disconnect(Connection connectionId) {
        if (connectionId == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }
        Connection connection = connectionDao.getConnectionById(connectionId.getId());
        if (connection == null) {
            throw new IllegalArgumentException("Connection not found");
        }
        connectionDao.deleteConnection(connection.getId());
    }

    public Transaction buyItem(long connectionId, List<Long> itemIds) {
        // Get connection
        Connection connection = connectionDao.getConnectionById(connectionId);
        if (connection == null) {
            throw new IllegalArgumentException("Connection not found");
        }

        // Get customer
        long customerId = connection.getUserId();
        Customer customer = customerDao.getCustomerById(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found");
        }

        // Get machine for transaction items
        ConcreteVendingMachine machine = concreteVendingMachineDao.findById(connection.getMachineId());
        if (machine == null) {
            throw new IllegalArgumentException("Machine not found");
        }

        // Calculate total price and create transaction items (but don't update
        // quantities yet)
        double totalPrice = 0.0;
        List<TransactionItem> transactionItems = new ArrayList<>();
        List<Item> itemsToUpdate = new ArrayList<>(); // Track items that need quantity updates

        for (Long itemId : itemIds) {
            Item item = itemDao.getItemById(itemId);
            if (item == null) {
                throw new IllegalArgumentException("Item not found: " + itemId);
            }

            // Check if item is available
            if (item.getQuantity() <= 0) {
                throw new IllegalArgumentException("Item not available");
            }

            totalPrice += item.getPrice();
            transactionItems.add(new TransactionItem(machine, item, 1));
            itemsToUpdate.add(item); // Add to list for later update
        }

        // Check balance BEFORE updating anything
        double initialBalance = checkBalance(customerId, totalPrice);

        // Now that we know balance is sufficient, update item quantities
        for (Item item : itemsToUpdate) {
            item.setQuantity(item.getQuantity() - 1);
            itemDao.updateItem(item);
        }

        // Update customer balance
        updateBalance(customerId, totalPrice);
        double finalBalance = initialBalance - totalPrice;

        // Create and return transaction
        Transaction transaction = new Transaction(
                customer,
                PaymentMethod.Wallet,
                initialBalance,
                finalBalance,
                transactionItems);

        return transactionService.createTransaction(transaction);
    }
}
