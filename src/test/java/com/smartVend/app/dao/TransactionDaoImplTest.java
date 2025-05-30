package com.smartvend.app.dao;

import com.smartvend.app.dao.impl.TransactionDaoImpl;
import com.smartvend.app.model.transaction.Transaction;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class TransactionDaoImplTest {

    private EntityManager entityManager;
    private TransactionDaoImpl transactionDao;

    @BeforeEach
    void setUp() {
        entityManager = mock(EntityManager.class);
        transactionDao = new TransactionDaoImpl();
        // Reflection to inject the mock
        try {
            var field = TransactionDaoImpl.class.getDeclaredField("entityManager");
            field.setAccessible(true);
            field.set(transactionDao, entityManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createTransaction_persistsTransaction() {
        Transaction transaction = mock(Transaction.class);
        Transaction result = transactionDao.createTransaction(transaction);
        verify(entityManager).persist(transaction);
        assertEquals(transaction, result);
    }

    @Test
    void getTransactionsByCustomer_returnsList() {
        long customerId = 123;
        @SuppressWarnings("unchecked")
        TypedQuery<Transaction> query = (TypedQuery<Transaction>) mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Transaction.class))).thenReturn(query);
        when(query.setParameter(eq("customerId"), eq(customerId))).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        List<Transaction> result = transactionDao.getTransactionsByCustomer(customerId);
        assertTrue(result.isEmpty());
    }
}
