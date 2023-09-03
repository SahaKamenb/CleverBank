package com.clever.bank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class InterestCalculatorTest {

    @BeforeEach
    public void setUp() {

        Main.users = new ArrayList<>();
    }

    @Test

    public void testInterestCalculation() {
        User user = new User("testUser", "password");
        Account account = new Account("123456", 1000.0, new Bank("Test Bank"));
        user.addAccount(account);
        Main.users.add(user);
        InterestCalculator interestCalculator = new InterestCalculator();
        interestCalculator.start();

        // Дайте процессу расчета процентов время завершиться (например, 2 секунды)
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // Обработка исключения
        }

        assertEquals(1010.0, account.getBalance());
    }



}

