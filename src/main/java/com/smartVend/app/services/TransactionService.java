package com.smartvend.app.services;

import java.util.List;

import com.smartvend.app.dao.impl.TransactionDaoImpl;
import com.smartvend.app.model.transaction.Transaction;

public class TransactionService {
    private TransactionDaoImpl transactionDao;

    public TransactionService(TransactionDaoImpl transactionDao) {
        this.transactionDao = transactionDao;
    }

    public List<Transaction> getCustomerTransactions(long customerId) {
        if (transactionDao == null) {
            throw new IllegalStateException("TransactionDao is not initialized");
        }
        if (customerId <= 0) {
            throw new IllegalArgumentException("Invalid customer ID");
        }

        return transactionDao.getTransactionsByCustomer(customerId);
    }

    public Transaction createTransaction(Transaction transaction) {
        if (transactionDao == null) {
            throw new IllegalStateException("TransactionDao is not initialized");
        }
        if (transaction == null || transaction.getCustomerId() <= 0) {
            throw new IllegalArgumentException("Invalid transaction data");
        }

        return transactionDao.createTransaction(transaction);
    }
}
