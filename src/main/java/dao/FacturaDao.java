package dao;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import modelo.Factura;
import modelo.Producto;
import utils.AlertUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

import static conexion.DatabaseConnection.logger;

public class FacturaDao {

    AlertUtil alertUtil = new AlertUtil();

    public FacturaDao(){

    }


    //! <---------- GET ALL PRODUCTS ---------->
    public List<Factura> getAllProductos(Connection conn) {
        String sql = "SELECT id, fecha, montofinal, tipo, cliente FROM Factura";
        List<Factura> facturas = new ArrayList<>();
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                facturas.add(new Factura(
                        rs.getInt("id"),
                        rs.getTimestamp("fecha"),
                        rs.getDouble("montoFinal"),
                        rs.getString("tipo"),
                        rs.getString("cliente")
                ));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error trying to show items in the database", e);
        }
        return facturas;
    }


    //! <---------- OBTENER MONTO DIARIO ---------->
    public double getMontoDiario(Connection conn, String tipo){
        double montoDiario = 0.0;
        String sql = "SELECT SUM(factura.montoFinal) AS Monto FROM public.factura " +
                "WHERE DATE(factura.fecha) = CURRENT_DATE " +
                "AND factura.tipo = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tipo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    montoDiario = rs.getDouble("Monto");
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error trying to show total", e);
        }

        return montoDiario;
    }


    //! <---------- ELIMINAR TABLA ---------->
    public void deleteProduct(Connection conn, int id){
        String deleteDetalles = "DELETE FROM DetalleFactura WHERE factura_id = ?";
        String deleteFactura = "DELETE FROM Factura WHERE id = ?";

        try(PreparedStatement stmtDetalles = conn.prepareStatement(deleteDetalles);
            PreparedStatement stmtFactura = conn.prepareStatement(deleteFactura)) {

            stmtDetalles.setInt(1, id); // Detalles
            stmtDetalles.executeUpdate();

            stmtFactura.setInt(1, id);
            int rowsAffected = stmtFactura.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Factura eliminada con éxito.");
            } else {
                System.out.println("No se encontró la factura con ID: " + id);
                alertUtil.mostrarAlerta("No se encontró la factura con ID: " + id);
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al eliminar factura", e);
            alertUtil.mostrarAlerta("Error al eliminar la factura");
        }

    }


    //! <---------- NOMBRE CLIENTE ---------->
    public String obtenerNombreClientePorId(Connection conn, int facturaId) {
        String sql = "SELECT c.nombre FROM Cliente c " +
                "JOIN Factura f ON f.cliente_id = c.id " +
                "WHERE f.id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, facturaId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("nombre");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener el nombre del cliente para la factura: " + facturaId, e);
        }
        return "Desconocido";
    }


}
