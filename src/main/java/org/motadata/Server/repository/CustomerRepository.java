package org.motadata.Server.repository;

import org.motadata.Server.BankDB;
import org.motadata.Server.model.Customer;

import java.util.ArrayList;
import java.util.List;

public class CustomerRepository {
    public List<Customer> getAllCustomers(){
        return new ArrayList<>(BankDB.getBankDBMap().values());
    }

    public void addCustomer(Customer customer) {
        if (BankDB.getBankDBMap().containsKey(customer.getAccountNumber())) {
        } else {
            BankDB.getBankDBMap().put(customer.getAccountNumber(), customer);
        }
    }


    public Customer getCustomerByAccountNumber(String accountNumber){
        return BankDB.getBankDBMap().get(accountNumber);
    }
}
