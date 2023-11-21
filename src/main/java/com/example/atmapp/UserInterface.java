package com.example.atmapp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

public class UserInterface {

    private Stage primaryStage;
    private ATM atm;
    private Customer currentCustomer;

    public UserInterface(Stage primaryStage, ATM atm) {
        this.primaryStage = primaryStage;
        this.atm = atm;
    }

    public void start() {
        primaryStage.setTitle("ATM");
        primaryStage.setScene(createLoginScene());
        primaryStage.show();
    }

    private Scene createLoginScene() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Label cardNumberLabel = new Label("Card Number:");
        TextField cardNumberField = new TextField();
        Label pinLabel = new Label("PIN:");
        PasswordField pinField = new PasswordField();
        Button loginBtn = new Button("Login");
        Label messageLabel = new Label();

        grid.add(cardNumberLabel, 0, 0);
        grid.add(cardNumberField, 1, 0);
        grid.add(pinLabel, 0, 1);
        grid.add(pinField, 1, 1);
        grid.add(loginBtn, 1, 2);
        grid.add(messageLabel, 1, 3);

        loginBtn.setOnAction(e -> {
            String cardNumber = cardNumberField.getText();
            String pin = pinField.getText();
            try {
                Customer customer = DataHandler.authenticateCustomer(cardNumber, pin);
                if (customer != null) {
                    currentCustomer = customer;
                    primaryStage.setScene(createMainMenuScene());
                } else {
                    messageLabel.setText("Invalid card number or PIN.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                messageLabel.setText("A database error occurred.");
            }
        });

        return new Scene(grid, 300, 200);
    }

    private Scene createMainMenuScene() {
        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);

        Button viewBalanceBtn = new Button("View Balance");
        Button withdrawBtn = new Button("Withdraw");
        Button depositBtn = new Button("Deposit");
        Button transferBtn = new Button("Transfer");
        Button logoutBtn = new Button("Logout");

        vbox.getChildren().addAll(viewBalanceBtn, withdrawBtn, depositBtn, transferBtn, logoutBtn);

        viewBalanceBtn.setOnAction(e -> {
            atm.viewAccountBalanceFX(currentCustomer);
        });
        withdrawBtn.setOnAction(e -> {
            atm.performWithdrawalFX(currentCustomer);
        });
        depositBtn.setOnAction(e -> {
            atm.performDepositFX(currentCustomer);
        });
        transferBtn.setOnAction(e -> {
            atm.performTransferFX(currentCustomer);
        });
        logoutBtn.setOnAction(e -> primaryStage.setScene(createLoginScene()));

        return new Scene(vbox, 300, 200);
    }
}

