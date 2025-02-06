package org.motadata.Client;

import org.json.JSONException;
import org.json.JSONObject;
import org.zeromq.ZMQ;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class BankController {
    private final Scanner scanner = new Scanner(System.in);
    private final ZMQ.Context context = ZMQ.context(1);
    private final ZMQ.Socket socket = context.socket(ZMQ.REQ);
    private final Map<Integer, String> mappingAccountType = new HashMap<>();

    public BankController() {
        mappingAccountType.put(0, "Savings");
        mappingAccountType.put(1, "Salary");
        socket.connect("tcp://localhost:6555"); // Connect to the server
    }

    public void startBankingSystem() throws JSONException {
        System.out.println("Welcome to the Banking System!");
        boolean loggedIn = false;
        String accountNumber = null;
        int attempts = 0;

        while (attempts < 3 && !loggedIn) {
            System.out.print("Enter Username: ");
            String username = scanner.nextLine();
            System.out.print("Enter Password: ");
            String password = scanner.nextLine();

            JSONObject requestJson = new JSONObject();
            requestJson.put("action", "AUTHENTICATE");
            requestJson.put("username", username);
            requestJson.put("password", password);
            socket.send(requestJson.toString().getBytes(ZMQ.CHARSET));
            String response = socket.recvStr();

            if (!response.equals("INVALID")) {
                loggedIn = true;
                accountNumber = response;
                System.out.println("Login Successful!");
            } else {
                attempts++;
                System.out.println("Invalid username or password. Attempts left: " + (3 - attempts));

                if (attempts == 3) {
                    System.out.print("Maximum login attempts reached. Forgot Password? (Y/N): ");
                    String choice = scanner.nextLine();
                    if (choice.equalsIgnoreCase("Y")) {
                        forgotPassword();
                    }
                    showMenu(accountNumber);
                }
            }
        }
        showMenu(accountNumber);
    }

    public void forgotPassword() throws JSONException {
        System.out.print("Enter Username: ");
        String username = scanner.nextLine();

        JSONObject requestJson = new JSONObject();
        requestJson.put("action", "FORGOT_PASSWORD");
        requestJson.put("username", username);
        socket.send(requestJson.toString().getBytes(ZMQ.CHARSET));

        String response = socket.recvStr();
        if (response.equals("USERNAME_NOT_FOUND")) {
            System.out.println("Username not found.");
            return;
        }

        System.out.println("Answer the security question to reset your password:");
        System.out.println("Question: " + response);

        System.out.print("Answer: ");
        String answer = scanner.nextLine();

        JSONObject answerRequest = new JSONObject();
        answerRequest.put("action", "FORGOT_PASSWORD_ANSWER");
        answerRequest.put("username", username);
        answerRequest.put("answer", answer);
        socket.send(answerRequest.toString().getBytes(ZMQ.CHARSET));

        String answerResponse = socket.recvStr();

        if (answerResponse.equals("INCORRECT_ANSWER")) {
            System.out.println("Incorrect answer. Cannot reset password.");
            return;
        }

        System.out.print("Enter New Password: ");
        String newPassword = scanner.nextLine();

        JSONObject resetRequest = new JSONObject();
        resetRequest.put("action", "RESET_PASSWORD");
        resetRequest.put("username", username);
        resetRequest.put("newPassword", newPassword);
        socket.send(resetRequest.toString().getBytes(ZMQ.CHARSET));

        String finalResponse = socket.recvStr();
        if (finalResponse.equals("SUCCESS")) {
            System.out.println("Password reset successful! Please log in again.");
        } else {
            System.out.println("Password reset failed.");
        }
    }

    public void showMenu(String accountNumber) throws JSONException {
        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. View Balance");
            System.out.println("2. Deposit Money");
            System.out.println("3. Withdraw Money");
            System.out.println("4. Transfer Money");
            System.out.println("5. View Transaction History");
            System.out.println("6. Logout");
            System.out.print("Enter your choice: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine()); // Fix nextInt() issue
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1 -> viewBalance(accountNumber);
                case 2 -> depositMoney(accountNumber);
                case 3 -> withdrawMoney(accountNumber);
                case 4 -> transferMoney(accountNumber);
                case 5 -> viewTransactionHistory(accountNumber);
                case 6 -> {
                    System.out.println("Logged out successfully. Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    public void viewBalance(String accountNumber) throws JSONException {
        JSONObject requestJson = new JSONObject();
        requestJson.put("action", "VIEW_BALANCE");
        requestJson.put("accountNumber", accountNumber);

        socket.send(requestJson.toString().getBytes(ZMQ.CHARSET));
        System.out.println(socket.recvStr());
    }

    public void depositMoney(String accountNumber) throws JSONException {
        System.out.print("Choose AccountType (0 for Savings, 1 for Salary): ");
        int accountTypeIndex;
        try {
            accountTypeIndex = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter 0 or 1.");
            return;
        }

        if (!mappingAccountType.containsKey(accountTypeIndex)) {
            System.out.println("Invalid account type.");
            return;
        }

        System.out.print("Enter Amount to Deposit: ");
        double amount;
        try {
            amount = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount.");
            return;
        }

        if (amount <= 0) {
            System.out.println("Deposit amount must be greater than zero.");
            return;
        }

        JSONObject requestJson = new JSONObject();
        requestJson.put("action", "DEPOSIT");
        requestJson.put("accountNumber", accountNumber);
        requestJson.put("accountType", mappingAccountType.get(accountTypeIndex));
        requestJson.put("amount", amount);

        socket.send(requestJson.toString().getBytes(ZMQ.CHARSET));
        System.out.println(socket.recvStr());
    }

    public void withdrawMoney(String accountNumber) throws JSONException {
        System.out.print("Choose AccountType (0 for Savings, 1 for Salary): ");
        int accountTypeIndex;
        try {
            accountTypeIndex = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter 0 or 1.");
            return;
        }

        if (!mappingAccountType.containsKey(accountTypeIndex)) {
            System.out.println("Invalid account type.");
            return;
        }

        System.out.print("Enter Amount to Withdraw: ");
        double amount;
        try {
            amount = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount.");
            return;
        }

        if (amount <= 0) {
            System.out.println("Withdraw amount must be greater than zero.");
            return;
        }

        JSONObject requestJson = new JSONObject();
        requestJson.put("action", "WITHDRAW");
        requestJson.put("accountNumber", accountNumber);
        requestJson.put("accountType", mappingAccountType.get(accountTypeIndex));
        requestJson.put("amount", amount);

        socket.send(requestJson.toString().getBytes(ZMQ.CHARSET));
        System.out.println(socket.recvStr());
    }

    public void transferMoney(String accountNumber) throws JSONException {
        System.out.print("Enter Recipient Account Number: ");
        String recipientAccountNumber = scanner.nextLine();
        System.out.print("Enter Amount to Transfer: ");
        double amount;
        try {
            amount = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount.");
            return;
        }

        JSONObject requestJson = new JSONObject();
        requestJson.put("action", "TRANSFER_MONEY");
        requestJson.put("senderAccountNumber", accountNumber);
        requestJson.put("recipientAccountNumber", recipientAccountNumber);
        requestJson.put("amount", amount);

        socket.send(requestJson.toString().getBytes(ZMQ.CHARSET));
        System.out.println(socket.recvStr());
    }

    public void viewTransactionHistory(String accountNumber) throws JSONException {
        JSONObject requestJson = new JSONObject();
        requestJson.put("action", "VIEW_TRANSACTION_HISTORY");
        requestJson.put("accountNumber", accountNumber);

        socket.send(requestJson.toString().getBytes(ZMQ.CHARSET));
        System.out.println(socket.recvStr());
    }

    public void startBankingSystemTestingwithscannerparameter(Scanner scanner) throws JSONException {
        System.out.println("Welcome to the Banking System!");
        boolean loggedIn = false;
        String accountNumber = null;
        int attempts = 0;

        while (attempts < 3 && !loggedIn) {
            System.out.print("Enter Username: ");
            String username = scanner.nextLine();
            System.out.print("Enter Password: ");
            String password = scanner.nextLine();

            JSONObject requestJson = new JSONObject();
            requestJson.put("action", "AUTHENTICATE");
            requestJson.put("username", username);
            requestJson.put("password", password);
            socket.send(requestJson.toString().getBytes(ZMQ.CHARSET));
            String response = socket.recvStr();

            if (!response.equals("INVALID")) {
                loggedIn = true;
                accountNumber = response;
                System.out.println("Login Successful!");
            } else {
                attempts++;
                System.out.println("Invalid username or password. Attempts left: " + (3 - attempts));

                if (attempts == 3) {
                    System.out.print("Maximum login attempts reached. Forgot Password? (Y/N): ");
                    String choice = scanner.nextLine();
                    if (choice.equalsIgnoreCase("Y")) {
                        forgotPassword();
                    }
                    showMenu(accountNumber);
                }
            }
        }
    }


    public void depositMoneyTestingwithscannerparameter(String accountNumber, Scanner scanner) throws JSONException {
        System.out.print("Choose AccountType (0 for Savings, 1 for Salary): ");
        int accountTypeIndex;
        try {
            accountTypeIndex = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter 0 or 1.");
            return;
        }

        if (!mappingAccountType.containsKey(accountTypeIndex)) {
            System.out.println("Invalid account type.");
            return;
        }

        System.out.print("Enter Amount to Deposit: ");
        double amount;
        try {
            amount = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount.");
            return;
        }

        if (amount <= 0) {
            System.out.println("Deposit amount must be greater than zero.");
            return;
        }

        JSONObject requestJson = new JSONObject();
        requestJson.put("action", "DEPOSIT");
        requestJson.put("accountNumber", accountNumber);
        requestJson.put("accountType", mappingAccountType.get(accountTypeIndex));
        requestJson.put("amount", amount);

        socket.send(requestJson.toString().getBytes(ZMQ.CHARSET));
        System.out.println(socket.recvStr());
    }

    public void withdrawMoneyTestingwithscannerparameter(String accountNumber, Scanner scanner) throws JSONException {
        System.out.print("Choose AccountType (0 for Savings, 1 for Salary): ");
        int accountTypeIndex;
        try {
            accountTypeIndex = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter 0 or 1.");
            return;
        }

        if (!mappingAccountType.containsKey(accountTypeIndex)) {
            System.out.println("Invalid account type.");
            return;
        }

        System.out.print("Enter Amount to Withdraw: ");
        double amount;
        try {
            amount = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount.");
            return;
        }

        if (amount <= 0) {
            System.out.println("Withdraw amount must be greater than zero.");
            return;
        }

        JSONObject requestJson = new JSONObject();
        requestJson.put("action", "WITHDRAW");
        requestJson.put("accountNumber", accountNumber);
        requestJson.put("accountType", mappingAccountType.get(accountTypeIndex));
        requestJson.put("amount", amount);

        socket.send(requestJson.toString().getBytes(ZMQ.CHARSET));
        System.out.println(socket.recvStr());
    }

    public void transferMoneyTestingwithscannerparameter(String accountNumber, Scanner scanner) throws JSONException {
        System.out.print("Enter Recipient Account Number: ");
        String recipientAccountNumber = scanner.nextLine();
        System.out.print("Enter Amount to Transfer: ");
        double amount;
        try {
            amount = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount.");
            return;
        }

        JSONObject requestJson = new JSONObject();
        requestJson.put("action", "TRANSFER_MONEY");
        requestJson.put("senderAccountNumber", accountNumber);
        requestJson.put("recipientAccountNumber", recipientAccountNumber);
        requestJson.put("amount", amount);

        socket.send(requestJson.toString().getBytes(ZMQ.CHARSET));
        System.out.println(socket.recvStr());
    }
}
