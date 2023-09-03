package com.clever.bank;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class InterestCalculator {
    private static final double INTEREST_RATE = 0.01;
    private static final long INTERVAL_SECONDS = 30;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void start() {

        scheduler.scheduleAtFixedRate(this::calculateInterest, 0, INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

    private void calculateInterest() {


        for (User user : Main.users) {
            for (Account account : user.getAccounts()) {
                synchronized (account) {
                    double balance = account.getBalance();
                    double interest = balance * INTEREST_RATE;
                    account.deposit(interest);
                }
            }
        }
        Main.saveUsersToFile(Main.users);
    }
}






