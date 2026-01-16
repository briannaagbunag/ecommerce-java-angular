package com.agbunag.service;

import com.agbunag.model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    Customer create(Customer customer);
    Optional<Customer> findByUsernameAndPassword(String username, String password);
    List<Customer> findAll();
    Optional<Customer> findById(int customerId);
}
