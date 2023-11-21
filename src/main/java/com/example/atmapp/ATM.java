package com.example.atmapp;

import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ATM {

    public void viewAccountBalanceFX(Customer currentCustomer) {
        try {
            List<Account> customerAccounts = DataHandler.fetchCustomerAccounts(currentCustomer.getId());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);

            if (customerAccounts.size() == 1) {
                Account account = customerAccounts.get(0);
                alert.setContentText("Balance in your account (" + account.getAccountNumber() + "): $" + account.getBalance());
            } else {
                StringBuilder balances = new StringBuilder();
                for (Account acc : customerAccounts) {
                    balances.append("Account ").append(acc.getAccountNumber()).append(": $").append(acc.getBalance()).append("\n");
                }
                alert.setContentText(balances.toString());
            }
            alert.showAndWait();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("A database error occurred.");
        }
    }

    public void performWithdrawalFX(Customer currentCustomer) {
        try {
            List<Account> customerAccounts = DataHandler.fetchCustomerAccounts(currentCustomer.getId());
            ChoiceDialog<String> accountChoiceDialog = createAccountChoiceDialog(customerAccounts, "Choose an account to withdraw from:");
            Optional<String> accountResult = accountChoiceDialog.showAndWait();

            accountResult.ifPresent(selectedDetail -> handleWithdrawal(customerAccounts, selectedDetail));
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("A database error occurred.");
        }
    }

    public void performDepositFX(Customer currentCustomer) {
        try {
            List<Account> customerAccounts = DataHandler.fetchCustomerAccounts(currentCustomer.getId());
            ChoiceDialog<String> accountChoiceDialog = createAccountChoiceDialog(customerAccounts, "Choose an account to deposit into:");
            Optional<String> accountResult = accountChoiceDialog.showAndWait();

            accountResult.ifPresent(selectedDetail -> handleDeposit(customerAccounts, selectedDetail));
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("A database error occurred.");
        }
    }

    public void performTransferFX(Customer currentCustomer) {
        try {
            List<Account> accounts = DataHandler.fetchCustomerAccounts(currentCustomer.getId());
            ChoiceDialog<Account> fromAccountDialog = createTransferAccountChoiceDialog(accounts, "Choose the account to transfer FROM:");
            Optional<Account> fromResult = fromAccountDialog.showAndWait();

            fromResult.ifPresent(fromAccount -> handleTransfer(accounts, fromAccount));
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("A database error occurred.");
        }
    }

    private void handleWithdrawal(List<Account> customerAccounts, String selectedDetail) {
        Account selectedAccount = findAccountByDetail(customerAccounts, selectedDetail);
        if (selectedAccount != null) {
            TextInputDialog dialog = new TextInputDialog("0");
            dialog.setTitle("Withdrawal");
            dialog.setHeaderText("Enter amount to withdraw:");
            Optional<String> result = dialog.showAndWait();

            result.ifPresent(amountStr -> {
                try {
                    double amount = Double.parseDouble(amountStr);
                    if (selectedAccount.getBalance() >= amount) {
                        selectedAccount.withdraw(amount);
                        DataHandler.updateAccountBalance(selectedAccount.getAccountNumber(), selectedAccount.getBalance());
                        showAlert("Successfully withdrew $" + amount + " from account " + selectedAccount.getAccountNumber());
                    } else {
                        showAlert("Insufficient funds in account " + selectedAccount.getAccountNumber());
                    }
                } catch (NumberFormatException | SQLException e) {
                    showAlert("Invalid amount entered or a database error occurred.");
                }
            });
        }
    }

    private void handleDeposit(List<Account> customerAccounts, String selectedDetail) {
        Account selectedAccount = findAccountByDetail(customerAccounts, selectedDetail);
        if (selectedAccount != null) {
            TextInputDialog dialog = new TextInputDialog("0");
            dialog.setTitle("Deposit");
            dialog.setHeaderText("Enter amount to deposit:");
            Optional<String> result = dialog.showAndWait();

            result.ifPresent(amountStr -> {
                try {
                    double amount = Double.parseDouble(amountStr);
                    selectedAccount.deposit(amount);
                    DataHandler.updateAccountBalance(selectedAccount.getAccountNumber(), selectedAccount.getBalance());
                    showAlert("Successfully deposited $" + amount + " to account " + selectedAccount.getAccountNumber());
                } catch (NumberFormatException | SQLException e) {
                    showAlert("Invalid amount entered or a database error occurred.");
                }
            });
        }
    }

    private void handleTransfer(List<Account> accounts, Account fromAccount) {
        List<Account> otherAccounts = new ArrayList<>(accounts);
        otherAccounts.remove(fromAccount);

        ChoiceDialog<Account> toAccountDialog = createTransferAccountChoiceDialog(otherAccounts, "Choose the account to transfer TO:");
        Optional<Account> toResult = toAccountDialog.showAndWait();

        toResult.ifPresent(toAccount -> {
            TextInputDialog amountDialog = new TextInputDialog("0");
            amountDialog.setTitle("Transfer");
            amountDialog.setHeaderText("Enter amount to transfer:");
            Optional<String> amountResult = amountDialog.showAndWait();

            amountResult.ifPresent(amountStr -> {
                try {
                    double amount = Double.parseDouble(amountStr);
                    if (fromAccount.getBalance() >= amount) {
                        fromAccount.withdraw(amount);
                        toAccount.deposit(amount);
                        DataHandler.updateAccountBalance(fromAccount.getAccountNumber(), fromAccount.getBalance());
                        DataHandler.updateAccountBalance(toAccount.getAccountNumber(), toAccount.getBalance());
                        showAlert("Successfully transferred $" + amount + " from account " + fromAccount.getAccountNumber() + " to " + toAccount.getAccountNumber());
                    } else {
                        showAlert("Insufficient funds in account " + fromAccount.getAccountNumber());
                    }
                } catch (NumberFormatException | SQLException e) {
                    showAlert("Invalid amount entered or a database error occurred.");
                }
            });
        });
    }

    private ChoiceDialog<String> createAccountChoiceDialog(List<Account> customerAccounts, String headerText) {
        List<String> choices = customerAccounts.stream()
                .map(account -> "Account Number: " + account.getAccountNumber() + " - Balance: $" + account.getBalance())
                .collect(Collectors.toList());

        ChoiceDialog<String> accountChoiceDialog = new ChoiceDialog<>(choices.get(0), choices);
        accountChoiceDialog.setTitle("Select Account");
        accountChoiceDialog.setHeaderText(headerText);
        return accountChoiceDialog;
    }

    private ChoiceDialog<Account> createTransferAccountChoiceDialog(List<Account> accounts, String headerText) {
        ChoiceDialog<Account> accountChoiceDialog = new ChoiceDialog<>(accounts.get(0), accounts);
        accountChoiceDialog.setTitle("Transfer");
        accountChoiceDialog.setHeaderText(headerText);
        accountChoiceDialog.setContentText("Select account:");
        return accountChoiceDialog;
    }

    private Account findAccountByDetail(List<Account> customerAccounts, String selectedDetail) {
        return customerAccounts.stream()
                .filter(account -> selectedDetail.contains(account.getAccountNumber()))
                .findFirst()
                .orElse(null);
    }

    private void showAlert(String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
