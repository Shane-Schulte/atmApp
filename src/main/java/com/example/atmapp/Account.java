package com.example.atmapp;

import java.sql.SQLException;

public class Account {
    private final String accountNumber;
    private double balance;
    private final int customerId;

    public Account(String accountNumber, int customerId, double balance) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.customerId = customerId;
    }

    public void deposit(double amount) throws SQLException {
        if (amount > 0) {
            balance += amount;
            DataHandler.updateAccountBalance(accountNumber, balance);
        } else {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
    }

    public void withdraw(double amount) throws SQLException {
        if (amount > 0 && balance >= amount) {
            balance -= amount;
            DataHandler.updateAccountBalance(accountNumber, balance);
        } else {
            throw new IllegalArgumentException("Insufficient funds or invalid amount.");
        }
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public int getCustomerId() {
        return customerId;
    }
}
