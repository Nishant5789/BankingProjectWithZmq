package org.motadata.Server.service;

public interface AuthService {

    String authenticateUser(String username, String password);

    String handleForgotPassword(String username);

    String verifySecurityAnswer(String username, String answer);

    public String resetPassword(String username, String newPassword);
}
