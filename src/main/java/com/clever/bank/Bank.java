package com.clever.bank;

import lombok.Data;


@Data
public class Bank {
    private String name;



    public Bank(String name) {
        this.name = name;

    }



}
