package com.smartVend.app.model;

import java.util.Date;
import java.util.List;

public class Transaction {
    private String id;
    private Date date;
    private double initialBalance;
    private double updatedBalance;
    private Customer customer;
    private PaymentMethod paymentMethod;
    private List<TransactionItem> transactionItems;

    public Transaction() {}

    public Transaction(String id, Date date, double initialBalance, double updatedBalance,
                       Customer customer, PaymentMethod paymentMethod, List<TransactionItem> transactionItems) {
        this.id = id;
        this.date = date;
        this.initialBalance = initialBalance;
        this.updatedBalance = updatedBalance;
        this.customer = customer;
        this.paymentMethod = paymentMethod;
        this.transactionItems = transactionItems;
    }

    // Getter e setter qui...
}
