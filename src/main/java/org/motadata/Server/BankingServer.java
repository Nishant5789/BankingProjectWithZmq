package org.motadata.Server;

import org.json.JSONException;
import org.json.JSONObject;
import org.motadata.Server.model.Account;
import org.motadata.Server.model.Customer;
import org.motadata.Server.repository.AccountRepository;
import org.motadata.Server.service.AccountServiceImpl;
import org.motadata.Server.service.AuthServiceImpl;
import org.motadata.Server.service.CustomerService;
import org.zeromq.ZMQ;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BankingServer {
    private static final AuthServiceImpl authService = new AuthServiceImpl();
    private static final AccountServiceImpl accountService = new AccountServiceImpl();
    public static final CustomerService customerService = new CustomerService();

    public static void main(String[] args) throws JSONException {
        seedSampleData();
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket socket = context.socket(ZMQ.REP);
        socket.bind("tcp://*:6555");

        while (!Thread.currentThread().isInterrupted()) {
            String request = socket.recvStr();
            JSONObject requestJson = new JSONObject(request);
            String action = requestJson.getString("action");
            System.out.println(requestJson);

            switch (action) {
                case "AUTHENTICATE":
                    String username = requestJson.getString("username");
                    String password = requestJson.getString("password");
                    String response = authService.authenticateUser(username, password);
                    socket.send(response);
                    break;

                case "FORGOT_PASSWORD":
                    String forgotUser = requestJson.getString("username");
                    String question = authService.handleForgotPassword(forgotUser);
                    socket.send(question);
                    break;

                case "FORGOT_PASSWORD_ANSWER":
                    String ansUser = requestJson.getString("username");
                    String answer = requestJson.getString("answer");
                    String ansResponse = authService.verifySecurityAnswer(ansUser, answer);
                    socket.send(ansResponse);
                    break;

                case "RESET_PASSWORD":
                    String resetUser = requestJson.getString("username");
                    String newPassword = requestJson.getString("newPassword");
                    String resetResponse = authService.resetPassword(resetUser, newPassword);
                    socket.send(resetResponse);
                    break;

                case "VIEW_BALANCE":
                    String accountNumber = requestJson.getString("accountNumber");
                    String balanceResponse = accountService.getBalance(accountNumber);
                    socket.send(balanceResponse);
                    break;

                case "DEPOSIT":
                    String depositAccount = requestJson.getString("accountNumber");
                    String depositType = requestJson.getString("accountType");
                    double depositAmount = requestJson.getDouble("amount");
                    accountService.deposit(depositAccount, depositAmount);
                    socket.send("Deposited " + depositAmount + " successfully");
                    break;

                case "WITHDRAW":
                    String withdrawAccount = requestJson.getString("accountNumber");
                    String withdrawType = requestJson.getString("accountType");
                    double withdrawAmount = requestJson.getDouble("amount");
                    accountService.withdraw(withdrawAccount, withdrawAmount);
                    accountService.updateTransectionHistory(withdrawAccount, withdrawAmount + " is withdraw at " + new Date());

                    socket.send("Withdrawn " + withdrawAmount + " successfully");
                    break;

                case "TRANSFER_MONEY":
                    String senderAccount = requestJson.getString("senderAccountNumber");
                    String receiverAccount = requestJson.getString("recipientAccountNumber");
                    double transferAmount = requestJson.getDouble("amount");
                    accountService.transferMoney(senderAccount, receiverAccount, transferAmount);
                    socket.send("Transfer request initiated");
                    break;

                case "VIEW_TRANSACTION_HISTORY":
                    String historyAccount = requestJson.getString("accountNumber");
                    String transactionHistory = accountService.viewTransactionHistory(historyAccount);
                    socket.send(transactionHistory);
                    break;

                default:
                    socket.send("Invalid action. Please try again.");
            }
        }
    }

    private static void seedSampleData() {
        Customer customer1 = new Customer("Nishant", "C001", "nishant1", "nishant123", 1, "Blue",
                "123456", "123 Main St", List.of(new Account("Savings", 1000.0), new Account("Salary", 1000.0)));
        Customer customer2 = new Customer("Dhruv", "C002", "dhruv1", "dhruv123", 0, "fluffy",
                "654321", "456 Elm St", List.of(new Account("Savings", 2000.0)));
        customerService.addCustomer(customer1);
        customerService.addCustomer(customer2);
        System.out.println("sample data is added - bankserver is start..");
    }
}


