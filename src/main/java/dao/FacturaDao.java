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




}
