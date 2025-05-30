package com.smartvend.app.dao.impl;

import java.time.Instant;

import com.smartvend.app.dao.ConnectionDao;
import com.smartvend.app.model.connection.Connection;
import com.smartvend.app.model.user.User;
import com.smartvend.app.model.vendingmachine.VendingMachine;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

public class ConnectionDaoImpl implements ConnectionDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Connection createConnection(String userId, String machineId) {
        User user = entityManager.find(User.class, userId);
        VendingMachine machine = entityManager.find(VendingMachine.class, machineId);
        Connection connection = new Connection(user, machine, Instant.now());
        entityManager.persist(connection);
        return connection;
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
