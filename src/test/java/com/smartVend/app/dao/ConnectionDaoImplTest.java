package com.smartvend.app.dao;

import com.smartvend.app.dao.impl.ConnectionDaoImpl;
import com.smartvend.app.model.connection.Connection;
import com.smartvend.app.model.user.User;
import com.smartvend.app.model.vendingmachine.VendingMachine;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ConnectionDaoImplTest {

    private EntityManager entityManager;
    private ConnectionDaoImpl connectionDao;

    @BeforeEach
    void setUp() {
        entityManager = mock(EntityManager.class);
        connectionDao = new ConnectionDaoImpl();
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
        User user = mock(User.class);
        VendingMachine machine = mock(VendingMachine.class);

        when(entityManager.find(User.class, "user1")).thenReturn(user);
        when(entityManager.find(VendingMachine.class, "machine1")).thenReturn(machine);

        Connection result = connectionDao.createConnection("user1", "machine1");
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
