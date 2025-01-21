package modelo;

public class Producto {

    private int id;
    private String titulo;
    private String categoria;
    private int cantidad;
    private double precio;

    public Producto(int id, String titulo, String categoria, int cantidad, double precio){
        this.id = id;
        this.titulo = titulo;
        this.categoria = categoria;
        this.cantidad = cantidad;
        this.precio = precio;
    }

    public void Mostrar(){
        System.out.println("ID: " + id);
        System.out.println("Titulo: " + titulo);
        System.out.println("Precio: " + precio);
    }

}
