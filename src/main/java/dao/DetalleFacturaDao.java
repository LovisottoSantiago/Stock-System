package dao;

import modelo.DetalleFactura;
import modelo.Producto;
import utils.AlertUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import static conexion.DatabaseConnection.logger;

public class DetalleFacturaDao {

    private AlertUtil alertUtil = new AlertUtil();

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
                        rs.getInt("id"),
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
                        rs.getInt("id"),
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
        String obtenerPrecioSql = "SELECT precio, cantidad FROM Producto WHERE id = ?";
        String insertarSql = "INSERT INTO DetalleFactura (producto_id, cantidad, precioUnitario, subTotal, factura_id) VALUES (?, ?, ?, ?, NULL)";

        try (PreparedStatement obtenerPrecioStmt = conn.prepareStatement(obtenerPrecioSql)) {
            obtenerPrecioStmt.setInt(1, productoId);
            ResultSet rs = obtenerPrecioStmt.executeQuery();

            if (rs.next()) {
                int cantidadDisponible = rs.getInt("cantidad");
                if (cantidadDisponible >= cantidad) {
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
                    System.out.println("No hay suficiente stock disponible para el producto ID: " + productoId);
                    alertUtil.mostrarAlerta("No hay stock suficiente");

                }
            } else {
                System.out.println("No se encontró el producto con ID: " + productoId);
                alertUtil.mostrarAlerta("No se encontro al producto con ID" + productoId);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al agregar producto al carrito", e);
            alertUtil.mostrarAlerta("Error");
        }
    }


    public void deleteProductoCarrito(Connection conn, int detalleId) {
        String sql = "DELETE FROM DetalleFactura WHERE producto_id = ? AND factura_id IS NULL";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, detalleId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al eliminar producto del carrito", e);
            alertUtil.mostrarAlerta("Error al eliminar producto del carrito");
        }
    }


    public void vaciarCarrito(Connection conn) {
        String sql = "DELETE FROM DetalleFactura WHERE factura_id IS NULL";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al vaciar el carrito", e);
            alertUtil.mostrarAlerta("Error al vaciar el carrito");
        }
    }


    public void generarFactura(Connection conn, String tipoPago, String cliente){
        String insertFacturaSQL = "INSERT INTO Factura (fecha, montoFinal, tipo, cliente) VALUES (NOW(), ?, ?, ?)";
        String updateDetallesSQL = "UPDATE DetalleFactura SET factura_id = ? WHERE factura_id IS NULL";
        String updateStockSQL = "UPDATE Producto SET cantidad = cantidad - ? WHERE id = ?";

        try (PreparedStatement insertFacturaStmt = conn.prepareStatement(insertFacturaSQL, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement updateDetallesStmt = conn.prepareStatement(updateDetallesSQL);
             PreparedStatement updateStockStmt = conn.prepareStatement(updateStockSQL)) {

            double total = 0.0;
            List<DetalleFactura> carrito = getCarritoActual(conn);
            for (DetalleFactura detalle : carrito) {
                total += detalle.getSubTotal();
            }

            insertFacturaStmt.setDouble(1, total);
            insertFacturaStmt.setString(2, tipoPago);
            insertFacturaStmt.setString(3, cliente);
            insertFacturaStmt.executeUpdate();

            ResultSet generatedKeys = insertFacturaStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int facturaId = generatedKeys.getInt(1);

                updateDetallesStmt.setInt(1, facturaId);
                updateDetallesStmt.executeUpdate();

                for (DetalleFactura detalle : carrito) {
                    updateStockStmt.setInt(1, detalle.getCantidad());
                    updateStockStmt.setInt(2, detalle.getProductoId());
                    updateStockStmt.executeUpdate();
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al generar la factura", e);
            alertUtil.mostrarAlerta("Error al generar la factura");
        }
    }


    public double obtenerMontoTotalCarrito(Connection conn) {
        String sql = "SELECT SUM(df.subTotal) AS montoTotal " +
                "FROM DetalleFactura df " +
                "WHERE df.factura_id IS NULL"; // Solo los productos del carrito (sin factura asignada)

        double montoTotal = 0.0;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                montoTotal = rs.getDouble("montoTotal");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener el monto total del carrito", e);
            alertUtil.mostrarAlerta("Error al obtener el monto total del carrito");
        }

        return montoTotal;
    }


    // Mostrar una factura en el carrito
    public List<DetalleFactura> getDetallesFacturaPorId(Connection conn, int facturaId) {
        String sql = "SELECT df.id, p.id AS producto_id, p.titulo, df.cantidad, df.precioUnitario, df.subTotal " +
                "FROM DetalleFactura df " +
                "JOIN Producto p ON df.producto_id = p.id " +
                "WHERE df.factura_id = ?";
        List<DetalleFactura> detalles = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, facturaId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                DetalleFactura detalle = new DetalleFactura(
                        rs.getInt("id"),
                        rs.getInt("producto_id"),
                        rs.getString("titulo"),
                        rs.getInt("cantidad"),
                        rs.getDouble("precioUnitario"),
                        rs.getDouble("subTotal")
                );
                detalles.add(detalle);
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener los detalles de la factura con ID: " + facturaId, e);
            alertUtil.mostrarAlerta("Error al obtener los detalles de la factura.");
        }

        return detalles;
    }


}
