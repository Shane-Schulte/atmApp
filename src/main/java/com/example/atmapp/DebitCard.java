package com.example.atmapp;


public class DebitCard {
    private final String cardNumber;
    private final int customerId;

    public DebitCard(String cardNumber, int customerId) {
        this.cardNumber = cardNumber;
        this.customerId = customerId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public int getCustomerId() {
        return customerId;
    }
}

