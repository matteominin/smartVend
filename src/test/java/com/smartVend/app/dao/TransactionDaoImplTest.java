package com.smartvend.app.dao;

import com.smartvend.app.dao.impl.TransactionDaoImpl;
import com.smartvend.app.model.transaction.Transaction;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class TransactionDaoImplTest {
    @Mock
    private EntityManagerFactory entityManagerFactory;
    @Mock
    private EntityManager entityManager;
    @Mock
    private EntityTransaction transaction;

    @InjectMocks
    private TransactionDaoImpl transactionDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
        when(entityManager.getTransaction()).thenReturn(transaction);
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
