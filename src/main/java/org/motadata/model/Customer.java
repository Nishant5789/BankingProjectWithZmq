package org.motadata.model;

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
        super(name, id, username, password, securityQuestionIndex, securityAnswer);
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

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String[] getSecurityQuestions() {
        return securityQuestions;
    }

    public void setSecurityQuestions(String[] securityQuestions) {
        this.securityQuestions = securityQuestions;
    }

    public int getSecurityQuestionIndex() {
        return securityQuestionIndex;
    }

    public void setSecurityQuestionIndex(int securityQuestionIndex) {
        this.securityQuestionIndex = securityQuestionIndex;
    }

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }
}

