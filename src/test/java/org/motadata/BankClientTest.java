package org.motadata;

import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motadata.Client.BankController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.Assert.*;

public class BankClientTest {
    private BankController bankController;
    private InputStream originalSystemIn;
    private ByteArrayOutputStream outputStream;

    @Before
    public void setUp() {
        bankController = new BankController();
        originalSystemIn = System.in;
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @After
    public void tearDown() {
        System.setIn(originalSystemIn); // Restore system input
    }

    @Test
    public void testAuthenticationSuccess() throws JSONException {
        String simulatedInput = "nishant1\nnishant123\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
        Scanner scanner = new Scanner(System.in);
        bankController.startBankingSystemTestingwithscannerparameter(scanner);
        String consoleOutput = outputStream.toString();
        assertTrue(consoleOutput.contains("Login Successful"));
    }

    @Test
    public void testDepositMoney() throws JSONException {
        String simulatedInput = "0\n500\n"; // Choosing Savings (0) and depositing 500
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
        Scanner scanner = new Scanner(System.in);
        bankController.depositMoneyTestingwithscannerparameter("123456", scanner);
        String consoleOutput = outputStream.toString();
        assertTrue(consoleOutput.contains("Deposited 500.0 successfully"));
    }

    @Test
    public void testWithdrawMoneySuccess() throws JSONException {
        String simulatedInput = "0\n500\n"; // Choosing Savings (0) and depositing 500
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
        Scanner scanner = new Scanner(System.in);
        bankController.withdrawMoneyTestingwithscannerparameter("123456", scanner);
        String consoleOutput = outputStream.toString();
        assertTrue(consoleOutput.contains("Withdrawn 500.0 successfully"));
    }

    @Test
    public void testTransferMoneySuccess() throws InterruptedException, JSONException {
        String simulatedInput = "789123\n100\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        Scanner scanner = new Scanner(System.in);
        bankController.transferMoneyTestingwithscannerparameter("123456", scanner);

        Thread.sleep(2000);
        String consoleOutput = outputStream.toString();
        assertTrue(consoleOutput.contains("Transfer request initiated"));
    }

    @Test
    public void testViewBalance_ValidAccount() throws JSONException {
        bankController.viewBalance("123456");
        String consoleOutput = outputStream.toString();
        assertTrue(consoleOutput.contains("Balance: 1000.0"));
    }
}

