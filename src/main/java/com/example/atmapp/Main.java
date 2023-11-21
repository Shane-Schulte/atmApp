package com.example.atmapp;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        ATM atm = new ATM();
        UserInterface userInterface = new UserInterface(primaryStage, atm);
        userInterface.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
