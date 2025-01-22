package modelo;

import javafx.beans.property.*;

public class Producto {
    private IntegerProperty id;
    private StringProperty titulo;
    private StringProperty categoria;
    private IntegerProperty cantidad;
    private DoubleProperty precio;

    public Producto(int id, String titulo, String categoria, int cantidad, double precio) {
        this.id = new SimpleIntegerProperty(id);
        this.titulo = new SimpleStringProperty(titulo);
        this.categoria = new SimpleStringProperty(categoria);
        this.cantidad = new SimpleIntegerProperty(cantidad);
        this.precio = new SimpleDoubleProperty(precio);
    }

    public IntegerProperty idProperty() { return id; }
    public StringProperty tituloProperty() { return titulo; }
    public StringProperty categoriaProperty() { return categoria; }
    public IntegerProperty cantidadProperty() { return cantidad; }
    public DoubleProperty precioProperty() { return precio; }

    public int getId() { return id.get(); }
    public String getTitulo() { return titulo.get(); }
    public String getCategoria() { return categoria.get(); }
    public int getCantidad() { return cantidad.get(); }
    public double getPrecio() { return precio.get(); }
}
