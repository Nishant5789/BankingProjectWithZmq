package org.motadata.Server.service;

import org.motadata.Server.model.Account;

import java.util.List;

public interface AccountService {

    String getBalance(String AccountNumber);

    void deposit(String AccountNumber, double amount);

    void withdraw(String AccountNumber, double amount);

    String viewTransactionHistory(String AccountNumber);

    void transferMoney(String senderAccountNumber, String recipientAccountNumber, double amount);

    void handleTransferMoney();
}
