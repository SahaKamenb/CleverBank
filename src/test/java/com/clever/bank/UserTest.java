package com.clever.bank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @BeforeEach
    public void setUp() {

        Main.users = new ArrayList<>();
    }
    @Test
    public void testFindUserByUsernameAndPassword() {
        User user = new User("testUser", "password");
        Main.users.add(user);
        User foundUser = User.findUserByUsernameAndPassword("testUser", "password");
        assertNotNull(foundUser);
        assertEquals("testUser", foundUser.getUsername());
    }


}
