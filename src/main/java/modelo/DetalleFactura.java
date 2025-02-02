package modelo;

import javafx.beans.property.*;

public class DetalleFactura {

    private IntegerProperty id;
    private StringProperty producto;
    private IntegerProperty cantidad;
    private DoubleProperty precioUnitario;
    private DoubleProperty subTotal;

    public DetalleFactura (int id, String producto, int cantidad, double precioUnitario, double subTotal) {
        this.id = new SimpleIntegerProperty(id);
        this.producto = new SimpleStringProperty(producto);
        this.cantidad = new SimpleIntegerProperty(cantidad);
        this.precioUnitario = new SimpleDoubleProperty(precioUnitario);
        this.subTotal = new SimpleDoubleProperty(subTotal);
    }


}
