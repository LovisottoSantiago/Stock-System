package dao;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import modelo.Producto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        String query = "INSERT INTO Producto (id, titulo, categoria, cantidad, precio) VALUES (?, ?, ?, ?, ?)";
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


    //! <---------- DELETE METHOD ---------->
    public void deleteProduct(Connection conn, int id) {
        String getTitleQuery = "SELECT titulo FROM Producto WHERE id = ?";
        String deleteQuery = "DELETE FROM Producto WHERE id = ?";
        String title = "";

        try (PreparedStatement getTitleStatement = conn.prepareStatement(getTitleQuery)) {
            getTitleStatement.setInt(1, id);
            ResultSet rs = getTitleStatement.executeQuery();
            if (rs.next()) {
                title = rs.getString("titulo");
            } else {
                System.out.println("No se encontró un producto con ese ID.");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación de eliminación");
        alert.setHeaderText("Estás por eliminar el siguiente producto:");
        alert.setContentText("ID: " + id + "\nTítulo: " + title + "\n\n¿Deseas continuar?");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try (PreparedStatement deleteStatement = conn.prepareStatement(deleteQuery)) {
                deleteStatement.setInt(1, id);

                int rowsAffected = deleteStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("El producto fue eliminado correctamente.\n");
                } else {
                    System.out.println("No se encontró un producto con el ID especificado.\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Eliminación cancelada.\n");
        }
    }




}