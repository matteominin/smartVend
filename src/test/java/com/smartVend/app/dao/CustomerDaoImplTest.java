package com.smartvend.app.dao;

import com.smartvend.app.dao.impl.CustomerDaoImpl;
import com.smartvend.app.model.user.Customer;
import com.smartvend.app.model.user.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

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
    void findAll_returnsEmptyList() {
        @SuppressWarnings("unchecked")
        TypedQuery<Customer> query = (TypedQuery<Customer>) mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Customer.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        List<Customer> result = customerDao.findAll();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}