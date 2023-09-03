package com.clever.bank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionTest {
    @BeforeEach
    public void setUp() {
        Main.users = new ArrayList<>();
    }
    @Test
    public void testDepositTransaction() throws IOException {
        User user = new User("testUser", "password");
        Main.users.add(user);
        Account account = new Account("123456", 100.0, new Bank("Test Bank"));
        user.addAccount(account);
        Transaction depositTransaction = new Transaction(account, 50.0, TransactionType.Deposit);
        depositTransaction.executeTransaction();
        assertEquals(150.0, account.getBalance());
    }

    @Test
    public void testWithdrawTransaction() throws IOException {
        Account account = new Account("123456", 100.0, new Bank("Test Bank"));
        Transaction withdrawTransaction = new Transaction(account, 50.0, TransactionType.Withdraw);
        withdrawTransaction.executeTransaction();
        assertEquals(50.0, account.getBalance());
    }

    @Test
    public void testTransferTransaction() throws IOException {
        Account sourceAccount = new Account("123456", 100.0, new Bank("Source Bank"));
        Account destinationAccount = new Account("789012", 50.0, new Bank("Destination Bank"));
        Transaction transferTransaction = new Transaction(sourceAccount, destinationAccount, 25.0, TransactionType.Transfer);
        transferTransaction.executeTransaction();
        assertEquals(75, sourceAccount.getBalance());
        assertEquals(74.75, destinationAccount.getBalance());
    }

    @Test
    public void testInvalidWithdrawTransaction() throws IOException {
        Account account = new Account("123456", 100.0, new Bank("Test Bank"));
        Transaction withdrawTransaction = new Transaction(account, 150.0, TransactionType.Withdraw);
        withdrawTransaction.executeTransaction();
        assertEquals(100.0, account.getBalance()); // Balance should remain unchanged
    }
}

