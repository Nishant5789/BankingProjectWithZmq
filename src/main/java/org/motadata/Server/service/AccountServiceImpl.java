package org.motadata.Server.service;

import org.motadata.Server.model.Account;
import org.motadata.Server.repository.AccountRepository;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AccountServiceImpl implements AccountService {
    private static final String ZMQ_ADDRESS = "tcp://localhost:5555";
    AccountRepository accountRepository = new AccountRepository();
    private static final Map<String, String> responseMap = new ConcurrentHashMap<>();
    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            2,
            4,
            60, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(5),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    @Override
    public String getBalance(String AccountNumber) {
        return accountRepository.getBalance(AccountNumber);
    }

    @Override
    public void deposit(String AccountNumber, double amount) {
        Account account = accountRepository.getSavingAccountByAccountNumber(AccountNumber);
        account.setBalance(account.getBalance() + amount);
    }

    @Override
    public void withdraw(String AccountNumber, double amount) {
        Account account = accountRepository.getSavingAccountByAccountNumber(AccountNumber);
        account.setBalance(account.getBalance() - amount);
    }

    public void updateTransectionHistory(String AccountNumber, String transactionEntry) {
        Account account = accountRepository.getSavingAccountByAccountNumber(AccountNumber);
        account.getTransactionHistory().add(transactionEntry);
    }

    @Override
    public String viewTransactionHistory(String AccountNumber) {
        Account account = accountRepository.getSavingAccountByAccountNumber(AccountNumber);
        StringBuilder transactionInfo = new StringBuilder("Your accounts:\n");

        for (String transaction : account.getTransactionHistory()) {
            transactionInfo.append(transaction).append("\n");
        }
        return transactionInfo.toString();
    }

    @Override
    public void transferMoney(String senderAccountNumber, String recipientAccountNumber, double amount) {
        executor.execute(() -> {
            try (ZContext context = new ZContext()) {
                ZMQ.Socket dealer = context.createSocket(ZMQ.DEALER);
                dealer.setIdentity(senderAccountNumber.getBytes());
                dealer.connect(ZMQ_ADDRESS);

                String request = senderAccountNumber + "|" + recipientAccountNumber + "|" + amount;
                dealer.send(request);
                responseMap.put(senderAccountNumber, request);

                String response = dealer.recvStr();
                if ("SUCCESS".equals(response)) {
                    Account senderAccount = accountRepository.getSavingAccountByAccountNumber(senderAccountNumber);
                    if (senderAccount != null) {
                        withdraw(senderAccountNumber, amount);
                        updateTransectionHistory(senderAccountNumber, amount + " transfer to acc - " + recipientAccountNumber);
                        responseMap.remove(senderAccount);
                    }
                    System.out.println("Transfer completed successfully.");
                } else {
                    System.out.println("Transfer failed: " + response);
                }
            }
        });
    }

    @Override
    public void handleTransferMoney() {
        executor.execute(() -> {
            try (ZContext context = new ZContext()) {
                ZMQ.Socket responder = context.createSocket(ZMQ.DEALER);
                responder.bind("tcp://*:5556");

                while (!Thread.currentThread().isInterrupted()) {
                    String request = responder.recvStr();
                    if (request == null) break;

                    String[] parts = request.split("\\|");
                    String senderAccount = parts[0];
                    String recipientAccount = parts[1];
                    double amount = Double.parseDouble(parts[2]);

                    Account recipientAccountObj = accountRepository.getSavingAccountByAccountNumber(recipientAccount);
                    if (recipientAccountObj != null) {
                        deposit(recipientAccount, amount);
                        updateTransectionHistory(recipientAccount, amount + " transfer from acc - " + senderAccount);
                        responder.send("SUCCESS");
                    } else {
                        responder.send("ERROR: Recipient account not found.");
                    }
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        });
    }
}
