package org.motadata.Server.service;

import org.motadata.Server.model.Customer;

import java.util.Scanner;


public class AuthServiceImpl implements AuthService {
    private final Scanner scanner = new Scanner(System.in);
    private final CustomerService customerService = new CustomerService();

    @Override
    public String authenticateUser(String username, String password) {
        for (Customer customer : customerService.getAllCustomers()) {
            if (customer.getUsername().equals(username) && customer.getPassword().equals(password)) {
                return customer.getAccountNumber();
            }
        }
        return "INVALID";
    }

    private String getSecurityQuestion(int index) {
        String[] questions = {
                "What is your pet's name?",
                "What is your favorite color?",
                "What is your mother's maiden name?"
        };
        return questions[index % questions.length];
    }

    @Override
    public String handleForgotPassword(String username) {
        for (Customer customer : customerService.getAllCustomers()) {
            if (customer.getUsername().equals(username)) {
                return getSecurityQuestion(customer.getSecurityQuestionIndex());
            }
        }
        return "USERNAME_NOT_FOUND";
    }

    @Override
    public String verifySecurityAnswer(String username, String answer) {
        for (Customer customer : customerService.getAllCustomers()) {
            if (customer.getUsername().equals(username)) {
                if (customer.getSecurityAnswer().equalsIgnoreCase(answer.trim())) {
                    return "ANSWER_CORRECT";
                } else {
                    return "INCORRECT_ANSWER";
                }
            }
        }
        return "USERNAME_NOT_FOUND";
    }

    @Override
    public String resetPassword(String username, String newPassword) {
        for (Customer customer : customerService.getAllCustomers()) {
            if (customer.getUsername().equals(username)) {
                customer.setPassword(newPassword);
                return "SUCCESS";
            }
        }
        return "USERNAME_NOT_FOUND";
    }
}
