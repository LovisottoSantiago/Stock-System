package modelo;

import javafx.beans.property.*;

public class DetalleFactura {
    private IntegerProperty id;
    private StringProperty producto;
    private IntegerProperty cantidad;
    private DoubleProperty precioUnitario;
    private DoubleProperty subTotal;

    public DetalleFactura(int id, String producto, int cantidad, double precioUnitario, double subTotal) {
        this.id = new SimpleIntegerProperty(id);
        this.producto = new SimpleStringProperty(producto);
        this.cantidad = new SimpleIntegerProperty(cantidad);
        this.precioUnitario = new SimpleDoubleProperty(precioUnitario);
        this.subTotal = new SimpleDoubleProperty(subTotal);
    }

    // Getters para usar en la tabla de JavaFX
    public IntegerProperty idProperty() { return id; }
    public StringProperty productoProperty() { return producto; }
    public IntegerProperty cantidadProperty() { return cantidad; }
    public DoubleProperty precioUnitarioProperty() { return precioUnitario; }
    public DoubleProperty subTotalProperty() { return subTotal; }

    // Métodos normales para obtener los valores
    public int getId() { return id.get(); }
    public String getProducto() { return producto.get(); }
    public int getCantidad() { return cantidad.get(); }
    public double getPrecioUnitario() { return precioUnitario.get(); }
    public double getSubTotal() { return subTotal.get(); }

    // Setters
    public void setId(int id) { this.id.set(id); }
    public void setProducto(String producto) { this.producto.set(producto); }
    public void setCantidad(int cantidad) { this.cantidad.set(cantidad); }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario.set(precioUnitario); }
    public void setSubTotal(double subTotal) { this.subTotal.set(subTotal); }
}
