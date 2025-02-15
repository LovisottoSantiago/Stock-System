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
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
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
    private static final String CONTRASENA_CORRECTA = "password";


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
        TextInputDialog dialogId = new TextInputDialog();
        dialogId.setTitle("Agregar Producto");
        dialogId.setHeaderText(null);
        dialogId.setContentText("ID:");
        Optional<String> resultId = dialogId.showAndWait();
        int id = resultId.map(Integer::parseInt).orElseThrow(() -> new RuntimeException("ID no ingresado"));

        if (productoDao.productoExiste(connection.getConnection(username, password), id)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ID Existente");
            alert.setHeaderText(null);
            alert.setContentText("El ID ingresado ya existe. No se puede agregar el producto.");
            alert.showAndWait();
            return;
        }

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


    public void mostrarFacturacion(){
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Ingresar Contraseña");
        stage.setWidth(250);
        stage.setHeight(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setMaxWidth(200);
        Button btnIngresar = new Button("Ingresar");
        Label mensaje = new Label();

        btnIngresar.setOnAction(e -> {
            if (passwordField.getText().equals(CONTRASENA_CORRECTA)) {
                stage.close();
                obtenerMontoDiario();
            } else {
                mensaje.setText("Contraseña incorrecta");
            }
        });

        VBox layout = new VBox(10, new Label("Ingrese la contraseña:"), passwordField, btnIngresar, mensaje);
        layout.setAlignment(Pos.CENTER);
        layout.setMinWidth(200);
        stage.setScene(new Scene(layout));
        stage.showAndWait();
    }

}
