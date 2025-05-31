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
    public Customer getCustomerById(long customerId) {
        return entityManager.find(Customer.class, customerId);
    }

    @Override
    public Customer createCustomer(Customer customer) {
        entityManager.persist(customer);
        return customer;
    }

    @Override
    public Customer updateCustomer(Customer customer) {
        return entityManager.merge(customer);
    }

    @Override
    public List<Customer> findAll() {
        return entityManager.createQuery(
                "SELECT c FROM Customer c", Customer.class)
                .getResultList();
    }

    @Override
    public void deleteCustomer(long customerId) {
        Customer customer = entityManager.find(Customer.class, customerId);
        if (customer != null) {
            entityManager.remove(customer);
        }
    }
}
