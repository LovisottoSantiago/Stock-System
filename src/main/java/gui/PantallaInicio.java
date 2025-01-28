package gui;

import conexion.DatabaseConnection;
import dao.ProductoDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import modelo.Producto;

import java.util.List;

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
    private ProductoDao productoDao = new ProductoDao();

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


}
