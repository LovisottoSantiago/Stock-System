package dao;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import modelo.Factura;
import modelo.Producto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

import static conexion.DatabaseConnection.logger;

public class FacturaDao {

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

}
