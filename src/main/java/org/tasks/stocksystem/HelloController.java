package org.tasks.stocksystem;

import conexion.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class HelloController {
    @FXML
    private TextField usernameLabel;

    @FXML
    private PasswordField passwordLabel;

    @FXML
    protected void onHelloButtonClick() {
        String username = usernameLabel.getText();
        String password = passwordLabel.getText();
        DatabaseConnection connection = new DatabaseConnection();
        connection.getConnection(username, password);

    }
}