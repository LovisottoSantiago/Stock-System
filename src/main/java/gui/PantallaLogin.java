package gui;

import conexion.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.tasks.stocksystem.StockAppMain;

import java.io.IOException;


public class PantallaLogin {
    @FXML
    private TextField usernameLabel;

    @FXML
    private PasswordField passwordLabel;

    @FXML
    private Label connectedFlag;

    @FXML
    protected void onHelloButtonClick() throws IOException {
        String username = usernameLabel.getText();
        String password = passwordLabel.getText();
        DatabaseConnection connection = new DatabaseConnection();
        connection.getConnection(username, password);

        boolean flag = connection.getIsConnected();
        if (flag) {
            connectedFlag.setText("Conectado.");
            connectedFlag.setTextFill(Color.GREEN);

            Stage st = new Stage();
            start(st, connection, username, password);

            Stage currentStage = (Stage) connectedFlag.getScene().getWindow();
            currentStage.close();
        }
        else {
            connectedFlag.setText("Credenciales incorrectas.");
            connectedFlag.setTextFill(Color.RED);
        }
        System.out.println(flag);

    }

    public void start(Stage stage, DatabaseConnection connection, String username, String password) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StockAppMain.class.getResource("Pantalla-Inicio.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1225, 600);
        PantallaInicio controller = fxmlLoader.getController();
        controller.initData(connection, username, password);

        stage.setTitle("Pantalla Principal");
        stage.setScene(scene);
        stage.show();

    }


}