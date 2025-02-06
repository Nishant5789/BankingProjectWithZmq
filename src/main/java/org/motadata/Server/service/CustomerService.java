package org.motadata.Server.service;

import org.motadata.Server.model.Customer;
import org.motadata.Server.repository.CustomerRepository;

import java.util.List;

public class CustomerService  {
    CustomerRepository customerRepository = new CustomerRepository();
    public void addCustomer(Customer customer) {
        customerRepository.addCustomer(customer);
    }

    List<Customer> getAllCustomers(){
        return customerRepository.getAllCustomers();
    }

    Customer getCustomerByAccountNumber(String accountNumber){
        return customerRepository.getCustomerByAccountNumber(accountNumber);
    }
}
