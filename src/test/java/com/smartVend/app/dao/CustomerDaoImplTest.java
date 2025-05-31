package com.smartvend.app.dao;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.smartvend.app.dao.impl.CustomerDaoImpl;
import com.smartvend.app.model.user.Customer;
import com.smartvend.app.model.user.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

class CustomerDaoImplTest {

    private EntityManager entityManager;
    private CustomerDaoImpl customerDao;

    @BeforeEach
    void setUp() {
        entityManager = mock(EntityManager.class);
        customerDao = new CustomerDaoImpl();

        try {
            var field = CustomerDaoImpl.class.getDeclaredField("entityManager");
            field.setAccessible(true);
            field.set(customerDao, entityManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
