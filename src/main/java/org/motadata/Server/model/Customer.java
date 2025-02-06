package org.motadata.Server.model;

import java.util.List;

public class Customer extends Person{
    private String accountNumber;
    private String address;
    protected String username;
    private String password;
    private String[] securityQuestions = {
            "What's your first watch movie?",
            "Who's your favorite sportsperson?",
            "What's your hobby?"
    };
    private int securityQuestionIndex;
    private String securityAnswer;
    private List<Account> accounts;

    public Customer(String name, String id, String username, String password,
                    int securityQuestionIndex, String securityAnswer,
                    String accountNumber, String address, List<Account> accounts) {
        super(name, id);
        this.accountNumber = accountNumber;
        this.address = address;
        this.username = username;
        this.password = password; // Storing password as String
        this.securityQuestionIndex = securityQuestionIndex;
        this.securityAnswer = securityAnswer.toLowerCase().trim(); // Normalize the answer for comparison
        this.accounts = accounts;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getSecurityQuestionIndex() {
        return securityQuestionIndex;
    }

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public List<Account> getAccounts() {
        return accounts;
    }
}

