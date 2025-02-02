package org.motadata.service;

import org.motadata.model.Account;
import org.motadata.repository.BankRepository;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AccountServiceImpl implements AccountService {
    private static final String ZMQ_ADDRESS = "tcp://localhost:5555";
    BankRepository bankRepository = new BankRepository();
    private static final Map<String, String> responseMap = new ConcurrentHashMap<>();
    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            2,
            4,
            60, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(5),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    @Override
    public void deposit(String AccountNumber, double amount) {
        Account account = getSavingAccountByAccountNumber(AccountNumber);
        account.setBalance(account.getBalance() + amount);
        updateTransectionHistory(account, amount + " is deposit at " + new Date());
    }

    @Override
    public void withdraw(String AccountNumber, double amount) {
        Account account = getSavingAccountByAccountNumber(AccountNumber);
        account.setBalance(account.getBalance() - amount);
        updateTransectionHistory(account, amount + " is withdraw at " + new Date());
    }

    public void updateTransectionHistory(Account account, String transactionEntry) {
        account.getTransactionHistory().add(transactionEntry);
    }

    @Override
    public List<Account> getAllTypeAccounts(String AccountNumber) {
        return new ArrayList<>(bankRepository.getBankDBMap().get(AccountNumber).getAccounts());
    }

    @Override
    public void viewTransactionHistory(Account account) {
        for (String transaction : account.getTransactionHistory()) {
            System.out.println(transaction);
        }
    }

    @Override
    public Account getSavingAccountByAccountNumber(String senderAccountNumber) {
        return bankRepository.getBankDBMap().get(senderAccountNumber).getAccounts().get(0);
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
                    Account senderAccount = getSavingAccountByAccountNumber(senderAccountNumber);
                    if (senderAccount != null) {
                        withdraw(senderAccountNumber, amount);
                        updateTransectionHistory(senderAccount, amount + " transfer to acc - " + recipientAccountNumber);
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

                    Account recipientAccountObj = getSavingAccountByAccountNumber(recipientAccount);
                    if (recipientAccountObj != null) {
                        deposit(recipientAccount, amount);
                        updateTransectionHistory(recipientAccountObj, amount + " transfer from acc - " + senderAccount);
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
