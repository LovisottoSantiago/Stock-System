package dao;

import modelo.DetalleFactura;
import modelo.Producto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static conexion.DatabaseConnection.logger;

public class DetalleFacturaDao {

    public DetalleFacturaDao() {
    }

    public List<DetalleFactura> getDetallesPorFactura(int facturaId, Connection conn) {
        String sql = "SELECT df.id, p.id AS producto_id, p.titulo, df.cantidad, df.precioUnitario, df.subTotal " +
                "FROM DetalleFactura df " +
                "JOIN Producto p ON df.producto_id = p.id " +
                "WHERE df.factura_id = ?";
        List<DetalleFactura> detalles = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, facturaId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Producto producto = new Producto(
                        rs.getInt("producto_id"),
                        rs.getString("titulo"),
                        null,
                        0,
                        0.0
                );

                DetalleFactura detalle = new DetalleFactura(
                        rs.getInt("id"),
                        producto,
                        rs.getInt("cantidad"),
                        rs.getDouble("precioUnitario"),
                        rs.getDouble("subTotal")
                );

                detalles.add(detalle);
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al intentar obtener los detalles de la factura", e);
        }

        return detalles;
    }
}
