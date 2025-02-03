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

    public List<DetalleFactura> getDetallesFactura(int facturaId, Connection conn) {
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
                        0.0,
                        null
                );

                DetalleFactura detalle = new DetalleFactura(
                        rs.getInt("producto_id"),
                        rs.getString("producto"),
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


    public List<DetalleFactura> getCarritoActual(Connection conn) {
        String sql = "SELECT df.id, p.id AS producto_id, p.titulo, df.cantidad, df.precioUnitario, df.subTotal " +
                "FROM DetalleFactura df " +
                "JOIN Producto p ON df.producto_id = p.id " +
                "WHERE df.factura_id IS NULL";
        List<DetalleFactura> carrito = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                DetalleFactura detalle = new DetalleFactura(
                        rs.getInt("producto_id"),
                        rs.getString("titulo"),
                        rs.getInt("cantidad"),
                        rs.getDouble("precioUnitario"),
                        rs.getDouble("subTotal")
                );
                carrito.add(detalle);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener el carrito de compras", e);
        }

        return carrito;
    }


    public void addProductoCarrito(Connection conn, int productoId, int cantidad) {
        String obtenerPrecioSql = "SELECT precio FROM Producto WHERE id = ?";
        String insertarSql = "INSERT INTO DetalleFactura (producto_id, cantidad, precioUnitario, subTotal, factura_id) " +
                "VALUES (?, ?, ?, ?, NULL)";

        try (PreparedStatement obtenerPrecioStmt = conn.prepareStatement(obtenerPrecioSql)) {
            obtenerPrecioStmt.setInt(1, productoId);
            ResultSet rs = obtenerPrecioStmt.executeQuery();

            if (rs.next()) {
                double precioUnitario = rs.getDouble("precio");
                double subTotal = cantidad * precioUnitario;

                try (PreparedStatement insertarStmt = conn.prepareStatement(insertarSql)) {
                    insertarStmt.setInt(1, productoId);
                    insertarStmt.setInt(2, cantidad);
                    insertarStmt.setDouble(3, precioUnitario);
                    insertarStmt.setDouble(4, subTotal);
                    insertarStmt.executeUpdate();
                }
            } else {
                System.out.println("No se encontró el producto con ID: " + productoId);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al agregar producto al carrito", e);
        }
    }



    public void deleteProductoCarrito(Connection conn, int detalleId) {
        String sql = "DELETE FROM DetalleFactura WHERE producto_id = ? AND factura_id IS NULL";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, detalleId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al eliminar producto del carrito", e);
        }
    }


    public void vaciarCarrito(Connection conn) {
        String sql = "DELETE FROM DetalleFactura WHERE factura_id IS NULL";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al vaciar el carrito", e);
        }
    }


    // Objetivo:
    // 1. Crear una nueva factura en Factura.
    // 2. Asignar el factura_id generado a todos los productos del carrito.

    public void generarFactura(Connection conn, String tipoPago){
        String insertFacturaSQL = "INSERT INTO Factura (fecha, montoFinal, tipoPago) VALUES (NOW(), ?, ?)";
        String updateDetallesSQL = "UPDATE DetalleFactura SET factura_id = ? WHERE factura_id IS NULL";

        try (PreparedStatement insertFacturaStmt = conn.prepareStatement(insertFacturaSQL, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement updateDetallesStmt = conn.prepareStatement(updateDetallesSQL)) {

            double total = 0.0;
            for (DetalleFactura detalle : getCarritoActual(conn)) {
                total += detalle.getSubTotal();
            }

            insertFacturaStmt.setDouble(1, total);
            insertFacturaStmt.setString(2, tipoPago);
            insertFacturaStmt.executeUpdate();

            ResultSet generatedKeys = insertFacturaStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int facturaId = generatedKeys.getInt(1);

                updateDetallesStmt.setInt(1, facturaId);
                updateDetallesStmt.executeUpdate();
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al generar la factura", e);
        }
    }

}
