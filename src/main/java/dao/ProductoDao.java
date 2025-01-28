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


    //! <---------- INSERT METHOD ---------->
    public void insertProduct(Connection conn, int id, String titulo, String categoria, int cantidad, double precio) {
        String query = "INSERT INTO Product (id, titulo, categoria, cantidad, precio) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.setString(2, titulo);
            statement.setString(3, categoria);
            statement.setInt(4, cantidad);
            statement.setDouble(5, precio);

            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("Row inserted");
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error inserting row into the database", e);
        }
    }





}