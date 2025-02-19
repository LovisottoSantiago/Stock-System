package gui;

import conexion.DatabaseConnection;
import dao.DetalleFacturaDao;
import dao.FacturaDao;
import dao.ProductoDao;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import java.text.DecimalFormat;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import modelo.DetalleFactura;
import modelo.Factura;
import javafx.util.Callback;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.text.SimpleDateFormat;
import modelo.Producto;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utils.AlertUtil;

import java.util.*;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.function.Function;

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
    @FXML
    private TextField searchField = new TextField();

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

    // Extra
    private Map<Integer, Image> imagenesProductos = new HashMap<>();
    private static final String CONTRASENA_CORRECTA = "machupichu";
    private AlertUtil alertUtil = new AlertUtil();
    @FXML
    public Button agregarBtn;


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
        precargarImagenes();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterProducts(newValue);  // Llama al método cada vez que el usuario escribe algo
        });

        actualizarTodo();

        // Configurar copia de ID para cada tabla
        configurarCopiaId(tablaProductos, Producto::getId);
        configurarCopiaId(tablaFacturas, Factura::getId);
        configurarCopiaId(tablaCarrito, DetalleFactura::getProductoId);

        // Event listener para el agregar productos
        Platform.runLater(() -> {
            agregarBtn.getScene().setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.DIGIT0 || event.getCode() == KeyCode.NUMPAD0) {
                    addProductsCarrito();
                    System.out.println("Prueba");
                }
            });
        });

    }


    //! <---------- PRODUCTOS ---------->
    public void showProducts(){
        List<Producto> productos = productoDao.getAllProductos(connection.getConnection(username, password));

        ObservableList<Producto> observableProductos = FXCollections.observableArrayList(productos);
        observableProductos.sort(Comparator.comparing(Producto::getCategoria)
                .thenComparing(Producto::getTitulo));
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
        Stage stage = new Stage();
        stage.setTitle("Agregar Producto");
        stage.setMinWidth(400);
        stage.setMinHeight(299);
        stage.setMaxHeight(300);
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        Label lblId = new Label("ID:");
        TextField txtId = new TextField();
        txtId.setMinWidth(300);
        Label lblTitulo = new Label("Título:");
        TextField txtTitulo = new TextField();
        Label lblCategoria = new Label("Categoría:");
        TextField txtCategoria = new TextField();
        Label lblCantidad = new Label("Cantidad:");
        TextField txtCantidad = new TextField();
        Label lblPrecio = new Label("Precio:");
        TextField txtPrecio = new TextField();
        Label lblImagen = new Label("Imagen:");
        TextField txtImagen = new TextField();

        Button btnCancelar = new Button("Cancelar");
        Button btnGuardar = new Button("Guardar");
        HBox buttonBox = new HBox(10, btnCancelar, btnGuardar);
        buttonBox.setAlignment(Pos.CENTER);

        grid.add(lblId, 0, 0);
        grid.add(txtId, 1, 0);
        grid.add(lblTitulo, 0, 1);
        grid.add(txtTitulo, 1, 1);
        grid.add(lblCategoria, 0, 2);
        grid.add(txtCategoria, 1, 2);
        grid.add(lblCantidad, 0, 3);
        grid.add(txtCantidad, 1, 3);
        grid.add(lblPrecio, 0, 4);
        grid.add(txtPrecio, 1, 4);
        grid.add(lblImagen, 0, 5);
        grid.add(txtImagen, 1, 5);
        grid.add(buttonBox, 0, 6, 2, 1);

        btnGuardar.setOnAction(e -> {
            try {
                int id = Integer.parseInt(txtId.getText());
                if (productoDao.productoExiste(connection.getConnection(username, password), id)) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("ID Existente");
                    alert.setHeaderText(null);
                    alert.setContentText("El ID ingresado ya existe. No se puede agregar el producto.");
                    alert.showAndWait();
                    return;
                }
                int cantidad = Integer.parseInt(txtCantidad.getText());
                double precio = Double.parseDouble(txtPrecio.getText());
                productoDao.insertProduct(
                        connection.getConnection(username, password),
                        id,
                        txtTitulo.getText(),
                        txtCategoria.getText(),
                        cantidad,
                        precio,
                        txtImagen.getText()
                );
                showProducts(); // Refrescar la tabla
                stage.close();
            } catch (NumberFormatException ex) {
                System.out.println("Error: ID, Cantidad y Precio deben ser numéricos");
            }
        });

        btnCancelar.setOnAction(e -> stage.close());

        Scene scene = new Scene(grid, 400, 350);
        stage.setScene(scene);
        stage.show();
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

        Stage stage = new Stage();
        stage.setTitle("Editar Producto");
        stage.setMinWidth(400);
        stage.setMinHeight(299);
        stage.setMaxHeight(300);
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        Label lblTitulo = new Label("Título:");
        TextField txtTitulo = new TextField(producto.getTitulo());
        txtTitulo.setMinWidth(300);
        Label lblCategoria = new Label("Categoría:");
        TextField txtCategoria = new TextField(producto.getCategoria());
        Label lblCantidad = new Label("Cantidad:");
        TextField txtCantidad = new TextField(String.valueOf(producto.getCantidad()));
        Label lblPrecio = new Label("Precio:");
        TextField txtPrecio = new TextField(String.valueOf(producto.getPrecio()));
        Label lblImagen = new Label("Imagen:");
        TextField txtImagen = new TextField(producto.getImagenUrl());

        Button btnCancelar = new Button("Cancelar");
        Button btnGuardar = new Button("Guardar");
        HBox buttonBox = new HBox(10, btnCancelar, btnGuardar);
        buttonBox.setAlignment(Pos.CENTER);

        grid.add(lblTitulo, 0, 0);
        grid.add(txtTitulo, 1, 0);
        grid.add(lblCategoria, 0, 1);
        grid.add(txtCategoria, 1, 1);
        grid.add(lblCantidad, 0, 2);
        grid.add(txtCantidad, 1, 2);
        grid.add(lblPrecio, 0, 3);
        grid.add(txtPrecio, 1, 3);
        grid.add(lblImagen, 0, 4);
        grid.add(txtImagen, 1, 4);
        grid.add(buttonBox, 0, 5, 2, 1);


        btnGuardar.setOnAction(e -> {
            try {
                int nuevaCantidad = Integer.parseInt(txtCantidad.getText());
                double nuevoPrecio = Double.parseDouble(txtPrecio.getText());
                productoDao.updateProduct(
                        connection.getConnection(username, password),
                        id,
                        txtTitulo.getText(),
                        txtCategoria.getText(),
                        nuevaCantidad,
                        nuevoPrecio,
                        txtImagen.getText()
                );
                showProducts(); // Refrescar la tabla
                stage.close();
            } catch (NumberFormatException ex) {
                System.out.println("Error: Cantidad y precio deben ser numéricos");
            }
        });

        btnCancelar.setOnAction(e -> stage.close());

        Scene scene = new Scene(grid, 400, 300);
        stage.setScene(scene);
        stage.show();
    }

    public void filterProducts(String searchText) {
        List<Producto> productos = productoDao.getAllProductos(connection.getConnection(username, password));

        // Filtrar productos cuyo título contenga el texto ingresado
        List<Producto> filteredProductos = productos.stream()
                .filter(p -> p.getTitulo().toLowerCase().contains(searchText.toLowerCase()))
                .sorted(Comparator.comparing(Producto::getCategoria)
                        .thenComparing(Producto::getTitulo))
                .toList();

        // Convertir la lista filtrada a ObservableList y actualizar la tabla
        ObservableList<Producto> observableProductos = FXCollections.observableArrayList(filteredProductos);
        tablaProductos.setItems(observableProductos);
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
        Stage ventanaMontos = new Stage();
        ventanaMontos.setTitle("Montos Facturados");

        double montoEfectivo = facturaDao.getMontoDiario(connection.getConnection(username, password), "Efectivo");
        double montoTransferencia = facturaDao.getMontoDiario(connection.getConnection(username, password), "Transferencia");
        double montoDiarioTotal = montoEfectivo + montoTransferencia;

        DecimalFormat decimalFormat = new DecimalFormat("$#,###");

        Label labelMontoEf = new Label("Efectivo: " + decimalFormat.format(montoEfectivo));
        Label labelMontoTr = new Label("Transferencia: " + decimalFormat.format(montoTransferencia));
        Label labelFacturaDiaria = new Label("Total facturado: " + decimalFormat.format(montoDiarioTotal));

        labelMontoEf.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        labelMontoTr.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        labelFacturaDiaria.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2E7D32;");

        VBox layout = new VBox(10, labelMontoEf, labelMontoTr, labelFacturaDiaria);
        layout.setPadding(new Insets(20, 30, 20, 30));
        layout.setAlignment(Pos.CENTER_LEFT);
        layout.setMinWidth(400);
        layout.setMaxHeight(400);

        ventanaMontos.setScene(new Scene(layout));
        ventanaMontos.show();
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
        while (true) {
            TextInputDialog dialogId = new TextInputDialog();
            dialogId.setTitle("Agregar Producto");
            dialogId.setHeaderText(null);
            dialogId.setContentText("ID (Ingrese 0 para salir):");
            Optional<String> resultId = dialogId.showAndWait();

            if (resultId.isEmpty()) break; // Si el usuario cierra el diálogo

            int id;
            try {
                id = Integer.parseInt(resultId.get());
            } catch (NumberFormatException e) {
                alertUtil.mostrarAlerta("ID inválido, intente nuevamente.");
                continue;
            }

            if (id == 0) break; // Opción para salir

            boolean confirmacion = mostrarImagenProducto(id);
            if (!confirmacion) continue;

            TextInputDialog dialogCantidad = new TextInputDialog();
            dialogCantidad.setTitle("Agregar Producto");
            dialogCantidad.setHeaderText(null);
            dialogCantidad.setContentText("Cantidad:");
            Optional<String> resultCantidad = dialogCantidad.showAndWait();

            int cantidad;
            try {
                cantidad = resultCantidad.map(Integer::parseInt).orElse(0);
                if (cantidad <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                alertUtil.mostrarAlerta("Cantidad inválida, intente nuevamente.");
                continue;
            }

            detalleFacturaDao.addProductoCarrito(connection.getConnection(username, password), id, cantidad);
            double valor = detalleFacturaDao.obtenerMontoTotalCarrito(connection.getConnection(username, password));
            DecimalFormat decimalFormat = new DecimalFormat("#,###");
            String valorFormateado = decimalFormat.format(valor);
            totalLabel.setText("Total: $" + valorFormateado);

            // Refresh table
            showCarrito();
        }
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
        Stage imageStage = new Stage();
        imageStage.initModality(Modality.APPLICATION_MODAL);
        imageStage.setTitle("Confirmar Producto");
        imageStage.setWidth(350);

        StackPane imageContainer = new StackPane();
        imageContainer.setPrefSize(300, 320);

        Image imagen = imagenesProductos.get(productId);

        if (imagen == null) {
            Rectangle placeholder = new Rectangle(300, 300, Color.LIGHTGRAY);
            Text noImageText = new Text("No hay imagen disponible");
            noImageText.setFill(Color.BLACK);
            noImageText.setFont(new Font(18));
            imageContainer.getChildren().addAll(placeholder, noImageText);
        } else {
            ImageView imageView = new ImageView(imagen);
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(300);
            imageView.setFitHeight(300);
            imageContainer.getChildren().add(imageView);
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

        dialog.getDialogPane().setStyle("-fx-font-size: 26px;");
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
                alertError.getDialogPane().setStyle("-fx-font-size: 26px;");
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
                alertInfo.getDialogPane().setStyle("-fx-font-size: 26px;");
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
            alertError.getDialogPane().setStyle("-fx-font-size: 26px;");
            alertError.showAndWait();
            return false;
        }
    }

    private <T> void configurarCopiaId(TableView<T> tabla, Function<T, Integer> getIdFunction) {
        tabla.setOnMouseClicked(event -> {
            T itemSeleccionado = tabla.getSelectionModel().getSelectedItem();
            if (itemSeleccionado != null) {
                int idSeleccionado = getIdFunction.apply(itemSeleccionado);

                // Copiar al portapapeles
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent contenido = new ClipboardContent();
                contenido.putString(String.valueOf(idSeleccionado));
                clipboard.setContent(contenido);

                System.out.println("ID copiado al portapapeles: " + idSeleccionado);
            }
        });
    }

    private void precargarImagenes() {
        List<Producto> productos = productoDao.getAllProductos(connection.getConnection(username, password));

        for (Producto producto : productos) {
            String imageUrl = producto.getImagenUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                try {
                    Image image = new Image(imageUrl, true);
                    imagenesProductos.put(producto.getId(), image);
                } catch (Exception e) {
                    System.out.println("Error al cargar imagen del producto ID " + producto.getId());
                }
            }
        }
    }

    public boolean solicitarContraseña() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Ingresar Contraseña");
        stage.setWidth(250);
        stage.setHeight(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setMaxWidth(200);

        Button btnIngresar = new Button("Ingresar");
        Label mensaje = new Label();
        mensaje.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: red;");

        final Boolean[] accesoPermitido = {null};

        btnIngresar.setOnAction(e -> {
            if (passwordField.getText().equals(CONTRASENA_CORRECTA)) {
                accesoPermitido[0] = true;
                stage.close();
            } else {
                mensaje.setText("Contraseña incorrecta");

                accesoPermitido[0] = false;
            }
        });

        passwordField.setOnKeyPressed(event -> { // listener
            if (event.getCode() == KeyCode.ENTER) {
                btnIngresar.fire();
            }
        });

        VBox layout = new VBox(12, new Label("Ingrese la contraseña:"), passwordField, btnIngresar, mensaje);
        layout.setAlignment(Pos.CENTER);
        stage.setScene(new Scene(layout));

        stage.showAndWait();

        return Boolean.TRUE.equals(accesoPermitido[0]);
    }

    public void mostrarFacturacion() {
        if (solicitarContraseña()) {
            obtenerMontoDiario();
        }
    }

    public void accesoAgregarProducto() {
        if (solicitarContraseña()){
            addProducts();
        }
    }

    public void accesoModificarProducto() {
        if (solicitarContraseña()) {
            updateProducts();
        }
    }

    public void accesoEliminarProducto() {
        if (solicitarContraseña()){
            deleteProducts();
        }
    }

    public void accesoEliminarFactura() {
        if (solicitarContraseña()) {
            eliminarFactura();
        }
    }

}
