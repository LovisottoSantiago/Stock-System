package modelo;

import javafx.beans.property.*;

public class Producto {
    private IntegerProperty id;
    private StringProperty titulo;
    private StringProperty categoria;
    private IntegerProperty cantidad;
    private DoubleProperty precio;
    private String imagen_url; // not include in the table

    public Producto(int id, String titulo, String categoria, int cantidad, double precio, String imagen_url) {
        this.id = new SimpleIntegerProperty(id);
        this.titulo = new SimpleStringProperty(titulo);
        this.categoria = new SimpleStringProperty(categoria);
        this.cantidad = new SimpleIntegerProperty(cantidad);
        this.precio = new SimpleDoubleProperty(precio);
        this.imagen_url = imagen_url;
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

    public String getImagenUrl() { return imagen_url; }
}
