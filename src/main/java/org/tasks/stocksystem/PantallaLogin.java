package org.tasks.stocksystem;

import conexion.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;


public class PantallaLogin {
    @FXML
    private TextField usernameLabel;

    @FXML
    private PasswordField passwordLabel;

    @FXML
    private Label connectedFlag;

    @FXML
    protected void onHelloButtonClick() {
        String username = usernameLabel.getText();
        String password = passwordLabel.getText();
        DatabaseConnection connection = new DatabaseConnection();
        connection.getConnection(username, password);

        boolean flag = connection.getIsConnected();
        if (flag) {
            connectedFlag.setText("Conectado.");
            connectedFlag.setTextFill(Color.GREEN);
        }
        else {
            connectedFlag.setText("Credenciales incorrectas.");
            connectedFlag.setTextFill(Color.RED);
        }
        System.out.println(flag);

    }
}