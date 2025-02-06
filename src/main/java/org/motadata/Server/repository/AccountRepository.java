package org.motadata.Server.repository;

import org.motadata.Server.BankDB;
import org.motadata.Server.model.Account;
import org.motadata.Server.model.Customer;

import java.util.ArrayList;
import java.util.List;

public class AccountRepository {

    public List<Account> getAllTypeAccounts(String AccountNumber) {
        Customer customer = BankDB.getBankDBMap().get(AccountNumber);
        return  new ArrayList<>(customer.getAccounts());
    }

    public Account getSavingAccountByAccountNumber(String senderAccountNumber) {
        List<Account> accounts = getAllTypeAccounts(senderAccountNumber);
        return accounts.get(0);
    }

    public String getBalance(String AccountNumber) {
        StringBuilder balanceInfo = new StringBuilder("Your accounts:\n");
        for (Account account : getAllTypeAccounts(AccountNumber)) {
            balanceInfo.append(account.getAccountType()).append(" - Balance: ").append(account.getBalance()).append("\n");
        }
        return balanceInfo.toString();
    }
}
