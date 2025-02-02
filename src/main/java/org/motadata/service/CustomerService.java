package org.motadata.service;

import org.motadata.model.Customer;

import java.util.List;

public interface CustomerService {
    void addCustomer(Customer customer);

    List<Customer> getAllCustomers();

    Customer getCustomerByAccountNumber(String accountNumber);
}

