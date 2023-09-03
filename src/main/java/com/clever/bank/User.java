package com.clever.bank;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class User {
    private String username;
    private String password;
    private List<Account> accounts;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.accounts = Collections.synchronizedList(new ArrayList<>());
    }
    public static User findUserByUsernameAndPassword(String username, String password) {
        for (User user : Main.users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    public void addAccount(Account account) {
        accounts.add(account);
    }
}




