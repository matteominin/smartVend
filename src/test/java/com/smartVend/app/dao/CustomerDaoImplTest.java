package com.smartvend.app.dao;

import com.smartvend.app.dao.impl.CustomerDaoImpl;
import com.smartvend.app.model.user.Customer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CustomerDaoImplTest {

    private EntityManager entityManager;
    private CustomerDaoImpl customerDao;

    @BeforeEach
    void setUp() {
        entityManager = mock(EntityManager.class);
        customerDao = new CustomerDaoImpl();
        // inject entityManager
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
        Customer customer = new Customer();
        when(entityManager.find(Customer.class, "123")).thenReturn(customer);

        Customer result = customerDao.getCustomerById("123");
        assertNotNull(result);
        assertEquals(customer, result);
    }

    @Test
    void findAll_returnsEmptyList() {
        TypedQuery<Customer> query = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Customer.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        List<Customer> result = customerDao.findAll();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
