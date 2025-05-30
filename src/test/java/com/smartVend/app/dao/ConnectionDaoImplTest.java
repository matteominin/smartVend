package com.smartvend.app.dao;

import com.smartvend.app.dao.impl.ConnectionDaoImpl;
import com.smartvend.app.model.connection.Connection;
import com.smartvend.app.model.user.Customer;
import com.smartvend.app.model.user.User;
import com.smartvend.app.model.vendingmachine.ConcreteVendingMachine;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ConnectionDaoImplTest {

    private EntityManager entityManager;
    private ConnectionDaoImpl connectionDao;
    private Customer customer;
    private ConcreteVendingMachine machine;

    @BeforeEach
    void setUp() {
        entityManager = mock(EntityManager.class);
        connectionDao = new ConnectionDaoImpl();
        customer = new Customer(2L, new User(1L, null, null, null, null), 10);
        machine = new ConcreteVendingMachine(
                "machine1",
                null,
                "Location",
                5,
                null,
                null);

        // Inject EntityManager via reflection
        try {
            var field = ConnectionDaoImpl.class.getDeclaredField("entityManager");
            field.setAccessible(true);
            field.set(connectionDao, entityManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
}