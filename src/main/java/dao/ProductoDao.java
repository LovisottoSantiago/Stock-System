package dao;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import modelo.Producto;
import utils.AlertUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

import static conexion.DatabaseConnection.logger;

public class ProductoDao {

    private AlertUtil alertUtil = new AlertUtil();

    public ProductoDao() {

    }


    //! <---------- GET ALL PRODUCTS ---------->
    public List<Producto> getAllProductos(Connection conn) {
        String sql = "SELECT id, titulo, categoria, cantidad, precio, imagen_url FROM Producto";
        List<Producto> productos = new ArrayList<>();
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                productos.add(new Producto(
                        rs.getLong("id"),
                        rs.getString("titulo"),
                        rs.getString("categoria"),
                        rs.getInt("cantidad"),
                        rs.getDouble("precio"),
                        rs.getString("imagen_url")
                ));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error trying to show items in the database", e);
        }
        return productos;
    }


    //! <---------- INSERT METHOD ---------->
    public void insertProduct(Connection conn, long id, String titulo, String categoria, int cantidad, double precio, String imagen_url) {
        String query = "INSERT INTO Producto (id, titulo, categoria, cantidad, precio, imagen_url) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setLong(1, id);
            statement.setString(2, titulo);
            statement.setString(3, categoria);
            statement.setInt(4, cantidad);
            statement.setDouble(5, precio);
            statement.setString(6, imagen_url);

            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("Row inserted");
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error inserting row into the database", e);
            alertUtil.mostrarAlerta("Error insertando productos");
        }
    }


    //! <---------- DELETE METHOD ---------->
    public void deleteProduct(Connection conn, long id) {
        String getTitleQuery = "SELECT titulo FROM Producto WHERE id = ?";
        String deleteQuery = "DELETE FROM Producto WHERE id = ?";
        String title = "";

        try (PreparedStatement getTitleStatement = conn.prepareStatement(getTitleQuery)) {
            getTitleStatement.setLong(1, id);
            ResultSet rs = getTitleStatement.executeQuery();
            if (rs.next()) {
                title = rs.getString("titulo");
            } else {
                System.out.println("No se encontró un producto con ese ID.");
                alertUtil.mostrarAlerta("No se encontró un producto con ese ID.");
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
                deleteStatement.setLong(1, id);

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


    //! <---------- UPDATE METHOD ---------->
    public void updateProduct(Connection conn, long id, String titulo, String categoria, int cantidad, double precio, String imagen_url) {
        String sql = "UPDATE Producto SET titulo = ?, categoria = ?, cantidad = ?, precio = ?, imagen_url = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, titulo);
            stmt.setString(2, categoria);
            stmt.setInt(3, cantidad);
            stmt.setDouble(4, precio);
            stmt.setString(5, imagen_url);
            stmt.setLong(6, id);

            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Producto actualizado correctamente.");
            } else {
                System.out.println("No se encontró un producto con el ID especificado.");

            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al actualizar el producto", e);
            alertUtil.mostrarAlerta("Error al actualizar el producto.");
        }
    }


    public String getImageUrlById(Connection conn, long id) {
        String sql = "SELECT imagen_url FROM Producto WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("imagen_url");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener la URL de la imagen", e);
        }
        return null;
    }


    public boolean productoExiste(Connection conn, long id){
        String query = "SELECT titulo FROM public.producto WHERE ID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return  true;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;

    }
}