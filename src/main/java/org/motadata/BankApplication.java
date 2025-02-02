package org.motadata;

import org.motadata.controller.BankController;

public class BankApplication {
    public static void main(String[] args) {
        BankController bankController = new BankController();
        bankController.seedSampleData();
        bankController.startBankingSystem();
    }
}
