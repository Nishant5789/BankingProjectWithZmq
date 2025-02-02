package org.motadata.service;

import org.motadata.model.Account;
import org.motadata.model.Customer;
import org.motadata.repository.BankRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomerServiceImpl implements CustomerService {
    BankRepository bankRepository = new BankRepository();

    @Override
    public void addCustomer(Customer customer) {
        if (bankRepository.getBankDBMap().containsKey(customer.getAccountNumber())) {
            System.out.println("Customer with account number already exists.");
        } else {
            bankRepository.getBankDBMap().put(customer.getAccountNumber(), customer);
            System.out.println("Customer added successfully!");
        }
    }

    @Override
    public List<Customer> getAllCustomers() {
        return new ArrayList<>(bankRepository.getBankDBMap().values());
    }


    @Override
    public Customer getCustomerByAccountNumber(String accountNumber) {
        return bankRepository.getBankDBMap().get(accountNumber);
    }
}
