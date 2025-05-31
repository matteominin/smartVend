package com.smartvend.app.model.transaction;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

import com.smartvend.app.model.user.Customer;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Transaction implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    @Column(nullable = false)
    private Instant date;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;
    @Column(nullable = false)
    private double initialBalance;
    @Column(nullable = false)
    private double updatedBalance;
    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL)
    private List<TransactionItem> transactionItems;
    public Transaction() {
        // Default constructor for JPA
        this.date = Instant.now();
    }

    public Transaction(Customer customer, PaymentMethod paymentMethod, double initialBalance,
            double updatedBalance, List<TransactionItem> transactionItems) {
        this.customer = customer;
        this.paymentMethod = paymentMethod;
        this.initialBalance = initialBalance;
        this.updatedBalance = updatedBalance;
        this.transactionItems = transactionItems;
        this.date = Instant.now();
    }

    public Transaction(long id, Customer customer, PaymentMethod paymentMethod, double initialBalance,
            double updatedBalance, List<TransactionItem> transactionItems) {
        this.id = id;
        this.customer = customer;
        this.paymentMethod = paymentMethod;
        this.initialBalance = initialBalance;
        this.updatedBalance = updatedBalance;
        this.transactionItems = transactionItems;
        this.date = Instant.now();
    }

    public Long getCustomerId() {
        return customer != null ? customer.getId() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public double getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(double initialBalance) {
        this.initialBalance = initialBalance;
    }

    public double getUpdatedBalance() {
        return updatedBalance;
    }

    public void setUpdatedBalance(double updatedBalance) {
        this.updatedBalance = updatedBalance;
    }

    public List<TransactionItem> getTransactionItems() {
        return transactionItems;
    }

    public void setTransactionItems(List<TransactionItem> transactionItems) {
        this.transactionItems = transactionItems;
    }
    
}
