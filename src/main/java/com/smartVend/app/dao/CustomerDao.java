package com.smartvend.app.dao;

import java.util.List;

import com.smartvend.app.model.user.Customer;

public interface CustomerDao {
    Customer getCustomerById(String customerId);
    List<Customer> findAll();
}
