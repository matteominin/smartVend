package com.smartvend.app.dao;

import com.smartvend.app.dao.impl.TransactionDaoImpl;
import com.smartvend.app.model.transaction.Transaction;
import com.smartvend.app.model.transaction.PaymentMethod;
import com.smartvend.app.model.user.Customer;
import com.smartvend.app.model.user.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class TransactionDaoImplTest {

    /*
     * ──────────────────────────── UNIT TESTS (Mockito) ───────────────────────────
     */
    @Nested
    class Unit {

        @Mock
        EntityManagerFactory emf;
        @Mock
        EntityManager em;
        @Mock
        EntityTransaction tx;
        @InjectMocks
        TransactionDaoImpl dao;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
            when(emf.createEntityManager()).thenReturn(em);
            when(em.getTransaction()).thenReturn(tx);
        }

        @Test
        void createTransaction_persistsTransaction() {
            Transaction transaction = mock(Transaction.class);
            when(tx.isActive()).thenReturn(false);

            Transaction result = dao.createTransaction(transaction);

            verify(em).persist(transaction);
            verify(tx).begin();
            verify(tx).commit();
            assertEquals(transaction, result);
        }

        @Test
        void createTransaction_rollsBackOnException() {
            Transaction transaction = mock(Transaction.class);
            doThrow(new RuntimeException("fail")).when(em).persist(transaction);
            when(tx.isActive()).thenReturn(true);

            assertThrows(RuntimeException.class, () -> dao.createTransaction(transaction));
            verify(tx).rollback();
        }

        @Test
        void getTransactionsByCustomer_returnsList() {
            long customerId = 123;
            @SuppressWarnings("unchecked")
            TypedQuery<Transaction> query = (TypedQuery<Transaction>) mock(TypedQuery.class);
            when(em.createQuery(anyString(), eq(Transaction.class))).thenReturn(query);
            when(query.setParameter(eq("customerId"), eq(customerId))).thenReturn(query);
            when(query.getResultList()).thenReturn(Collections.emptyList());

            List<Transaction> result = dao.getTransactionsByCustomer(customerId);
            assertTrue(result.isEmpty());
            verify(em).createQuery(contains("customerId"), eq(Transaction.class));
            verify(query).setParameter("customerId", customerId);
            verify(query).getResultList();
        }
    }

    /*
     * ──────────────────────────── INTEGRATION TESTS (H2) ─────────────────────────
     */
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
            if (emf != null)
                emf.close();
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
