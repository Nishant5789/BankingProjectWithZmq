package org.motadata.service;

import org.motadata.model.Account;

import java.util.List;

public interface AccountService {
    void deposit(String AccountNumber, double amount);

    void withdraw(String AccountNumber, double amount);

    List<Account> getAllTypeAccounts(String AccountNumber);

    void viewTransactionHistory(Account account);

    void transferMoney(String senderAccountNumber, String recipientAccountNumber, double amount);

    void handleTransferMoney();

    Account getSavingAccountByAccountNumber(String senderAccountNumber);
}
