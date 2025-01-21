package gui;

import conexion.DatabaseConnection;
import dao.ProductoDao;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import modelo.Producto;

import java.util.ArrayList;
import java.util.List;

public class PantallaInicio {

    private DatabaseConnection connection;
    private String username;
    private String password;
    private ProductoDao productoDao = new ProductoDao();



    public void initData(DatabaseConnection connection, String username, String password) {
        this.connection = connection;
        this.username = username;
        this.password = password;
        System.out.println("Conexión establecida: " + connection);
    }


    public void showProducts(){
        List<Producto> pro = productoDao.getAllProductos(connection.getConnection(username, password));
        for (Producto i : pro) {
            i.Mostrar();
        }
    }

}
