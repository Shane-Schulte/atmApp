module com.example.atmapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.atmapp to javafx.fxml;
    exports com.example.atmapp;
}