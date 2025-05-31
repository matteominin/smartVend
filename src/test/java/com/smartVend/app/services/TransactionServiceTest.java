package com.smartvend.app.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.smartvend.app.dao.impl.TransactionDaoImpl;
import com.smartvend.app.model.transaction.PaymentMethod;
import com.smartvend.app.model.transaction.Transaction;
import com.smartvend.app.model.user.Customer;
import com.smartvend.app.model.user.User;

public class TransactionServiceTest {
    @Mock
    private TransactionDaoImpl transactionDao;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

    }

    @Test
    @DisplayName("Test getCustomerTransactions with valid customer ID")
    public void testGetTransactionByCustomerValid() {
        Long customerId = 1L;
        Transaction transaction1 = new Transaction();
        Transaction transaction2 = new Transaction();

        when(transactionDao.getTransactionsByCustomer(customerId))
                .thenReturn(List.of(transaction1, transaction2));
        List<Transaction> transactions = transactionService.getCustomerTransactions(customerId);
        assertNotNull(transactions);
        assertEquals(2, transactions.size());
        assertEquals(transaction1, transactions.get(0));
        assertEquals(transaction2, transactions.get(1));
    }

    @Test
    @DisplayName("Test getCustomerTransactions with invalid customer ID")
    public void testGetTransactionByCustomerInvalid() {
        Long customerId = -1L;

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.getCustomerTransactions(customerId);
        });

        String expectedMessage = "Invalid customer ID";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    @DisplayName("Test createTransaction with valid transaction")
    public void testCreateTransactionValid() {
        Transaction transaction = new Transaction(
                1L,
                new Customer(1L, new User(), 80),
                PaymentMethod.Card,
                100,
                80,
                List.of());

        when(transactionDao.createTransaction(transaction))
                .thenReturn(transaction);
        Transaction createdTransaction = transactionService.createTransaction(transaction);
        assertNotNull(createdTransaction);
        assertEquals(transaction, createdTransaction);
    }

    @Test
    @DisplayName("Test createTransaction with invalid transaction")
    public void testCreateTransactionInvalid() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.createTransaction(null);
        });

        String expectedMessage = "Invalid transaction data";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    @DisplayName("Test createTransaction with invalid customer ID")
    public void testCreateTransactionInvalidCustomer() {
        Transaction transaction = new Transaction(
                1L,
                new Customer(0L, new User(), 80),
                PaymentMethod.Card,
                100,
                80,
                List.of());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.createTransaction(transaction);
        });

        String expectedMessage = "Invalid transaction data";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
}