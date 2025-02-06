package org.motadata.Server;

import org.motadata.Server.model.Customer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BankDB {
    private static final Map<String, Customer> BankDBMap = new ConcurrentHashMap<>();
    public static Map<String, Customer> getBankDBMap() {
        return BankDBMap;
    }
}
