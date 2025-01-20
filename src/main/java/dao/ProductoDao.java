package dao;

import modelo.Producto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static conexion.DatabaseConnection.logger;

public class ProductoDao {

    public ProductoDao() {

    }

    public List<Producto> getAllProductos(Connection conn) {
        String sql = "SELECT * FROM Producto";
        List<Producto> productos = new ArrayList<>();
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                productos.add(new Producto(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("categoria"),
                        rs.getInt("cantidad"),
                        rs.getDouble("precio")
                ));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error trying to show items in the database");
        }
        return productos;
    }


}
