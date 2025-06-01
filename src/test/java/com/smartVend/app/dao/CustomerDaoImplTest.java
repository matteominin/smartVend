package com.smartvend.app.dao;

import com.smartvend.app.dao.impl.CustomerDaoImpl;
import com.smartvend.app.model.user.Customer;
import com.smartvend.app.model.user.User;

import jakarta.persistence.*;

import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerDaoImplTest {

    // ------------------------ UNIT TESTS ------------------------
    @Nested
    class Unit {

        @Mock EntityManagerFactory entityManagerFactory;
        @Mock EntityManager entityManager;
        @Mock EntityTransaction transaction;

        @InjectMocks CustomerDaoImpl customerDao;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
            when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
            when(entityManager.getTransaction()).thenReturn(transaction);
        }

        @Test
        void getCustomerById_returnsCustomer() {
            Customer customer = new Customer(123L, new User(1L, null, null, null, null), 10);
            when(entityManager.find(Customer.class, 123L)).thenReturn(customer);
            Customer result = customerDao.getCustomerById(123L);
            assertNotNull(result);
            assertEquals(customer, result);
        }

        @Test
        void getCustomerById_returnsNullIfNotFound() {
            when(entityManager.find(Customer.class, 456L)).thenReturn(null);
            Customer result = customerDao.getCustomerById(456L);
            assertNull(result);
        }

        @Test
        void findAll_returnsEmptyList() {
            @SuppressWarnings("unchecked")
            TypedQuery<Customer> query = (TypedQuery<Customer>) mock(TypedQuery.class);
            when(entityManager.createQuery(anyString(), eq(Customer.class))).thenReturn(query);
            when(query.getResultList()).thenReturn(Collections.emptyList());
            List<Customer> result = customerDao.findAll();
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        void findAll_returnsListWithCustomers() {
            @SuppressWarnings("unchecked")
            TypedQuery<Customer> query = (TypedQuery<Customer>) mock(TypedQuery.class);
            Customer customer = new Customer(123L, new User(1L, null, null, null, null), 10);
            when(entityManager.createQuery(anyString(), eq(Customer.class))).thenReturn(query);
            when(query.getResultList()).thenReturn(List.of(customer));
            List<Customer> result = customerDao.findAll();
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(customer, result.get(0));
        }

        @Test
        void createCustomer_persistsCustomer() {
            Customer customer = new Customer(123L, new User(1L, null, null, null, null), 10);
            Customer result = customerDao.createCustomer(customer);
            verify(entityManager).persist(customer);
            assertEquals(customer, result);
        }

        @Test
        void updateCustomer_mergesCustomer() {
            Customer customer = new Customer(123L, new User(1L, null, null, null, null), 10);
            Customer merged = new Customer(123L, new User(1L, null, null, null, null), 15);
            when(entityManager.merge(customer)).thenReturn(merged);
            Customer result = customerDao.updateCustomer(customer);
            verify(entityManager).merge(customer);
            assertEquals(merged, result);
        }

        @Test
        void deleteCustomer_removesCustomerIfExists() {
            Customer customer = new Customer(123L, new User(1L, null, null, null, null), 10);
            when(entityManager.find(Customer.class, 123L)).thenReturn(customer);
            customerDao.deleteCustomer(123L);
            verify(entityManager).remove(customer);
        }

        @Test
        void deleteCustomer_doesNothingIfCustomerNotFound() {
            when(entityManager.find(Customer.class, 456L)).thenReturn(null);
            customerDao.deleteCustomer(456L);
            verify(entityManager, never()).remove(any());
        }
    }

    // ------------------------ INTEGRATION TESTS ------------------------
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class Integration {
        private EntityManagerFactory emf;
        private CustomerDaoImpl customerDao;

        @BeforeAll
        void setupAll() {
            emf = Persistence.createEntityManagerFactory("test-pu"); // usa il tuo persistence-unit di test
            customerDao = new CustomerDaoImpl(emf);
        }

        @AfterAll
        void tearDownAll() {
            if (emf != null) emf.close();
        }

        @Test
        void integration_CRUD_flow() {
            // Prima crea e salva l'utente collegato!
            User user = new User("customer@email.com", "Alice", "Bianchi", "pwd");
            var em = emf.createEntityManager();
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
            em.close();

            // Collega User a Customer
            Customer customer = new Customer(user, 0.0);

            // CREATE
            customerDao.createCustomer(customer);
            assertNotNull(customer.getId());

            // READ
            Customer loaded = customerDao.getCustomerById(customer.getId());
            assertNotNull(loaded);
            assertEquals("customer@email.com", loaded.getUser().getEmail());

            // UPDATE (modifica balance)
            loaded.setBalance(42.0);
            customerDao.updateCustomer(loaded);

            Customer afterUpdate = customerDao.getCustomerById(loaded.getId());
            assertEquals(42.0, afterUpdate.getBalance());

            // FIND ALL
            List<Customer> all = customerDao.findAll();
            assertFalse(all.isEmpty());

            // DELETE
            customerDao.deleteCustomer(customer.getId());
            Customer afterDelete = customerDao.getCustomerById(customer.getId());
            assertNull(afterDelete);
        }
    }
}