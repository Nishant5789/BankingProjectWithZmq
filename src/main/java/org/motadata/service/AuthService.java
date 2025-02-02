package org.motadata.service;

import org.motadata.model.Account;
import org.motadata.model.Customer;

import java.util.List;

public interface AuthService {
    void forgotPassword();

    Customer authenticateUser(String username, String password);
}
