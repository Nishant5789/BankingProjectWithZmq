
package org.motadata.Client;

import org.json.JSONException;

public class BankClient {
    public static void main(String[] args) throws JSONException {
        BankController bankController = new BankController();
        bankController.startBankingSystem();
    }
}
