package com.smartvend.app.dao;

import java.util.List;

import com.smartvend.app.model.user.Customer;

public interface CustomerDao {
    Customer getCustomerById(long customerId);

    Customer updateCustomer(Customer customer);

    List<Customer> findAll();

    Customer createCustomer(Customer customer);

    void deleteCustomer(long customerId);
}
