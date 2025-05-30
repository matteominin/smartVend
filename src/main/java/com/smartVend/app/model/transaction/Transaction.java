package com.smartvend.app.model.transaction;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import com.smartvend.app.model.user.Customer;

@Entity
public class Transaction implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    public Customer customer;

    @Column(nullable = false)
    public Instant date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public PaymentMethod paymentMethod;

    @Column(nullable = false)
    public double initialBalance;

    @Column(nullable = false)
    public double updatedBalance;

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL)
    public List<TransactionItem> transactionItems;

    public Transaction() {
    }

    public Transaction(Customer customer, Instant date, PaymentMethod paymentMethod, double initialBalance,
            double updatedBalance, List<TransactionItem> transactionItems) {
        this.customer = customer;
        this.date = date;
        this.paymentMethod = paymentMethod;
        this.initialBalance = initialBalance;
        this.updatedBalance = updatedBalance;
        this.transactionItems = transactionItems;
    }
}
