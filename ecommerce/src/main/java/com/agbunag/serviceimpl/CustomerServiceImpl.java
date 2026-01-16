package com.agbunag.serviceimpl;

import com.agbunag.entity.CustomerData;
import com.agbunag.model.Customer;
import com.agbunag.repository.CustomerDataRepository;
import com.agbunag.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerDataRepository customerDataRepository;

    @Override
    public Customer create(Customer customer) {
        log.info("Creating customer: " + customer);

        CustomerData customerData = new CustomerData();
        customerData.setCustomerId(customer.getCustomerId());
        customerData.setFirstname(customer.getFirstname());
        customerData.setMiddlename(customer.getMiddlename());
        customerData.setLastname(customer.getLastname());
        customerData.setUsername(customer.getUsername());
        customerData.setPassword(customer.getPassword());
        customerData.setGender(customer.getGender());
        customerData.setDateOfBirth(customer.getDateOfBirth());

        CustomerData savedCustomerData = customerDataRepository.save(customerData);
        log.info("Successfully saved customer with ID: " + savedCustomerData.getId());

        customer.setCustomerId(savedCustomerData.getId()); // Set the customerId in the Customer model
        customer.setId(savedCustomerData.getId()); // Optional: sync the ID if required

        return customer;
    }

    @Override
    public List<Customer> findAll() {
        return customerDataRepository.findAll().stream()
                .map(this::convertToCustomerModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Customer> findById(int customerId) {
        return customerDataRepository.findById(customerId).map(this::convertToCustomerModel);
    }


    @Override
    public Optional<Customer> findByUsernameAndPassword(String username, String password) {
        log.info("Attempting login for user: " + username);
        Optional<CustomerData> customerData = customerDataRepository.findByUsernameAndPassword(username, password);
        return customerData.map(this::convertToCustomerModel);
    }

    private Customer convertToCustomerModel(CustomerData customerData) {
        Customer customer = new Customer();
        customer.setId(customerData.getId());
        customer.setCustomerId(customerData.getCustomerId());
        customer.setFirstname(customerData.getFirstname());
        customer.setMiddlename(customerData.getMiddlename());
        customer.setLastname(customerData.getLastname());
        customer.setUsername(customerData.getUsername());
        customer.setPassword(customerData.getPassword());
        customer.setGender(customerData.getGender());
        customer.setDateOfBirth(customerData.getDateOfBirth());
        return customer;
    }
}
