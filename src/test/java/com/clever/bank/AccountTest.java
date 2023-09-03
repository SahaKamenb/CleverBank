package com.clever.bank;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AccountTest {

    @Test
    public void testDeposit() {
        Account account = new Account("123456", 100.0, new Bank("Test Bank"));
        account.deposit(50.0);
        assertEquals(150.0, account.getBalance());
    }

    @Test
    public void testWithdraw() {
        Account account = new Account("123456", 100.0, new Bank("Test Bank"));
        account.withdraw(50.0);
        assertEquals(50.0, account.getBalance());
    }

    @Test
    public void testWithdrawInsufficientFunds() {
        Account account = new Account("123456", 100.0, new Bank("Test Bank"));
        account.withdraw(150.0); // Trying to withdraw more than the balance
        assertEquals(100.0, account.getBalance()); // Balance should remain unchanged
    }
}

