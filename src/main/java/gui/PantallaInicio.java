package gui;

import conexion.DatabaseConnection;
import dao.FacturaDao;
import dao.ProductoDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import modelo.DetalleFactura;
import modelo.Factura;
import modelo.Producto;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public class PantallaInicio {

    // Productos
    @FXML
    private TableView<Producto> tablaProductos;
    @FXML
    private TableColumn<Producto, Integer> columnId;
    @FXML
    private TableColumn<Producto, String> columnTitulo;
    @FXML
    private TableColumn<Producto, String> columnCategoria;
    @FXML
    private TableColumn<Producto, Integer> columnCantidad;
    @FXML
    private TableColumn<Producto, Double> columnPrecio;


    // Factura
    @FXML
    private  TableView<Factura> tablaFacturas;
    @FXML
    private TableColumn<Factura, Integer> facturaId;
    @FXML
    private TableColumn<Factura, Timestamp> facturaFecha;
    @FXML
    private TableColumn<Factura, Double> facturaMonto;
    @FXML
    private TableColumn<Factura, String> facturaTipo;


    private DatabaseConnection connection;
    private String username;
    private String password;
    private final ProductoDao productoDao = new ProductoDao();
    private final FacturaDao facturaDao = new FacturaDao();

    public void initData(DatabaseConnection connection, String username, String password) {
        this.connection = connection;
        this.username = username;
        this.password = password;
        System.out.println("Conexión establecida: " + connection);

        showProducts();
        showFacturas();
    }


    //! <---------- PRODUCTOS ---------->
    public void showProducts(){
        List<Producto> productos = productoDao.getAllProductos(connection.getConnection(username, password));

        ObservableList<Producto> observableProductos = FXCollections.observableArrayList(productos);
        tablaProductos.setItems(observableProductos);

        columnId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        columnTitulo.setCellValueFactory(cellData -> cellData.getValue().tituloProperty());
        columnCategoria.setCellValueFactory(cellData -> cellData.getValue().categoriaProperty());
        columnCantidad.setCellValueFactory(cellData -> cellData.getValue().cantidadProperty().asObject());
        columnPrecio.setCellValueFactory(cellData -> cellData.getValue().precioProperty().asObject());
    }

    public void addProducts(){
        TextInputDialog dialogId = new TextInputDialog();
        dialogId.setTitle("Agregar Producto");
        dialogId.setHeaderText(null);
        dialogId.setContentText("ID:");
        Optional<String> resultId = dialogId.showAndWait();
        int id = resultId.map(Integer::parseInt).orElseThrow(() -> new RuntimeException("ID no ingresado"));

        TextInputDialog dialogTitulo = new TextInputDialog();
        dialogTitulo.setTitle("Agregar Producto");
        dialogTitulo.setHeaderText(null);
        dialogTitulo.setContentText("Título:");
        Optional<String> resultTitulo = dialogTitulo.showAndWait();
        String titulo = resultTitulo.orElse("");

        TextInputDialog dialogCategoria = new TextInputDialog();
        dialogCategoria.setTitle("Agregar Producto");
        dialogCategoria.setHeaderText(null);
        dialogCategoria.setContentText("Categoría:");
        Optional<String> resultCategoria = dialogCategoria.showAndWait();
        String categoria = resultCategoria.orElse("");

        TextInputDialog dialogCantidad = new TextInputDialog();
        dialogCantidad.setTitle("Agregar Producto");
        dialogCantidad.setHeaderText(null);
        dialogCantidad.setContentText("Cantidad:");
        Optional<String> resultCantidad = dialogCantidad.showAndWait();
        int cantidad = resultCantidad.map(Integer::parseInt).orElse(0);

        TextInputDialog dialogPrecio = new TextInputDialog();
        dialogPrecio.setTitle("Agregar Producto");
        dialogPrecio.setHeaderText(null);
        dialogPrecio.setContentText("Precio:");
        Optional<String> resultPrecio = dialogPrecio.showAndWait();
        double precio = resultPrecio.map(Double::parseDouble).orElse(0.0);

        TextInputDialog dialogImagen = new TextInputDialog();
        dialogImagen.setTitle("Agregar Imagen");
        dialogImagen.setHeaderText(null);
        dialogImagen.setContentText("Imagen:");
        Optional<String> resultImagen = dialogImagen.showAndWait();
        String imagen_url = resultImagen.orElse("");

        // Insert into database
        productoDao.insertProduct(connection.getConnection(username, password), id, titulo, categoria, cantidad, precio, imagen_url);

        // Refresh table
        showProducts();
    }

    public void deleteProducts(){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Eliminar producto de la base de datos");
        dialog.setHeaderText("Ingrese los datos del producto");
        dialog.setContentText("ID:");
        Optional<String> resultId = dialog.showAndWait();
        int id = resultId.map(Integer::parseInt).orElseThrow(() -> new RuntimeException("ID no ingresado"));

        productoDao.deleteProduct(connection.getConnection(username, password), id);

        // Refresh table
        showProducts();
    }

    public void updateProducts(){
        TextInputDialog dialogId = new TextInputDialog();
        dialogId.setTitle("Editar Producto");
        dialogId.setHeaderText(null);
        dialogId.setContentText("ID del producto a modificar:");
        Optional<String> resultId = dialogId.showAndWait();
        if (resultId.isEmpty()) return;

        int id = Integer.parseInt(resultId.get());
        Producto producto = null;
        for (Producto p : tablaProductos.getItems()) {
            if (p.getId() == id) {
                producto = p;
                break;
            }
        }

        if (producto == null) {
            System.out.println("No se encontró un producto con ese ID.");
            return;
        }

        TextInputDialog dialogTitulo = new TextInputDialog(producto.getTitulo());
        dialogTitulo.setTitle("Editar Producto");
        dialogTitulo.setHeaderText(null);
        dialogTitulo.setContentText("Nuevo Título:");
        String titulo = dialogTitulo.showAndWait().orElse(producto.getTitulo());

        TextInputDialog dialogCategoria = new TextInputDialog(producto.getCategoria());
        dialogCategoria.setTitle("Editar Producto");
        dialogCategoria.setHeaderText(null);
        dialogCategoria.setContentText("Nueva Categoría:");
        String categoria = dialogCategoria.showAndWait().orElse(producto.getCategoria());

        TextInputDialog dialogCantidad = new TextInputDialog(String.valueOf(producto.getCantidad()));
        dialogCantidad.setTitle("Editar Producto");
        dialogCantidad.setHeaderText(null);
        dialogCantidad.setContentText("Nueva Cantidad:");
        int cantidad = Integer.parseInt(dialogCantidad.showAndWait().orElse(String.valueOf(producto.getCantidad())));

        TextInputDialog dialogPrecio = new TextInputDialog(String.valueOf(producto.getPrecio()));
        dialogPrecio.setTitle("Editar Producto");
        dialogPrecio.setHeaderText(null);
        dialogPrecio.setContentText("Nuevo Precio:");
        double precio = Double.parseDouble(dialogPrecio.showAndWait().orElse(String.valueOf(producto.getPrecio())));

        TextInputDialog dialogImagen = new TextInputDialog(producto.getImagenUrl());
        dialogImagen.setTitle("Editar Producto");
        dialogImagen.setHeaderText(null);
        dialogImagen.setContentText("Nueva URL de Imagen:");
        String imagen_url = dialogImagen.showAndWait().orElse(producto.getImagenUrl());

        productoDao.updateProduct(connection.getConnection(username, password), id, titulo, categoria, cantidad, precio, imagen_url);

        // Refresh table
        showProducts();
    }


    //! <---------- FACTURAS ---------->
    public void showFacturas(){
        List<Factura> facturas = facturaDao.getAllProductos(connection.getConnection(username, password));

        ObservableList<Factura> observableFacturas = FXCollections.observableArrayList(facturas);
        tablaFacturas.setItems(observableFacturas);

        facturaId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        facturaFecha.setCellValueFactory(cellData -> (javafx.beans.value.ObservableValue<Timestamp>) cellData.getValue().fechaProperty());
        facturaMonto.setCellValueFactory(cellData -> cellData.getValue().montoFinalProperty().asObject());
        facturaTipo.setCellValueFactory(cellData -> cellData.getValue().tipoProperty());
    }







}
