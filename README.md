## Stock System - Sistema de Gestión de Stock y Facturación

### Descripción
Stock System es una aplicación desarrollada en Java con PostgreSQL para la gestión de stock y facturación en comercios (kiosko, almacén, farmacia, heladería). Su objetivo es facilitar el control de inventario y permitir cobrar de forma rápida y sencilla.
Este sistema es compatible con scanners 1D e impresoras térmicas.

<p align="center">
  <img src="https://github.com/user-attachments/assets/7c6d998d-2be0-4964-a9cf-8a52bded39dc" />
</p>

**Seguridad y Control de Acceso**
- Acceso restringido para agregar, modificar o eliminar productos mediante contraseña de administrador.
- Consulta de totales facturados solo accesible con contraseña.

**Funcionalidades Principales**
- Gestión de Productos
- Agregar, modificar y eliminar productos.
- Buscar productos por nombre, ID o categoría.
- Control de stock automático al realizar una venta.
- Almacenar información de cada producto: nombre, categoría, cantidad, precio e imagen (enlace url).

<p align="center">
  <img src="https://github.com/user-attachments/assets/a75412ae-a339-4f2f-b1c5-7c55c1479123" />
</p>
<p align="center">
  <img src="https://github.com/user-attachments/assets/d2b67a46-da7f-45ce-a8d8-9c134c97b63b" />
</p>

**Carrito de Compras y Facturación**
- Agregar productos al carrito por ID o escaneo con código de barras.
- Procesar ventas y descontar stock automáticamente.
- Métodos de pago: efectivo (con cálculo de vuelto) o transferencia bancaria.
- Generación y almacenamiento de facturas con fecha y hora.
- Posibilidad de recuperar facturas anteriores para verificar compras previas.
- Opción de imprimir tickets de compra.

**Interfaz y Flujo de Venta**
- Presionar la tecla 0 para iniciar el proceso de venta.
- Escanear un producto (o ingresar ID manualmente).
- Visualizar la imagen del producto y confirmar la selección.
- Ingresar la cantidad deseada.
- El sistema vuelve a solicitar un nuevo ID para continuar con la venta.

<p align="center">
  <img src="https://github.com/user-attachments/assets/50f615b7-f96e-4fb8-819d-ca085ad7f1d2" />
</p>
<p align="center">
  <img src="https://github.com/user-attachments/assets/22153d93-4fcd-45e1-8aa9-93349ad6a772" />
</p>
<p align="center">
  <img src="https://github.com/user-attachments/assets/98c72d35-a073-4890-9fff-d0430fe910e3" />
</p>

**Tecnologías Utilizadas**
- Lenguaje: Java (JavaFX para la interfaz gráfica)
- Base de Datos: PostgreSQL
- Arquitectura: Separación en paquetes: conexion, dao, gui, modelo.

**Instalación y Configuración**
- Clonar el repositorio.
- Configurar la base de datos en PostgreSQL e importar el esquema.
- Ajustar la configuración de conexión en conexion/ConexionBD.java.
- Ejecutar el proyecto con Maven: mvn javafx:run.
