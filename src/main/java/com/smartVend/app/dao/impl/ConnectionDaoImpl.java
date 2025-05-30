package com.smartvend.app.dao.impl;

import java.time.Instant;

import com.smartvend.app.dao.ConnectionDao;
import com.smartvend.app.model.connection.Connection;
import com.smartvend.app.model.user.Customer;
import com.smartvend.app.model.vendingmachine.ConcreteVendingMachine;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

public class ConnectionDaoImpl implements ConnectionDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Connection createConnection(Long userId, String machineId) {
        Customer customer = entityManager.find(Customer.class, userId);
        ConcreteVendingMachine machine = entityManager.find(ConcreteVendingMachine.class, machineId);
        if (customer == null) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        } else if (machine == null) {
            throw new IllegalArgumentException("Machine not found with ID: " + machineId);
        }
        Connection connection = new Connection(userId, machineId, Instant.now());
        entityManager.persist(connection);
        return connection;
    }

    @Override
    public Connection getConnectionById(Long connectionId) {
        return entityManager.find(Connection.class, connectionId);
    }

    @Override
    @Transactional
    public void deleteConnection(Long connectionId) {
        Connection connection = entityManager.find(Connection.class, connectionId);
        if (connection != null) {
            entityManager.remove(connection);
        }
    }
}
