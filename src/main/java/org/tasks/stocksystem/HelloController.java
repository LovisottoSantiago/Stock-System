package org.tasks.stocksystem;

import conexion.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
        System.out.println("Hello world!");

        // Database Connection Test
        DatabaseConnection conexion = new DatabaseConnection();
        conexion.getConnection("user", "password");

    }
}