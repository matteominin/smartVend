package com.smartvend.app.dao.impl;

import java.time.Instant;

import com.smartvend.app.dao.ConnectionDao;
import com.smartvend.app.model.connection.Connection;
import com.smartvend.app.model.user.Customer;
import com.smartvend.app.model.vendingmachine.ConcreteVendingMachine;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class ConnectionDaoImpl implements ConnectionDao {

    private final EntityManagerFactory emf;

    public ConnectionDaoImpl(EntityManagerFactory entityManagerFactory) {
        this.emf = entityManagerFactory;
    }

    @Override
    public Connection createConnection(Long userId, String machineId) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Customer customer = em.find(Customer.class, userId);
            ConcreteVendingMachine machine = em.find(ConcreteVendingMachine.class, machineId);
            if (customer == null) {
                throw new IllegalArgumentException("User not found with ID: " + userId);
            } else if (machine == null) {
                throw new IllegalArgumentException("Machine not found with ID: " + machineId);
            }
            Connection connection = new Connection(userId, machineId, Instant.now());
            em.persist(connection);

            em.getTransaction().commit();
            return connection;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Connection getConnectionById(Long connectionId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Connection.class, connectionId);
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteConnection(Long connectionId) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Connection connection = em.find(Connection.class, connectionId);
            if (connection != null) {
                em.remove(connection);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}
