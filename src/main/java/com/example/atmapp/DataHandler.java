package com.example.atmapp;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataHandler {

    public static Customer authenticateCustomer(String cardNumber, String pin) throws SQLException, NoSuchAlgorithmException {
        byte[] hashedPin = SecurityUtils.hashPin(pin);
        String sql = "SELECT Customers.customer_id, Customers.name " +
                "FROM Customers " +
                "JOIN DebitCards ON Customers.customer_id = DebitCards.customer_id " +
                "WHERE DebitCards.card_number = ? AND DebitCards.pin_hash = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cardNumber);
            pstmt.setBytes(2, hashedPin);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int customerId = rs.getInt("customer_id");
                String name = rs.getString("name");
                Customer customer = new Customer(customerId, name);
                customer.setAccounts(fetchCustomerAccounts(customerId));
                return customer;
            }
        }
        return null;
    }

    public static List<Account> fetchCustomerAccounts(int customerId) throws SQLException {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT account_number, balance FROM Accounts WHERE customer_id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String accountNumber = rs.getString("account_number");
                double balance = rs.getDouble("balance");
                accounts.add(new Account(accountNumber, customerId, balance));
            }
        }
        return accounts;
    }

    public static void updateAccountBalance(String accountNumber, double newBalance) throws SQLException {
        String sql = "UPDATE Accounts SET balance = ? WHERE account_number = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, newBalance);
            pstmt.setString(2, accountNumber);
            pstmt.executeUpdate();
        }
    }
}
