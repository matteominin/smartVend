package com.smartvend.app.dao.impl;

import java.util.List;

import com.smartvend.app.dao.TransactionDao;
import com.smartvend.app.model.transaction.Transaction;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

public class TransactionDaoImpl implements TransactionDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Transaction createTransaction(Transaction transaction) {
        entityManager.persist(transaction);
        return transaction;
    }

    @Override
    public List<Transaction> getTransactionsByCustomer(String customerId) {
        return entityManager.createQuery(
                "SELECT t FROM Transaction t WHERE t.customer.id = :customerId", Transaction.class)
            .setParameter("customerId", customerId)
            .getResultList();
    }
}
