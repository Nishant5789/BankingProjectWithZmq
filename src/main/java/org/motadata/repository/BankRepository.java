package org.motadata.repository;

import org.motadata.model.Customer;

import java.util.HashMap;
import java.util.Map;

public class BankRepository {
    private static Map<String, Customer> BankDBMap = new HashMap<>();

    public Map<String, Customer> getBankDBMap() {
        return BankDBMap;
    }
}
