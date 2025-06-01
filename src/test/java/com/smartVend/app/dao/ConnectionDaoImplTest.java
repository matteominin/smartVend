package com.smartvend.app.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.smartvend.app.dao.impl.ConnectionDaoImpl;
import com.smartvend.app.model.connection.Connection;
import com.smartvend.app.model.user.Customer;
import com.smartvend.app.model.user.User;
import com.smartvend.app.model.vendingmachine.ConcreteVendingMachine;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

class ConnectionDaoImplTest {
    private Customer customer;
    private ConcreteVendingMachine machine;

    @Mock
    private EntityManagerFactory entityManagerFactory;
    @Mock
    private EntityManager entityManager;
    @Mock
    private EntityTransaction transaction;

    @InjectMocks
    private ConnectionDaoImpl connectionDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
        when(entityManager.getTransaction()).thenReturn(transaction);

        customer = new Customer(2L, new User(1L, null, null, null, null), 10);
        machine = new ConcreteVendingMachine(
                "machine1",
                null,
                "Location",
                5,
                null,
                null);
    }

    @Test
    void createConnection_persistsConnection() {
        when(entityManager.find(Customer.class, customer.getId())).thenReturn(customer);
        when(entityManager.find(ConcreteVendingMachine.class, machine.getId())).thenReturn(machine);
        Connection result = connectionDao.createConnection(customer.getId(), machine.getId());
        assertNotNull(result);
        verify(entityManager).persist(any(Connection.class));
    }

    @Test
    void deleteConnection_removesConnection() {
        Connection connection = mock(Connection.class);
        when(entityManager.find(Connection.class, 10L)).thenReturn(connection);

        connectionDao.deleteConnection(10L);
        verify(entityManager).remove(connection);
    }

    @Test
    void deleteConnection_doesNothingIfNotFound() {
        when(entityManager.find(Connection.class, 20L)).thenReturn(null);
        connectionDao.deleteConnection(20L);
        verify(entityManager, never()).remove(any());
    }

    @Test
    void getConnectionById_returnsConnection() {
        Connection connection = mock(Connection.class);
        when(entityManager.find(Connection.class, 1L)).thenReturn(connection);

        Connection result = connectionDao.getConnectionById(1L);

        assertNotNull(result);
        assertEquals(connection, result);
    }

    @Test
    void getConnectionById_returnsNullIfNotFound() {
        when(entityManager.find(Connection.class, 2L)).thenReturn(null);

        Connection result = connectionDao.getConnectionById(2L);

        assertNull(result);
    }

    @Test
    void createConnection_throwsException_ifCustomerNotFound() {
        when(entityManager.find(Customer.class, customer.getId())).thenReturn(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            connectionDao.createConnection(customer.getId(), machine.getId());
        });

        assertTrue(exception.getMessage().contains("User not found"));
    }

    @Test
    void createConnection_throwsException_ifMachineNotFound() {
        when(entityManager.find(Customer.class, customer.getId())).thenReturn(customer);
        when(entityManager.find(ConcreteVendingMachine.class, machine.getId())).thenReturn(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            connectionDao.createConnection(customer.getId(), machine.getId());
        });

        assertTrue(exception.getMessage().contains("Machine not found"));
    }

}