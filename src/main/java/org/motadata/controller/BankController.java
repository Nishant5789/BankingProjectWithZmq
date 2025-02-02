package org.motadata.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.motadata.model.Account;
import org.motadata.model.Customer;
import org.motadata.service.AccountServiceImpl;
import org.motadata.service.AuthServiceImpl;
import org.motadata.service.CustomerServiceImpl;

public class BankController {

    private final Scanner scanner = new Scanner(System.in);
    public final CustomerServiceImpl customerService = new CustomerServiceImpl();
    public final AccountServiceImpl accountService = new AccountServiceImpl();
    public final AuthServiceImpl authService = new AuthServiceImpl();
    Map<Integer, String> mappingAccountType = new HashMap<>();

    public BankController() {
        mappingAccountType.put(0, "Savings");
        mappingAccountType.put(1, "Salary");
    }

    public void startBankingSystem() {
        System.out.println("Welcome to the Banking System!");

        boolean loggedIn = false;
        Customer loggedInCustomer = null;

        // Login Attempts
        int attempts = 0;
        while (attempts < 3 && !loggedIn) {
            System.out.print("Enter Username: ");
            String username = scanner.nextLine();
            System.out.print("Enter Password: ");
            String password = scanner.nextLine();


            loggedInCustomer = authService.authenticateUser(username, password);

            if (loggedInCustomer != null) {
                loggedIn = true;
                System.out.println("Login Successful!");
            } else {
                attempts++;
                System.out.println("Invalid username or password. Attempts left: " + (3 - attempts));
                if (attempts == 3) {
                    System.out.println("Maximum login attempts reached. Forgot Password? (Y/N): ");
                    String choice = scanner.nextLine();
                    if (choice.equalsIgnoreCase("Y")) {
                        authService.forgotPassword();
                        showMenu(loggedInCustomer.getAccountNumber());
                    } else {
                        System.out.println("Exiting system. Goodbye!");
                        return;
                    }
                }
            }
        }

        if (loggedIn) {
            showMenu(loggedInCustomer.getAccountNumber());
        }
    }

    public void showMenu(String AccountNumber) {
        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. View Balance");
            System.out.println("2. Deposit Money");
            System.out.println("3. Withdraw Money");
            System.out.println("4. Transfer Money");
            System.out.println("5. View Transaction History");
            System.out.println("6. Logout");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            try {
                switch (choice) {
                    case 1 -> viewBalance(AccountNumber);
                    case 2 -> depositMoney(AccountNumber);
                    case 3 -> withdrawMoney(AccountNumber);
                    case 4 -> transferMoney(AccountNumber);
                    case 5 -> viewTransactionHistory(AccountNumber);
                    case 6 -> {
                        System.out.println("Logged out successfully. Goodbye!");
                        return;
                    }
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void viewBalance(String AccountNumber) {
        System.out.println("Your accounts:");
        for (Account account : accountService.getAllTypeAccounts(AccountNumber)) {
            System.out.println(account.getAccountType() + " - Balance: " + account.getBalance());
        }
    }

    public void depositMoney(String AccountNumber) {
        System.out.print("Choose AccountType 0 for Savings & 1 for Salary): ");
        Integer accountTypeIndex = scanner.nextInt();
        System.out.print("Enter Amount to Deposit: ");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        if (amount <= 0) {
            System.out.println("Deposit amount must be greater than zero.");
            return;
        }

        for (Account account : accountService.getAllTypeAccounts(AccountNumber)) {
            if (account.getAccountType().equalsIgnoreCase(mappingAccountType.get(accountTypeIndex))) {
                accountService.deposit(AccountNumber, amount);
                System.out.println("Deposited " + amount + " to " + mappingAccountType.get(accountTypeIndex) + " account. New Balance: " + account.getBalance());
                return;
            }
        }
        System.out.println("Account type not found.");
    }

    public void withdrawMoney(String AccountNumber) {
        System.out.print("Choose AccountType 0 for Savings & 1 for Salary): ");
        Integer accountTypeIndex = scanner.nextInt();
        System.out.print("Enter Amount to Withdraw: ");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        if (amount <= 0) {
            System.out.println("Withdraw amount must be greater than zero.");
            return;
        }

        for (Account account : accountService.getAllTypeAccounts(AccountNumber)) {
            if (account.getAccountType().equalsIgnoreCase(mappingAccountType.get(accountTypeIndex))) {
                if (account.getBalance() < amount) {
                    System.out.println("Insufficient balance.");
                    return;
                }
                accountService.withdraw(AccountNumber, amount);
                System.out.println("Withdrawn " + amount + " from " + mappingAccountType.get(accountTypeIndex) + " account. New Balance: " + account.getBalance());
                return;
            }
        }
        System.out.println("Account type not found.");
    }

    public void transferMoney(String AccountNumber) {
        System.out.print("Enter Recipient Account Number: ");
        String recipientAccountNumber = scanner.nextLine();
        System.out.print("Enter Amount to Transfer: ");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // Consume newline
        boolean checkInSufficialBalance = accountService.getAllTypeAccounts(AccountNumber).stream()
                .filter(account -> account.getAccountType().equalsIgnoreCase(mappingAccountType.get(0)))
                .anyMatch(account -> account.getBalance() < amount);
        if(checkInSufficialBalance){
            System.out.println("Transection Cancel Due To Insufficient balance.");
            return;
        }


        try {
            accountService.transferMoney(AccountNumber, recipientAccountNumber, amount);
            System.out.println("Transferred " + amount + " to account " + recipientAccountNumber);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void viewTransactionHistory(String AccountNumber) {
        for (Account account : accountService.getAllTypeAccounts(AccountNumber)) {
            System.out.println("Transaction history for " + account.getAccountType() + " account:");
            accountService.viewTransactionHistory(account);
        }
    }

    public void seedSampleData() {
        Customer customer1 = new Customer("Nishant", "C001", "nishant1", "nishant123", 1, "Blue",
                "123456", "123 Main St", List.of(new Account("Savings", 1000.0), new Account("Salary", 1000.0)));
        Customer customer2 = new Customer("Dhruv", "C002", "dhruv1", "dhruv123", 0, "fluffy",
                "654321", "456 Elm St", List.of(new Account("Savings", 2000.0)));
        customerService.addCustomer(customer1);
        customerService.addCustomer(customer2);
    }

    public void viewBalanceTestingwithscannerparameter(String AccountNumber) {
        System.out.println("Your accounts:");
        for (Account account : accountService.getAllTypeAccounts(AccountNumber)) {
            System.out.println(account.getAccountType() + " - Balance: " + account.getBalance());
        }
    }

    public void depositMoneyTestingwithscannerparameter(String AccountNumber,Scanner scanner) {
        System.out.print("Choose AccountType 0 for Savings & 1 for Salary): ");
        Integer accountTypeIndex = scanner.nextInt();
        System.out.print("Enter Amount to Deposit: ");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        if (amount <= 0) {
            System.out.println("Deposit amount must be greater than zero.");
            return;
        }

        for (Account account : accountService.getAllTypeAccounts(AccountNumber)) {
            if (account.getAccountType().equalsIgnoreCase(mappingAccountType.get(accountTypeIndex))) {
                accountService.deposit(AccountNumber, amount);
                System.out.println("Deposited " + amount + " to " + mappingAccountType.get(accountTypeIndex) + " account. New Balance: " + account.getBalance());
                return;
            }
        }
        System.out.println("Account type not found.");
    }

    public void withdrawMoneyTestingwithscannerparameter(String AccountNumber, Scanner scanner) {
        System.out.print("Choose AccountType 0 for Savings & 1 for Salary): ");
        Integer accountTypeIndex = scanner.nextInt();
        System.out.print("Enter Amount to Withdraw: ");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        if (amount <= 0) {
            System.out.println("Withdraw amount must be greater than zero.");
            return;
        }

        for (Account account : accountService.getAllTypeAccounts(AccountNumber)) {
            if (account.getAccountType().equalsIgnoreCase(mappingAccountType.get(accountTypeIndex))) {
                if (account.getBalance() < amount) {
                    System.out.println("Insufficient balance.");
                    return;
                }
                accountService.withdraw(AccountNumber, amount);
                System.out.println("Withdrawn " + amount + " from " + mappingAccountType.get(accountTypeIndex) + " account. New Balance: " + account.getBalance());
                return;
            }
        }
        System.out.println("Account type not found.");
    }

    public void transferMoneyTestingwithscannerparameter(String AccountNumber, Scanner scanner) {
        System.out.print("Enter Recipient Account Number: ");
        String recipientAccountNumber = scanner.nextLine();
        System.out.print("Enter Amount to Transfer: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        boolean checkInSufficialBalance = accountService.getAllTypeAccounts(AccountNumber).stream()
                .filter(account -> account.getAccountType().equalsIgnoreCase(mappingAccountType.get(0)))
                .anyMatch(account -> account.getBalance() < amount);
        if(checkInSufficialBalance){
            System.out.println("Transection Cancel Due To Insufficient balance.");
            return;
        }
        try {
            accountService.transferMoney(AccountNumber, recipientAccountNumber, amount);
            System.out.println("Transferred " + amount + " to account " + recipientAccountNumber);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
