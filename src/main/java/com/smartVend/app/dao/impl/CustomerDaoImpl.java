package com.smartvend.app.dao.impl;

import java.util.List;

import com.smartvend.app.dao.CustomerDao;
import com.smartvend.app.model.user.Customer;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public class CustomerDaoImpl implements CustomerDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Customer getCustomerById(String customerId) {
        return entityManager.find(Customer.class, customerId);
    }

    @Override
    public List<Customer> findAll() {
        return entityManager.createQuery(
                "SELECT c FROM Customer c", Customer.class)
                .getResultList();
    }
}
