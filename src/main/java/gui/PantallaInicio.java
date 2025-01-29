package gui;

import conexion.DatabaseConnection;
import dao.ProductoDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import modelo.Producto;

import java.util.List;
import java.util.Optional;

public class PantallaInicio {

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

    private DatabaseConnection connection;
    private String username;
    private String password;
    private final ProductoDao productoDao = new ProductoDao();

    public void initData(DatabaseConnection connection, String username, String password) {
        this.connection = connection;
        this.username = username;
        this.password = password;
        System.out.println("Conexión establecida: " + connection);

        showProducts();
    }

    // Show products in the Table
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
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nuevo Producto");
        dialog.setHeaderText("Ingrese los datos del producto");

        dialog.setContentText("ID:");
        Optional<String> resultId = dialog.showAndWait();
        int id = resultId.map(Integer::parseInt).orElseThrow(() -> new RuntimeException("ID no ingresado"));

        dialog.setContentText("Título:");
        Optional<String> resultTitulo = dialog.showAndWait();
        String titulo = resultTitulo.orElse("");

        dialog.setContentText("Categoría:");
        Optional<String> resultCategoria = dialog.showAndWait();
        String categoria = resultCategoria.orElse("");

        dialog.setContentText("Cantidad:");
        Optional<String> resultCantidad = dialog.showAndWait();
        int cantidad = resultCantidad.map(Integer::parseInt).orElse(0);

        dialog.setContentText("Precio:");
        Optional<String> resultPrecio = dialog.showAndWait();
        double precio = resultPrecio.map(Double::parseDouble).orElse(0.0);

        // Insert into database
        productoDao.insertProduct(connection.getConnection(username, password), id, titulo, categoria, cantidad, precio);

        // Refresh table
        showProducts();
    }


}
