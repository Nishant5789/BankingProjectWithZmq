package org.motadata.service;

import org.motadata.model.Customer;
import java.util.Scanner;


public class AuthServiceImpl implements AuthService{
    private final Scanner scanner = new Scanner(System.in);
    private final CustomerService customerService = new CustomerServiceImpl();

    @Override
    public void forgotPassword() {
        System.out.print("Enter Username: ");
        String username = scanner.nextLine();
        for (Customer customer : customerService.getAllCustomers()) {
            if (customer.getUsername().equals(username)) {
                System.out.println("Answer the security question to reset your password:");
                System.out.println("Question: " + getSecurityQuestion(customer.getSecurityQuestionIndex()));
                System.out.print("Answer: ");
                String answer = scanner.nextLine();
                if (answer.equals(customer.getSecurityAnswer())) {
                    System.out.print("Enter New Password: ");
                    String newPassword = scanner.nextLine();
                    customer.setPassword(newPassword);
                    System.out.println("Password reset successful! Please log in again.");
                } else {
                    System.out.println("Incorrect answer. Cannot reset password.");
                }
                return;
            }
        }
        System.out.println("Username not found.");
    }

    @Override
    public Customer authenticateUser(String username, String password) {
        for (Customer customer : customerService.getAllCustomers()) {
            if (customer.getUsername().equals(username) && customer.getPassword().equals(password)) {
                return customer;
            }
        }
        return null;
    }

    private String getSecurityQuestion(int index) {
        String[] questions = {
                "What is your pet's name?",
                "What is your favorite color?",
                "What is your mother's maiden name?"
        };
        return questions[index % questions.length];
    }


}
