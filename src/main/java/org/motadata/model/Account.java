package org.motadata.model;

import java.util.ArrayList;
import java.util.List;

public class Account {
    private String accountType; // (Savings, Current, Salary)
    private double balance;
    private List<String> transactionHistory;

    public Account(String accountType, double initialBalance) {
        this.accountType = accountType;
        this.balance = initialBalance;
        this.transactionHistory = new ArrayList<>();
        transactionHistory.add("Account created with initial balance: " + initialBalance);
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public List<String> getTransactionHistory() {
        return transactionHistory;
    }

    public void setTransactionHistory(List<String> transactionHistory) {
        this.transactionHistory = transactionHistory;
    }
}
