package org.motadata.Controller;

import static org.  junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.junit.experimental.theories.suppliers.TestedOn;
import org.motadata.controller.BankController;
import org.motadata.model.Customer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.Assert.*;

public class BankControllerTest {
    private BankController bankController;
    private InputStream originalSystemIn;
    private ByteArrayOutputStream outputStream;

    @Before
    public void setUp() {
        bankController = new BankController();
        bankController.seedSampleData(); // Seed test data
        originalSystemIn = System.in;
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @After
    public void tearDown() {
        System.setIn(originalSystemIn); // Restore system input
    }

    @Test
    public void testAuthenticationSuccess() {
        Customer loggedInCustomer = bankController.authService.authenticateUser("nishant1", "nishant123");
        assertNotNull(loggedInCustomer);
        assertEquals("nishant1", loggedInCustomer.getUsername());
    }

    @Test
    public void testAuthenticationFailure() {
        Customer loggedInCustomer = bankController.authService.authenticateUser("wrongUser", "wrongPass");
        assertNull(loggedInCustomer);
    }

    @Test
    public void testDepositMoney() {
        String simulatedInput = "0\n500\n"; // Choosing Savings (0) and depositing 500
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
        Scanner scanner = new Scanner(System.in);
        bankController.depositMoneyTestingwithscannerparameter("123456", scanner);
        double newBalance = bankController.accountService.getAllTypeAccounts("123456")
                .stream()
                .filter(a -> a.getAccountType().equalsIgnoreCase("Savings"))
                .findFirst().get().getBalance();
        assertEquals(1000.0, newBalance, 0.001);
    }

    @Test
    public void testWithdrawMoneySuccess() {
        String simulatedInput = "0\n500\n"; // Choosing Savings (0) and depositing 500
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
        Scanner scanner = new Scanner(System.in);
        bankController.withdrawMoneyTestingwithscannerparameter("123456", scanner);
        double newBalance = bankController.accountService.getAllTypeAccounts("123456")
                .stream()
                .filter(a -> a.getAccountType().equalsIgnoreCase("Savings"))
                .findFirst().get().getBalance();
        assertEquals(500, newBalance, 0.001);
    }

    @Test
    public void testWithdrawMoneyInsufficientBalance() {
        String simulatedInput = "0\n5000\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        Scanner scanner = new Scanner(System.in);
        bankController.withdrawMoneyTestingwithscannerparameter("123456", scanner);

        String consoleOutput = outputStream.toString();
        assertTrue(consoleOutput.contains("Insufficient balance."));
    }

    @Test
    public void testTransferMoneySuccess() throws InterruptedException {
        String simulatedInput = "789123\n500\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        Scanner scanner = new Scanner(System.in);
        bankController.transferMoneyTestingwithscannerparameter("123456", scanner);

        Thread.sleep(2000);

        double senderBalance = bankController.accountService.getAllTypeAccounts("123456")
                .stream()
                .filter(a -> a.getAccountType().equalsIgnoreCase("Savings"))
                .findFirst().get().getBalance();
        assertEquals(500.0, senderBalance, 0.001);
    }

    @Test
    public void testTransferMoney_InsufficientFunds() {
        String simulatedInput = "654321\n2000\n"; // Transfer 2000 when balance is 1000
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        Scanner scanner = new Scanner(System.in);
        bankController.transferMoneyTestingwithscannerparameter("123456", scanner);

        String consoleOutput = outputStream.toString();
        assertTrue(consoleOutput.contains("Insufficient balance."));
    }

    @Test
    public void testViewBalance_ValidAccount() {
        bankController.viewBalanceTestingwithscannerparameter("123456");
        String consoleOutput = outputStream.toString();
        assertTrue(consoleOutput.contains("Balance: 1000.0"));
    }
}
