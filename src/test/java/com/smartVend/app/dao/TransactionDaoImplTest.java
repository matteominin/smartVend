package com.smartvend.app.dao;

import com.smartvend.app.dao.impl.TransactionDaoImpl;
import com.smartvend.app.model.transaction.Transaction;
import com.smartvend.app.model.transaction.PaymentMethod;
import com.smartvend.app.model.user.Customer;
import com.smartvend.app.model.user.User;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransactionDaoImplTest {

    @Nested
    class Integration {

        private static EntityManagerFactory emf;
        private TransactionDaoImpl dao;

        @BeforeAll
        static void startPU() {
            emf = Persistence.createEntityManagerFactory("test-pu");
        }

        @AfterAll
        static void stopPU() {
            if (emf != null) emf.close();
        }

        @BeforeEach
        void setup() {
            dao = new TransactionDaoImpl(emf);
        }

        @Test
        void integration_CRUD_flow() {
            // Crea utente e customer
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();

            User user = new User("customer@email.com", "Mario", "Rossi", "pwd");
            em.persist(user);
            em.flush();
            Customer customer = new Customer(user, 50.0);
            em.persist(customer);
            em.flush();

            em.getTransaction().commit();
            em.close();

            // Crea una transazione con initial/updated balance e payment method
            Transaction t = new Transaction();
            t.setCustomer(customer);
            t.setInitialBalance(50.0);
            t.setUpdatedBalance(42.0);
            t.setDate(Instant.now());
            t.setPaymentMethod(PaymentMethod.Card); // Cambia se hai enum diversi
            t.setTransactionItems(Collections.emptyList()); // O una lista vera se necessario

            dao.createTransaction(t);

            assertNotNull(t.getId());

            // Recupera la lista
            List<Transaction> list = dao.getTransactionsByCustomer(customer.getId());
            assertFalse(list.isEmpty());
            assertEquals(customer.getId(), list.get(0).getCustomer().getId());
            assertEquals(50.0, list.get(0).getInitialBalance());
            assertEquals(42.0, list.get(0).getUpdatedBalance());
            assertEquals(PaymentMethod.Card, list.get(0).getPaymentMethod());
        }
    }
}
