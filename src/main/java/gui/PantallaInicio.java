package gui;

import conexion.DatabaseConnection;
import dao.ProductoDao;

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
        productoDao.getAllProductos(connection.getConnection(username, password));
    }

}
