package gui;

import conexion.DatabaseConnection;

public class PantallaInicio {

    private DatabaseConnection connection;

    public void initData(DatabaseConnection connection) {
        this.connection = connection;
        System.out.println("Conexión establecida: " + connection);
    }



}
