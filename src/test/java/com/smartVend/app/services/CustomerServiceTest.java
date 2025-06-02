package com.smartvend.app.services;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.smartvend.app.dao.impl.ConcreteVendingMachineDaoImpl;
import com.smartvend.app.dao.impl.ConnectionDaoImpl;
import com.smartvend.app.dao.impl.CustomerDaoImpl;
import com.smartvend.app.dao.impl.InventoryDaoImpl;
import com.smartvend.app.dao.impl.ItemDaoImpl;
import com.smartvend.app.model.connection.Connection;
import com.smartvend.app.model.transaction.PaymentMethod;
import com.smartvend.app.model.transaction.Transaction;
import com.smartvend.app.model.user.Customer;
import com.smartvend.app.model.user.User;
import com.smartvend.app.model.vendingmachine.ConcreteVendingMachine;
import com.smartvend.app.model.vendingmachine.Inventory;
import com.smartvend.app.model.vendingmachine.Item;
import com.smartvend.app.model.vendingmachine.ItemType;
import com.smartvend.app.model.vendingmachine.MachineStatus;
import com.smartvend.app.model.vendingmachine.MachineType;
import com.smartvend.app.model.vendingmachine.VendingMachine;

public class CustomerServiceTest {
        @Mock
        private CustomerDaoImpl customerDao;
        @Mock
        private ConnectionDaoImpl connectionDao;
        @Mock
        private InventoryDaoImpl inventoryDao;
        @Mock
        private ItemDaoImpl itemDao;
        @Mock
        private ConcreteVendingMachineDaoImpl concreteVendingMachineDao;
        @Mock
        private TransactionService transactionService;

        @InjectMocks
        private CustomerService customerService;

        private Customer mockCustomer;
        private ConcreteVendingMachine mockConcreteVendingMachine;
        private Inventory mockInventory;
        private Item mockItem1;
        private Item mockItem2;
        private Transaction mockTransaction1;
        private Transaction mockTransaction2;

        @BeforeEach
        public void setUp() {
                MockitoAnnotations.openMocks(this);

                mockCustomer = new Customer(2L, new User(1L, "email@example.com", "Jhon", "Doe", "password"), 100.0);

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

                // Initialize new mock transactions
                mockTransaction1 = new Transaction(
                                1L,
                                mockCustomer,
                                PaymentMethod.Card,
                                20,
                                18,
                                null);
                mockTransaction2 = new Transaction(
                                2L,
                                mockCustomer,
                                PaymentMethod.Cash,
                                30.0,
                                25.0,
                                null);
        }

        // --- Tests for connect method ---
        @Test
        @DisplayName("Test connect method success")
        public void testConnectSuccess() {
                long userId = mockCustomer.getUser().getId();
                String machineId = mockConcreteVendingMachine.getId();

                when(customerDao.getCustomerById(anyLong())).thenReturn(mockCustomer);
                when(concreteVendingMachineDao.findById(anyString())).thenReturn(mockConcreteVendingMachine);

                Connection createdConnection = new Connection(mockCustomer.getUser(), mockConcreteVendingMachine,
                                Instant.now());
                when(connectionDao.createConnection(userId, machineId))
                                .thenReturn(createdConnection);

                Connection resultConnection = customerService.connect(userId, machineId);

                assertNotNull(resultConnection);
                assertEquals(userId, resultConnection.getUserId());
                assertEquals(machineId, resultConnection.getMachineId());
                assertNotNull(resultConnection.getConnectionTime());
        }

        @Test
        @DisplayName("Test connect method when customer not found")
        void testConnectCustomerNotFound() {
                long customerId = 99L;
                String machineId = mockConcreteVendingMachine.getId();

                when(customerDao.getCustomerById(customerId)).thenReturn(null);

                IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                        customerService.connect(customerId, machineId);
                });

                assertEquals("Customer not found", thrown.getMessage());
                verify(customerDao, times(1)).getCustomerById(customerId);
                verify(concreteVendingMachineDao, never()).findById(anyString());
                verify(connectionDao, never()).createConnection(anyLong(), anyString());
                verifyNoMoreInteractions(customerDao, connectionDao, inventoryDao, itemDao, concreteVendingMachineDao,
                                transactionService);
        }

        @Test
        @DisplayName("Test connect method when machine not found")
        void testConnectMachineNotFound() {
                long customerId = mockCustomer.getId();
                String machineId = "non-existent";

                when(customerDao.getCustomerById(customerId)).thenReturn(mockCustomer);
                when(concreteVendingMachineDao.findById(machineId)).thenReturn(null);

                IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                        customerService.connect(customerId, machineId);
                });

                assertEquals("Machine not found", thrown.getMessage());
                verify(customerDao, times(1)).getCustomerById(customerId);
                verify(concreteVendingMachineDao, times(1)).findById(machineId);
                verify(connectionDao, never()).createConnection(anyLong(), anyString());
                verifyNoMoreInteractions(customerDao, connectionDao, inventoryDao, itemDao, concreteVendingMachineDao,
                                transactionService);
        }

        // --- Tests for getInvenotry method ---
        @Test
        @DisplayName("Test getInventory method success with items found")
        void testGetInventorySuccess() {
                Connection tempConnection = new Connection(mockCustomer.getUser(), mockConcreteVendingMachine,
                                Instant.now());

                List<Item> expectedItems = Arrays.asList(mockItem1, mockItem2);

                when(inventoryDao.getMachineInventory(tempConnection.getMachineId())).thenReturn(mockInventory);
                when(itemDao.getInventoryItems(mockInventory.getId())).thenReturn(expectedItems);

                List<Item> actualItems = customerService.getInvenotry(tempConnection);

                assertNotNull(actualItems);
                assertFalse(actualItems.isEmpty());
                assertEquals(2, actualItems.size());
                assertTrue(actualItems.contains(mockItem1));
                assertTrue(actualItems.contains(mockItem2));

                verify(inventoryDao, times(1)).getMachineInventory(tempConnection.getMachineId());
                verify(itemDao, times(1)).getInventoryItems(mockInventory.getId());
                verifyNoMoreInteractions(customerDao, connectionDao, inventoryDao, itemDao, concreteVendingMachineDao,
                                transactionService);
        }

        @Test
        @DisplayName("Test getInventory method when inventory not found")
        void testGetInventoryNotFound() {
                Connection tempConnection = new Connection(mockCustomer.getUser(), mockConcreteVendingMachine,
                                Instant.now());
                when(inventoryDao.getMachineInventory(tempConnection.getMachineId())).thenReturn(null);

                IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                        customerService.getInvenotry(tempConnection);
                });

                assertEquals("Inventory not found", thrown.getMessage());
                verify(inventoryDao, times(1)).getMachineInventory(tempConnection.getMachineId());
                verify(itemDao, never()).getInventoryItems(anyLong());
                verifyNoMoreInteractions(customerDao, connectionDao, inventoryDao, itemDao, concreteVendingMachineDao,
                                transactionService);
        }

        @Test
        @DisplayName("Test getInventory method when no items found in inventory")
        void testGetInventoryNoItemsFound() {
                Connection tempConnection = new Connection(mockCustomer.getUser(), mockConcreteVendingMachine,
                                Instant.now());
                when(inventoryDao.getMachineInventory(tempConnection.getMachineId())).thenReturn(mockInventory);
                when(itemDao.getInventoryItems(mockInventory.getId())).thenReturn(Collections.emptyList());

                IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                        customerService.getInvenotry(tempConnection);
                });

                assertEquals("No items found in inventory", thrown.getMessage());
                verify(inventoryDao, times(1)).getMachineInventory(tempConnection.getMachineId());
                verify(itemDao, times(1)).getInventoryItems(mockInventory.getId());
                verifyNoMoreInteractions(customerDao, connectionDao, inventoryDao, itemDao, concreteVendingMachineDao,
                                transactionService);
        }

        @Test
        @DisplayName("Test getInventory method when itemDao returns null for items")
        void testGetInventoryItemDaoReturnsNull() {
                Connection tempConnection = new Connection(mockCustomer.getUser(), mockConcreteVendingMachine,
                                Instant.now());
                when(inventoryDao.getMachineInventory(tempConnection.getMachineId())).thenReturn(mockInventory);
                when(itemDao.getInventoryItems(mockInventory.getId())).thenReturn(null);

                IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                        customerService.getInvenotry(tempConnection);
                });

                assertEquals("No items found in inventory", thrown.getMessage());
                verify(inventoryDao, times(1)).getMachineInventory(tempConnection.getMachineId());
                verify(itemDao, times(1)).getInventoryItems(mockInventory.getId());
                verifyNoMoreInteractions(customerDao, connectionDao, inventoryDao, itemDao, concreteVendingMachineDao,
                                transactionService);
        }

        // --- Tests for checkBalance method ---
        @Test
        @DisplayName("Test checkBalance method success with sufficient balance")
        void testCheckBalanceSuccess() {
                long customerId = mockCustomer.getId();
                double amount = 50.0;

                when(customerDao.getCustomerById(customerId)).thenReturn(mockCustomer);

                double balance = customerService.checkBalance(customerId, amount);

                assertEquals(mockCustomer.getBalance(), balance);

                verify(customerDao, times(1)).getCustomerById(customerId);
                verifyNoMoreInteractions(customerDao, connectionDao, inventoryDao, itemDao, concreteVendingMachineDao,
                                transactionService);
        }

        @Test
        @DisplayName("Test checkBalance method when user not found")
        void testCheckBalanceUserNotFound() {
                long customerId = 99L;
                double amount = 10.0;

                when(customerDao.getCustomerById(customerId)).thenReturn(null);

                IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                        customerService.checkBalance(customerId, amount);
                });

                assertEquals("User not found", thrown.getMessage());
                verify(customerDao, times(1)).getCustomerById(customerId);
                verifyNoMoreInteractions(customerDao, connectionDao, inventoryDao, itemDao, concreteVendingMachineDao,
                                transactionService);
        }

        @Test
        @DisplayName("Test checkBalance method with insufficient balance")
        void testCheckBalanceInsufficientBalance() {
                long customerId = mockCustomer.getId();
                double amount = 150.0;

                when(customerDao.getCustomerById(customerId)).thenReturn(mockCustomer);

                IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                        customerService.checkBalance(customerId, amount);
                });

                assertEquals("Insufficient balance", thrown.getMessage());
                verify(customerDao, times(1)).getCustomerById(customerId);
                verifyNoMoreInteractions(customerDao, connectionDao, inventoryDao, itemDao, concreteVendingMachineDao,
                                transactionService);
        }

        // --- Tests for updateBalance method ---
        @Test
        @DisplayName("Test updateBalance method success")
        public void testUpdateBalanceSuccess() {
                long customerId = mockCustomer.getId();
                double initialBalance = mockCustomer.getBalance(); // 100.0
                double amountToDeduct = 30.0;
                double expectedNewBalance = initialBalance - amountToDeduct; // 70.0

                when(customerDao.getCustomerById(customerId)).thenReturn(mockCustomer);

                customerService.updateBalance(customerId, amountToDeduct);

                assertEquals(expectedNewBalance, mockCustomer.getBalance());
                verify(customerDao, times(1)).getCustomerById(customerId);
                verify(customerDao, times(1)).updateCustomer(mockCustomer);
                verifyNoMoreInteractions(customerDao, connectionDao, inventoryDao, itemDao, concreteVendingMachineDao,
                                transactionService);
        }

        @Test
        @DisplayName("Test updateBalance method when user not found")
        void testUpdateBalanceUserNotFound() {
                long customerId = 99L;
                double amountToDeduct = 10.0;

                when(customerDao.getCustomerById(customerId)).thenReturn(null);

                IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                        customerService.updateBalance(customerId, amountToDeduct);
                });

                assertEquals("User not found", thrown.getMessage());
                verify(customerDao, times(1)).getCustomerById(customerId);
                verify(customerDao, never()).updateCustomer(any(Customer.class));
                verifyNoMoreInteractions(customerDao, connectionDao, inventoryDao, itemDao, concreteVendingMachineDao,
                                transactionService);
        }

        @Test
        @DisplayName("Test updateBalance method with insufficient balance")
        void testUpdateBalanceInsufficientBalance() {
                long customerId = mockCustomer.getId();
                double initialBalance = mockCustomer.getBalance(); // 100.0
                double amountToDeduct = 150.0; // More than initial balance

                when(customerDao.getCustomerById(customerId)).thenReturn(mockCustomer);

                IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                        customerService.updateBalance(customerId, amountToDeduct);
                });

                assertEquals("Insufficient balance", thrown.getMessage());
                assertEquals(initialBalance, mockCustomer.getBalance()); // Balance should not change
                verify(customerDao, times(1)).getCustomerById(customerId);
                verify(customerDao, never()).updateCustomer(any(Customer.class));
                verifyNoMoreInteractions(customerDao, connectionDao, inventoryDao, itemDao, concreteVendingMachineDao,
                                transactionService);
        }

        // --- Tests for getTransactionHistory method ---
        @Test
        @DisplayName("Test getTransactionHistory method success")
        void testGetTransactionHistorySuccess() {
                long customerId = mockCustomer.getId();
                List<Transaction> expectedTransactions = Arrays.asList(mockTransaction1, mockTransaction2);

                when(customerDao.getCustomerById(customerId)).thenReturn(mockCustomer);
                when(transactionService.getCustomerTransactions(customerId)).thenReturn(expectedTransactions);

                List<Transaction> actualTransactions = customerService.getTransactionHistory(customerId);

                assertNotNull(actualTransactions);
                assertFalse(actualTransactions.isEmpty());
                assertEquals(2, actualTransactions.size());
                assertTrue(actualTransactions.contains(mockTransaction1));
                assertTrue(actualTransactions.contains(mockTransaction2));

                verify(customerDao, times(1)).getCustomerById(customerId);
                verify(transactionService, times(1)).getCustomerTransactions(customerId);
                verifyNoMoreInteractions(customerDao, connectionDao, inventoryDao, itemDao, concreteVendingMachineDao,
                                transactionService);
        }

        @Test
        @DisplayName("Test getTransactionHistory method when customer not found")
        void testGetTransactionHistoryCustomerNotFound() {
                long customerId = 99L;

                when(customerDao.getCustomerById(customerId)).thenReturn(null);

                IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                        customerService.getTransactionHistory(customerId);
                });

                assertEquals("User not found", thrown.getMessage());
                verify(customerDao, times(1)).getCustomerById(customerId);
                verify(transactionService, never()).getCustomerTransactions(anyLong());
                verifyNoMoreInteractions(customerDao, connectionDao, inventoryDao, itemDao, concreteVendingMachineDao,
                                transactionService);
        }

        @Test
        @DisplayName("Test getTransactionHistory method when no transactions found")
        void testGetTransactionHistoryNoTransactionsFound() {
                long customerId = mockCustomer.getId();
                List<Transaction> expectedTransactions = Collections.emptyList();

                when(customerDao.getCustomerById(customerId)).thenReturn(mockCustomer);
                when(transactionService.getCustomerTransactions(customerId)).thenReturn(expectedTransactions);

                List<Transaction> actualTransactions = customerService.getTransactionHistory(customerId);

                assertNotNull(actualTransactions);
                assertTrue(actualTransactions.isEmpty());
                assertEquals(0, actualTransactions.size());

                verify(customerDao, times(1)).getCustomerById(customerId);
                verify(transactionService, times(1)).getCustomerTransactions(customerId);
                verifyNoMoreInteractions(customerDao, connectionDao, inventoryDao, itemDao, concreteVendingMachineDao,
                                transactionService);
        }

        // --- Tests for disconnect method ---
        @Test
        @DisplayName("Test disconnect method success")
        void testDisconnectSuccess() {
                // Arrange
                Connection inputConnection = new Connection(mockCustomer.getUser(), mockConcreteVendingMachine,
                                Instant.now());
                Connection foundConnection = new Connection(mockCustomer.getUser(), mockConcreteVendingMachine,
                                Instant.now());

                when(connectionDao.getConnectionById(inputConnection.getId())).thenReturn(foundConnection);

                // Act
                customerService.disconnect(inputConnection);

                // Assert
                verify(connectionDao, times(1)).getConnectionById(inputConnection.getId());
                verify(connectionDao, times(1)).deleteConnection(foundConnection.getId());
                verifyNoMoreInteractions(customerDao, connectionDao, inventoryDao, itemDao, concreteVendingMachineDao,
                                transactionService);
        }

        @Test
        @DisplayName("Test disconnect method when connection parameter is null")
        void testDisconnectConnectionParameterNull() {
                // Act & Assert
                IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                        customerService.disconnect(null);
                });

                assertEquals("Connection cannot be null", thrown.getMessage());
                verify(connectionDao, never()).getConnectionById(anyLong());
                verify(connectionDao, never()).deleteConnection(any(Long.class));
                verifyNoMoreInteractions(customerDao, connectionDao, inventoryDao, itemDao, concreteVendingMachineDao,
                                transactionService);
        }

        @Test
        @DisplayName("Test disconnect method when connection not found in database")
        void testDisconnectConnectionNotFound() {
                // Arrange
                Connection inputConnection = new Connection(mockCustomer.getUser(), mockConcreteVendingMachine,
                                Instant.now());

                when(connectionDao.getConnectionById(inputConnection.getId())).thenReturn(null);

                // Act & Assert
                IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                        customerService.disconnect(inputConnection);
                });

                assertEquals("Connection not found", thrown.getMessage());
                verify(connectionDao, times(1)).getConnectionById(inputConnection.getId());
                verify(connectionDao, never()).deleteConnection(any(Long.class));
                verifyNoMoreInteractions(customerDao, connectionDao, inventoryDao, itemDao, concreteVendingMachineDao,
                                transactionService);
        }

        // --- Tests for buyItem method ---
        @Test
        @DisplayName("Test buyItem method success with single item")
        void testBuyItemSuccessSingleItem() {
                // Arrange
                long connectionId = 1L;
                List<Long> itemIds = Arrays.asList(30L);
                Connection mockConnection = new Connection(mockCustomer.getUser(), mockConcreteVendingMachine,
                                Instant.now());
                Transaction expectedTransaction = new Transaction(mockCustomer, PaymentMethod.Wallet, 100.0, 98.5,
                                Arrays.asList());

                when(connectionDao.getConnectionById(connectionId)).thenReturn(mockConnection);
                when(customerDao.getCustomerById(mockCustomer.getId())).thenReturn(mockCustomer);
                when(customerDao.getCustomerByUserId(mockCustomer.getUser().getId()))
                                .thenReturn(mockCustomer);
                when(concreteVendingMachineDao.findById(mockConcreteVendingMachine.getId()))
                                .thenReturn(mockConcreteVendingMachine);
                when(itemDao.getItemById(30L)).thenReturn(mockItem1);
                when(transactionService.createTransaction(any(Transaction.class))).thenReturn(expectedTransaction);

                // Act
                Transaction result = customerService.buyItem(connectionId, itemIds);

                // Assert
                assertNotNull(result);
                assertEquals(expectedTransaction, result);
                assertEquals(9, mockItem1.getQuantity()); // Original quantity was 10, should be decremented by 1

                verify(connectionDao, times(1)).getConnectionById(connectionId);
                verify(customerDao, times(2)).getCustomerById(mockCustomer.getId()); // Called in checkBalance and main
                                                                                     // method
                verify(concreteVendingMachineDao, times(1)).findById(mockConcreteVendingMachine.getId());
                verify(itemDao, times(1)).getItemById(30L);
                verify(itemDao, times(1)).updateItem(mockItem1);
                verify(customerDao, times(1)).updateCustomer(mockCustomer); // Called in updateBalance
                verify(transactionService, times(1)).createTransaction(any(Transaction.class));
        }

        @Test
        @DisplayName("Test buyItem method success with multiple items")
        void testBuyItemSuccessMultipleItems() {
                // Arrange
                long connectionId = 1L;
                List<Long> itemIds = Arrays.asList(30L, 31L);
                Connection mockConnection = new Connection(mockCustomer.getUser(), mockConcreteVendingMachine,
                                Instant.now());
                Transaction expectedTransaction = new Transaction(mockCustomer, PaymentMethod.Wallet, 100.0, 96.5,
                                Arrays.asList());

                when(connectionDao.getConnectionById(connectionId)).thenReturn(mockConnection);
                when(customerDao.getCustomerById(mockCustomer.getId())).thenReturn(mockCustomer);
                when(customerDao.getCustomerByUserId(mockCustomer.getUser().getId()))
                                .thenReturn(mockCustomer);
                when(concreteVendingMachineDao.findById(mockConcreteVendingMachine.getId()))
                                .thenReturn(mockConcreteVendingMachine);
                when(itemDao.getItemById(30L)).thenReturn(mockItem1); // Price: 1.50
                when(itemDao.getItemById(31L)).thenReturn(mockItem2); // Price: 2.00
                when(transactionService.createTransaction(any(Transaction.class))).thenReturn(expectedTransaction);

                // Act
                Transaction result = customerService.buyItem(connectionId, itemIds);

                // Assert
                assertNotNull(result);
                assertEquals(expectedTransaction, result);
                assertEquals(9, mockItem1.getQuantity()); // Original quantity was 10, should be decremented by 1
                assertEquals(4, mockItem2.getQuantity()); // Original quantity was 5, should be decremented by 1

                verify(connectionDao, times(1)).getConnectionById(connectionId);
                verify(customerDao, times(2)).getCustomerById(mockCustomer.getId());
                verify(concreteVendingMachineDao, times(1)).findById(mockConcreteVendingMachine.getId());
                verify(itemDao, times(1)).getItemById(30L);
                verify(itemDao, times(1)).getItemById(31L);
                verify(itemDao, times(1)).updateItem(mockItem1);
                verify(itemDao, times(1)).updateItem(mockItem2);
                verify(customerDao, times(1)).updateCustomer(mockCustomer);
                verify(transactionService, times(1)).createTransaction(any(Transaction.class));
        }

        @Test
        @DisplayName("Test buyItem method when connection not found")
        void testBuyItemConnectionNotFound() {
                // Arrange
                long connectionId = 999L;
                List<Long> itemIds = Arrays.asList(30L);

                when(connectionDao.getConnectionById(connectionId)).thenReturn(null);

                // Act & Assert
                IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                        customerService.buyItem(connectionId, itemIds);
                });

                assertEquals("Connection not found", thrown.getMessage());
                verify(connectionDao, times(1)).getConnectionById(connectionId);
                verify(customerDao, never()).getCustomerById(anyLong());
                verify(concreteVendingMachineDao, never()).findById(anyString());
                verify(itemDao, never()).getItemById(anyLong());
                verify(transactionService, never()).createTransaction(any(Transaction.class));
        }

        @Test
        @DisplayName("Test buyItem method when customer not found")
        void testBuyItemCustomerNotFound() {
                // Arrange
                long connectionId = 1L;
                List<Long> itemIds = Arrays.asList(30L);
                Connection mockConnection = mock(Connection.class);

                when(connectionDao.getConnectionById(connectionId)).thenReturn(mockConnection);
                when(mockConnection.getUserId()).thenReturn(999L);
                when(customerDao.getCustomerByUserId(999L)).thenReturn(null);

                // Act & Assert
                IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                        customerService.buyItem(connectionId, itemIds);
                });

                assertEquals("Customer not found", thrown.getMessage());
                verify(connectionDao, times(1)).getConnectionById(connectionId);
                verify(customerDao, times(1)).getCustomerByUserId(999L);
                verify(concreteVendingMachineDao, never()).findById(anyString());
                verify(itemDao, never()).getItemById(anyLong());
                verify(transactionService, never()).createTransaction(any(Transaction.class));
        }

        @Test
        @DisplayName("Test buyItem method when item not found")
        void testBuyItemItemNotFound() {
                // Arrange
                long connectionId = 1L;
                List<Long> itemIds = Arrays.asList(999L);
                Connection mockConnection = new Connection(mockCustomer.getUser(), mockConcreteVendingMachine,
                                Instant.now());

                when(connectionDao.getConnectionById(connectionId)).thenReturn(mockConnection);
                when(customerDao.getCustomerById(mockCustomer.getId())).thenReturn(mockCustomer);
                when(customerDao.getCustomerByUserId(mockCustomer.getUser().getId()))
                                .thenReturn(mockCustomer);
                when(concreteVendingMachineDao.findById(mockConcreteVendingMachine.getId()))
                                .thenReturn(mockConcreteVendingMachine);
                when(itemDao.getItemById(999L)).thenReturn(null);

                // Act & Assert
                IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                        customerService.buyItem(connectionId, itemIds);
                });

                assertEquals("Item not found: 999", thrown.getMessage());
                verify(connectionDao, times(1)).getConnectionById(connectionId);
                verify(concreteVendingMachineDao, times(1)).findById(mockConcreteVendingMachine.getId());
                verify(itemDao, times(1)).getItemById(999L);
                verify(itemDao, never()).updateItem(any(Item.class));
                verify(transactionService, never()).createTransaction(any(Transaction.class));
        }

        @Test
        @DisplayName("Test buyItem method when item is out of stock")
        void testBuyItemOutOfStock() {
                // Arrange
                long connectionId = 1L;
                List<Long> itemIds = Arrays.asList(30L);
                Connection mockConnection = new Connection(mockCustomer.getUser(), mockConcreteVendingMachine,
                                Instant.now());
                Item outOfStockItem = new Item(30L, "Soda", "Description", 1, 0, 1.50, 3, ItemType.Bottle); // Quantity
                                                                                                            // = 0

                when(connectionDao.getConnectionById(connectionId)).thenReturn(mockConnection);
                when(customerDao.getCustomerById(mockCustomer.getId())).thenReturn(mockCustomer);
                when(customerDao.getCustomerByUserId(mockCustomer.getUser().getId()))
                                .thenReturn(mockCustomer);
                when(concreteVendingMachineDao.findById(mockConcreteVendingMachine.getId()))
                                .thenReturn(mockConcreteVendingMachine);
                when(itemDao.getItemById(30L)).thenReturn(outOfStockItem);

                // Act & Assert
                IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                        customerService.buyItem(connectionId, itemIds);
                });

                assertEquals("Item not available", thrown.getMessage());
                verify(connectionDao, times(1)).getConnectionById(connectionId);
                verify(concreteVendingMachineDao, times(1)).findById(mockConcreteVendingMachine.getId());
                verify(itemDao, times(1)).getItemById(30L);
                verify(itemDao, never()).updateItem(any(Item.class));
                verify(transactionService, never()).createTransaction(any(Transaction.class));
        }

        @Test
        @DisplayName("Test buyItem method when customer has insufficient balance")
        void testBuyItemInsufficientBalance() {
                // Arrange
                long connectionId = 1L;
                List<Long> itemIds = Arrays.asList(30L);
                Connection mockConnection = new Connection(mockCustomer.getUser(), mockConcreteVendingMachine,
                                Instant.now());
                Customer poorCustomer = new Customer(new User(2L, "poor@example.com", "Poor", "Customer", "password"),
                                1.0); // Only
                                      // $1.00
                                      // balance
                Item expensiveItem = new Item(30L, "Expensive Item", "Very expensive", 1, 10, 150.0, 3,
                                ItemType.Bottle); // $150.00
                                                  // price

                when(connectionDao.getConnectionById(connectionId)).thenReturn(mockConnection);
                when(customerDao.getCustomerByUserId(mockCustomer.getUser().getId())).thenReturn(mockCustomer);
                when(customerDao.getCustomerById(mockCustomer.getId())).thenReturn(poorCustomer);
                when(concreteVendingMachineDao.findById(mockConcreteVendingMachine.getId()))
                                .thenReturn(mockConcreteVendingMachine);
                when(itemDao.getItemById(30L)).thenReturn(expensiveItem);

                // Act & Assert
                IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                        customerService.buyItem(connectionId, itemIds);
                });

                assertEquals("Insufficient balance", thrown.getMessage());
                verify(connectionDao, times(1)).getConnectionById(connectionId);
                verify(customerDao, times(1)).getCustomerById(mockCustomer.getId());
                verify(concreteVendingMachineDao, times(1)).findById(mockConcreteVendingMachine.getId());
                verify(itemDao, times(1)).getItemById(30L);
                verify(itemDao, never()).updateItem(any(Item.class)); // Item should not be updated if balance check
                                                                      // fails
                verify(customerDao, never()).updateCustomer(any(Customer.class)); // Customer balance should not be
                                                                                  // updated
                verify(transactionService, never()).createTransaction(any(Transaction.class));
        }

        @Test
        @DisplayName("Test buyItem method with empty item list")
        void testBuyItemEmptyItemList() {
                // Arrange
                long connectionId = 1L;
                List<Long> itemIds = Arrays.asList(); // Empty list
                Connection mockConnection = new Connection(mockCustomer.getUser(), mockConcreteVendingMachine,
                                Instant.now());
                Transaction expectedTransaction = new Transaction(mockCustomer, PaymentMethod.Wallet, 100.0, 100.0,
                                Arrays.asList());

                when(connectionDao.getConnectionById(connectionId)).thenReturn(mockConnection);
                when(customerDao.getCustomerByUserId(mockCustomer.getUser().getId())).thenReturn(mockCustomer);
                when(concreteVendingMachineDao.findById(mockConcreteVendingMachine.getId()))
                                .thenReturn(mockConcreteVendingMachine);
                when(customerDao.getCustomerById(mockCustomer.getId())).thenReturn(mockCustomer);
                when(transactionService.createTransaction(any(Transaction.class))).thenReturn(expectedTransaction);

                // Act
                Transaction result = customerService.buyItem(connectionId, itemIds);

                // Assert
                assertNotNull(result);
                assertEquals(expectedTransaction, result);

                verify(connectionDao, times(1)).getConnectionById(connectionId);
                verify(customerDao, times(2)).getCustomerById(mockCustomer.getId());
                verify(concreteVendingMachineDao, times(1)).findById(mockConcreteVendingMachine.getId());
                verify(itemDao, never()).getItemById(anyLong());
                verify(itemDao, never()).updateItem(any(Item.class));
                verify(customerDao, times(1)).updateCustomer(mockCustomer); // Called in updateBalance with amount 0.0
                verify(transactionService, times(1)).createTransaction(any(Transaction.class));
        }

        @Test
        @DisplayName("createCustomerFromUser: crea Customer da User valido")
        void testCreateCustomerFromUser() {
                User user = new User(1L, "mail@javabrew.it", "Anna", "Brew", "pw");
                Customer expected = new Customer(user, 0.0);

                when(customerDao.createCustomer(any(Customer.class))).thenReturn(expected);

                Customer created = customerService.createCustomerFromUser(user);

                assertNotNull(created);
                assertEquals(user, created.getUser());
                assertEquals(0.0, created.getBalance());
                assertEquals(User.Role.Customer, created.getUser().getRole());
                verify(customerDao, times(1)).createCustomer(any(Customer.class));
        }

        @Test
        @DisplayName("createCustomerFromUser: se User null -> IllegalArgumentException")
        void testCreateCustomerFromUserNull() {
                assertThrows(IllegalArgumentException.class, () -> {
                        customerService.createCustomerFromUser(null);
                });
                verify(customerDao, never()).createCustomer(any());
        }

}