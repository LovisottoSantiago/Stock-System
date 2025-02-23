## Stock System - Sistema de Gestión de Stock y Facturación

### Descripción
Stock System es una aplicación desarrollada en Java con PostgreSQL para la gestión de stock y facturación en comercios. Su objetivo es optimizar el control de inventario y facilitar el proceso de ventas, reduciendo errores y posibles fraudes internos.

**Funcionalidades Principales**
- Gestión de Productos
- Agregar, modificar y eliminar productos.
- Buscar productos por nombre, ID o categoría.
- Control de stock automático al realizar una venta.
- Almacenar información de cada producto: nombre, categoría, cantidad, precio e imagen.

**Carrito de Compras y Facturación**
- Agregar productos al carrito por ID o escaneo con código de barras.
- Procesar ventas y descontar stock automáticamente.
- Métodos de pago: efectivo (con cálculo de vuelto) o transferencia bancaria.
- Generación y almacenamiento de facturas con fecha y hora.
- Posibilidad de recuperar facturas anteriores para verificar compras previas.
- Opción de imprimir tickets de compra.

**Seguridad y Control de Acceso**
- Acceso restringido para agregar, modificar o eliminar productos mediante contraseña de administrador.
- Consulta de totales facturados solo accesible con contraseña.

**Interfaz y Flujo de Venta**
- Presionar la tecla 0 para iniciar el proceso de venta.
- Escanear un producto (o ingresar ID manualmente).
- Visualizar la imagen del producto y confirmar la selección.
- Ingresar la cantidad deseada.
- El sistema vuelve a solicitar un nuevo ID para continuar con la venta.

**Tecnologías Utilizadas**
- Lenguaje: Java (JavaFX para la interfaz gráfica)
- Base de Datos: PostgreSQL
- Arquitectura: Separación en paquetes: conexion, dao, gui, modelo.

**Instalación y Configuración**
- Clonar el repositorio.
- Configurar la base de datos en PostgreSQL e importar el esquema.
- Ajustar la configuración de conexión en conexion/ConexionBD.java.
- Ejecutar el proyecto con Maven: mvn javafx:run.

### Estado del Proyecto
Este sistema fue desarrollado con el objetivo de mejorar la administración de un comercio, optimizando la gestión de stock y ventas. Se encuentra en constante mejora para optimizar la experiencia de usuario y el rendimiento en equipos de bajos recursos.
