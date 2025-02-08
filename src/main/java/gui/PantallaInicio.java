package gui;

import conexion.DatabaseConnection;
import dao.DetalleFacturaDao;
import dao.FacturaDao;
import dao.ProductoDao;
import javafx.collections.FXCollections;
import java.text.DecimalFormat;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import modelo.DetalleFactura;
import modelo.Factura;
import javafx.util.Callback;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.text.SimpleDateFormat;
import modelo.Producto;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.Optional;
import java.sql.Timestamp;
import java.util.Comparator;
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
    @FXML
    private TableColumn<Factura, String> clienteTipo;


    // DetallesFactura
    @FXML
    private  TableView<DetalleFactura> tablaCarrito;
    @FXML
    private TableColumn<DetalleFactura, Integer> carritoId;
    @FXML
    private TableColumn<DetalleFactura, String> carritoProducto;
    @FXML
    private TableColumn<DetalleFactura, Integer> carritoCantidad;
    @FXML
    private TableColumn<DetalleFactura, Double> carritoPrecio;
    @FXML
    private TableColumn<DetalleFactura, Double> carritoMonto;
    @FXML
    private Label totalLabel; //para el carrito
    @FXML
    private Label labelMontoEf;
    @FXML
    private Label labelMontoTr;
    @FXML
    private Label labelFacturaDiaria;

    // Extra



    private DatabaseConnection connection;
    private String username;
    private String password;
    private final ProductoDao productoDao = new ProductoDao();
    private final FacturaDao facturaDao = new FacturaDao();
    private final DetalleFacturaDao detalleFacturaDao = new DetalleFacturaDao();

    public void initData(DatabaseConnection connection, String username, String password) {
        this.connection = connection;
        this.username = username;
        this.password = password;
        System.out.println("Conexión establecida: " + connection);

        actualizarTodo();
    }


    //! <---------- PRODUCTOS ---------->
    public void showProducts(){
        List<Producto> productos = productoDao.getAllProductos(connection.getConnection(username, password));

        ObservableList<Producto> observableProductos = FXCollections.observableArrayList(productos);
        observableProductos.sort(Comparator.comparing(Producto::getId));
        tablaProductos.setItems(observableProductos);


        columnId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        columnTitulo.setCellValueFactory(cellData -> cellData.getValue().tituloProperty());
        columnCategoria.setCellValueFactory(cellData -> cellData.getValue().categoriaProperty());
        columnCantidad.setCellValueFactory(cellData -> cellData.getValue().cantidadProperty().asObject());
        columnPrecio.setCellValueFactory(cellData -> cellData.getValue().precioProperty().asObject());

        // Aplicar formato a la columna de precios
        DecimalFormat decimalFormat = new DecimalFormat("$#,###");
        columnPrecio.setCellFactory(col -> new TableCell<Producto, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : decimalFormat.format(item));
            }
        });
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
        observableFacturas.sort((df1, df2) -> Integer.compare(df2.getId(), df1.getId()));
        tablaFacturas.setItems(observableFacturas);

        facturaId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());

        // Usar un CellFactory para formatear la fecha
        facturaFecha.setCellValueFactory(cellData -> cellData.getValue().fechaProperty());
        facturaFecha.setCellFactory(new Callback<TableColumn<Factura, Timestamp>, TableCell<Factura, Timestamp>>() {
            @Override
            public TableCell<Factura, Timestamp> call(TableColumn<Factura, Timestamp> param) {
                return new TableCell<Factura, Timestamp>() {
                    @Override
                    protected void updateItem(Timestamp item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy | HH:mm");
                            setText(sdf.format(item));
                        } else {
                            setText(null);
                        }
                    }
                };
            }
        });

        facturaMonto.setCellValueFactory(cellData -> cellData.getValue().montoFinalProperty().asObject());
        facturaTipo.setCellValueFactory(cellData -> cellData.getValue().tipoProperty());
        clienteTipo.setCellValueFactory(cellData -> cellData.getValue().clienteProperty());

        // Aplicar formato a la columna de precios
        DecimalFormat decimalFormat = new DecimalFormat("$#,###");
        facturaMonto.setCellFactory(col -> new TableCell<Factura, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : decimalFormat.format(item));
            }
        });
    }

    public void obtenerMontoDiario() {
        double montoEfectivo = facturaDao.getMontoDiario(connection.getConnection(username, password), "Efectivo");
        double montoTransferencia = facturaDao.getMontoDiario(connection.getConnection(username, password), "Transferencia");
        double montoDiarioTotal = montoEfectivo + montoTransferencia;

        DecimalFormat decimalFormat = new DecimalFormat("$#,###");

        labelMontoEf.setText("Total diario facturado efectivo: " + decimalFormat.format(montoEfectivo));
        labelMontoTr.setText("Total diario facturado transferencia: " + decimalFormat.format(montoTransferencia));
        labelFacturaDiaria.setText(decimalFormat.format(montoDiarioTotal));
    }

    public void eliminarFactura(){
        TextInputDialog dialogId = new TextInputDialog();
        dialogId.setTitle("Eliminar factura");
        dialogId.setHeaderText(null);
        dialogId.setContentText("ID:");
        Optional<String> resultId = dialogId.showAndWait();
        int id = resultId.map(Integer::parseInt).orElseThrow(() -> new RuntimeException("ID no ingresado"));
        facturaDao.deleteProduct(connection.getConnection(username, password), id);
        actualizarTodo();
    }


    //! <---------- DETALLE FACTURAS ---------->
    public void showCarrito(){
        List<DetalleFactura> detalleFacturas = detalleFacturaDao.getCarritoActual(connection.getConnection(username, password));

        ObservableList<DetalleFactura> observableDetalleFacturas = FXCollections.observableArrayList(detalleFacturas);
        tablaCarrito.setItems(observableDetalleFacturas);

        carritoId.setCellValueFactory(cellData -> cellData.getValue().productoIdProperty().asObject());
        carritoProducto.setCellValueFactory(cellData -> cellData.getValue().productoProperty());
        carritoCantidad.setCellValueFactory(cellData -> cellData.getValue().cantidadProperty().asObject());
        carritoPrecio.setCellValueFactory(cellData -> cellData.getValue().precioUnitarioProperty().asObject());
        carritoMonto.setCellValueFactory(cellData -> cellData.getValue().subTotalProperty().asObject());

        // Aplicar formato a la columna de precios
        DecimalFormat decimalFormat = new DecimalFormat("$#,###");
        carritoPrecio.setCellFactory(col -> new TableCell<DetalleFactura, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : decimalFormat.format(item));
            }
        });
        carritoMonto.setCellFactory(col -> new TableCell<DetalleFactura, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : decimalFormat.format(item));
            }
        });
    }

    public void addProductsCarrito(){
        TextInputDialog dialogId = new TextInputDialog();
        dialogId.setTitle("Agregar Producto");
        dialogId.setHeaderText(null);
        dialogId.setContentText("ID:");
        Optional<String> resultId = dialogId.showAndWait();
        int id = resultId.map(Integer::parseInt).orElseThrow(() -> new RuntimeException("ID no ingresado"));

        boolean confirmacion = mostrarImagenProducto(id);
        if (!confirmacion) return;

        TextInputDialog dialogCantidad = new TextInputDialog();
        dialogCantidad.setTitle("Agregar Producto");
        dialogCantidad.setHeaderText(null);
        dialogCantidad.setContentText("Cantidad:");
        Optional<String> resultCantidad = dialogCantidad.showAndWait();
        int cantidad = resultCantidad.map(Integer::parseInt).orElse(0);

        detalleFacturaDao.addProductoCarrito(connection.getConnection(username, password), id, cantidad );
        double valor = detalleFacturaDao.obtenerMontoTotalCarrito(connection.getConnection(username, password));
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        String valorFormateado = decimalFormat.format(valor);
        totalLabel.setText("Total: $" + valorFormateado);
        // Refresh table
        showCarrito();
    }

    public void deleteProductsCarrito(){
        TextInputDialog dialogId = new TextInputDialog();
        dialogId.setTitle("Eliminar Producto");
        dialogId.setHeaderText(null);
        dialogId.setContentText("ID:");
        Optional<String> resultId = dialogId.showAndWait();
        int id = resultId.map(Integer::parseInt).orElseThrow(() -> new RuntimeException("ID no ingresado"));

        detalleFacturaDao.deleteProductoCarrito(connection.getConnection(username, password), id);
        showCarrito();

        double total = detalleFacturaDao.obtenerMontoTotalCarrito(connection.getConnection(username, password));
        DecimalFormat decimalFormat = new DecimalFormat("$#,###");
        if (total == 0) {
            totalLabel.setText("Total: $0");
        } else {
            totalLabel.setText("Total: " + decimalFormat.format(total));
        }
    }

    public void pagoTransferencia(){
        String cliente = asignarCliente();
        detalleFacturaDao.generarFactura(connection.getConnection(username, password), "Transferencia", cliente);
        actualizarTodo();
    }

    public void pagoEfectivo(){
        if (!darVuelto()){
            return;
        }

        String cliente = asignarCliente();
        detalleFacturaDao.generarFactura(connection.getConnection(username, password), "Efectivo", cliente);
        actualizarTodo();
    }

    public String asignarCliente(){
        TextInputDialog dialogId = new TextInputDialog();
        dialogId.setTitle("Asignar Cliente");
        dialogId.setHeaderText(null);
        dialogId.setContentText("Nombre:");
        Optional<String> resultId = dialogId.showAndWait();
        String cliente = resultId.orElseThrow(() -> new RuntimeException("Cliente no ingresado"));
        return cliente;
    }

    public void vaciarCarrito(){
        detalleFacturaDao.vaciarCarrito(connection.getConnection(username, password));
        actualizarTodo();
    }

    private boolean mostrarImagenProducto(int productId) {
        String imageUrl = productoDao.getImageUrlById(connection.getConnection(username, password), productId);

        Stage imageStage = new Stage();
        imageStage.initModality(Modality.APPLICATION_MODAL);
        imageStage.setTitle("Confirmar Producto");
        imageStage.setWidth(350);

        StackPane imageContainer = new StackPane();
        imageContainer.setPrefSize(300, 320);

        if (imageUrl == null || imageUrl.isEmpty()) {
            Rectangle placeholder = new Rectangle(300, 300, Color.LIGHTGRAY);
            Text noImageText = new Text("No hay imagen disponible");
            noImageText.setFill(Color.BLACK);
            noImageText.setFont(new Font(18));
            imageContainer.getChildren().addAll(placeholder, noImageText);
        } else {
            try {
                ImageView imageView = new ImageView(new Image(imageUrl, true));
                imageView.setPreserveRatio(true);
                imageView.setFitWidth(300);
                imageView.setFitHeight(300);
                imageContainer.getChildren().add(imageView);
            } catch (Exception e) {

                Rectangle placeholder = new Rectangle(300, 300, Color.LIGHTGRAY);
                Text noInternetText = new Text("No hay internet");
                noInternetText.setFill(Color.BLACK);
                noInternetText.setFont(new Font(18));
                imageContainer.getChildren().addAll(placeholder, noInternetText);
            }
        }

        Button acceptButton = new Button("Aceptar");
        Button cancelButton = new Button("Cancelar");

        final boolean[] accepted = {false};

        acceptButton.setOnAction(e -> {
            accepted[0] = true;
            imageStage.close();
        });

        cancelButton.setOnAction(e -> {
            accepted[0] = false;
            imageStage.close();
        });

        HBox buttonBox = new HBox(10, acceptButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox root = new VBox(5, imageContainer, buttonBox);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10, 10, 15, 10));

        Scene scene = new Scene(root);
        imageStage.setScene(scene);
        imageStage.showAndWait();

        return accepted[0];
    }

    public void actualizarTodo(){
        showProducts();
        showFacturas();
        showCarrito();

        double totalCarrito = detalleFacturaDao.obtenerMontoTotalCarrito(connection.getConnection(username, password));
        if (totalCarrito > 0) {
            DecimalFormat decimalFormat = new DecimalFormat("$#,###");
            String valorFormateado = decimalFormat.format(totalCarrito);
            totalLabel.setText("Total: " + valorFormateado);
        } else {
            totalLabel.setText("Total: $0");
        }

        obtenerMontoDiario();
    }

    public void mostrarFacturaPorID() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Cargar Factura");
        dialog.setHeaderText(null);
        dialog.setContentText("ID de la factura:");

        Optional<String> resultId = dialog.showAndWait();
        if (!resultId.isPresent() || resultId.get().trim().isEmpty()) {
            return;
        }

        int facturaId;
        try {
            facturaId = Integer.parseInt(resultId.get().trim());
        } catch (NumberFormatException e) {
            return;
        }

        List<DetalleFactura> detalles = detalleFacturaDao.getDetallesFacturaPorId(connection.getConnection(username, password), facturaId);

        if (detalles.isEmpty()) {
            return;
        }

        // Clonar los detalles al carrito actual (sin asignar factura_id)
        for (DetalleFactura detalle : detalles) {
            detalleFacturaDao.addProductosViejos(
                    connection.getConnection(username, password),
                    detalle.getProductoId(),
                    detalle.getCantidad()
            );
        }

        actualizarTodo();
    }



    //! <---------- FUNCIONES EXTRA ---------->
    public boolean darVuelto(){
        double totalCarrito = detalleFacturaDao.obtenerMontoTotalCarrito(connection.getConnection(username, password));
        DecimalFormat decimalFormat = new DecimalFormat("$#,###");
        String totalCarritoStr = decimalFormat.format(totalCarrito);
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Dar vuelto");
        dialog.setHeaderText("Total: " + totalCarritoStr);
        dialog.setContentText("Pagó con: ");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) {
            // El usuario canceló la operación
            return false;
        }

        try {
            double pago = Double.parseDouble(result.get());
            // Calcula el vuelto
            double vuelto = pago - totalCarrito;

            if(vuelto < 0) {
                // Si el pago es insuficiente, se muestra un error
                Alert alertError = new Alert(Alert.AlertType.ERROR);
                alertError.setWidth(300);
                alertError.setHeight(300);
                alertError.setTitle("Pago insuficiente");
                alertError.setHeaderText(null);
                alertError.setContentText("El pago ingresado es menor al total de la compra.");
                alertError.showAndWait();
                return false;
            } else {
                // Si el pago es suficiente, se muestra el cambio al usuario
                Alert alertInfo = new Alert(Alert.AlertType.INFORMATION);
                alertInfo.setWidth(300);
                alertInfo.setHeight(300);
                alertInfo.setTitle("Cambio");
                alertInfo.setHeaderText(null);
                String vueltoStr = decimalFormat.format(vuelto);
                alertInfo.setContentText("El vuelto es: " + vueltoStr);
                alertInfo.showAndWait();
                return true;
            }
        } catch(NumberFormatException e) {
            // En caso de que el usuario no ingrese un número válido
            Alert alertError = new Alert(Alert.AlertType.ERROR);
            alertError.setWidth(300);
            alertError.setHeight(300);
            alertError.setTitle("Número inválido");
            alertError.setHeaderText(null);
            alertError.setContentText("Por favor, ingresa un número válido para el pago.");
            alertError.showAndWait();
            return false;
        }
    }


}
