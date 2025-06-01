package com.smartvend.app.dao.impl;

import java.util.List;

import com.smartvend.app.dao.TransactionDao;
import com.smartvend.app.model.transaction.Transaction;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory; // Import EntityManagerFactory

public class TransactionDaoImpl implements TransactionDao {

    private final EntityManagerFactory emf; // Use EntityManagerFactory

    public TransactionDaoImpl(EntityManagerFactory entityManagerFactory) {
        this.emf = entityManagerFactory;
    }

    @Override
    public Transaction createTransaction(Transaction transaction) {
        EntityManager em = emf.createEntityManager(); // Create EntityManager
        try {
            em.getTransaction().begin(); // Begin transaction
            em.persist(transaction);
            em.getTransaction().commit(); // Commit transaction
            return transaction;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback(); // Rollback on error
            }
            throw e;
        } finally {
            em.close(); // Close EntityManager
        }
    }

    @Override
    public List<Transaction> getTransactionsByCustomer(Long customerId) {
        EntityManager em = emf.createEntityManager(); // Create EntityManager
        try {
            return em.createQuery(
                    "SELECT t FROM Transaction t WHERE t.customer.id = :customerId", Transaction.class)
                    .setParameter("customerId", customerId)
                    .getResultList();
        } finally {
            em.close(); // Close EntityManager
        }
    }
}
