package com.smartvend.app.dao;

import java.util.List;

import com.smartvend.app.model.transaction.Transaction;

public interface TransactionDao {
    Transaction createTransaction(Transaction transaction);
    List<Transaction> getTransactionsByCustomer(String customerId);
}
