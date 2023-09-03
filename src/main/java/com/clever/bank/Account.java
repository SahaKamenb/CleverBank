package com.clever.bank;
import lombok.Data;

@Data
public class Account {
    private String accountNumber;
    private double balance;
    private Bank bank;

    public Account(String accountNumber, double initialBalance, Bank bank) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
        this.bank = bank;
    }

    public synchronized void deposit(double amount) {
        balance += amount;
    }


    public static Account findAccountByAccountNumber(String accountNumber) {
        for (User user : Main.users) {
            for (Account account : user.getAccounts()) {
                if (account.getAccountNumber().equals(accountNumber)) {
                    return account;
                }
            }
        }
        return null;
    }


    public synchronized void withdraw(double amount) {
        if (balance >= amount) {
            balance -= amount;
        } else {
            System.out.println("Insufficient funds");
        }
    }



}
