# Backend Ale y Leo SpA — API REST (Spring Boot)

Monolito Spring Boot 4 + Java 21 + MySQL + Spring Security con JWT.

## Cómo ejecutarlo
1. Encender MySQL (XAMPP/Laragon/MySQL Workbench) en el puerto 3306.
   Usuario `root` sin contraseña. Si tu MySQL usa otra, cámbiala en `src/main/resources/application.properties`.
   La base `artesanias_db` se crea sola.
2. En la carpeta del proyecto: `mvnw spring-boot:run` (o el botón Run de `ArtesaniasBackendApplication`).
3. La API queda en `http://localhost:8080`. Las tablas y los datos de prueba se crean al iniciar.
4. Tests: `mvnw test` (usan H2 en memoria, no necesitan MySQL).

Usuarios de prueba: `admin@aleyleo.cl / admin123` (ADMIN) · `cliente@aleyleo.cl / cliente123` (CLIENTE).
El archivo `pruebas.http` tiene ejemplos de todas las peticiones.

## Estructura (paquetes)
| Paquete | Responsabilidad |
|---|---|
| `model` | Entidades JPA = tablas de la base de datos |
| `repository` | Interfaces `JpaRepository`: consultas a la BD (Spring genera el SQL a partir del nombre del método) |
| `dto` | Objetos que entran (`...Request`) y salen (`...Response`) de la API. Nunca se expone la contraseña |
| `service` | Lógica del negocio: stock, total del pedido, pago simulado, estados |
| `controller` | Endpoints REST (`@RestController`) |
| `security` | JWT (`JwtService`, `JwtFiltro`) y reglas de acceso (`SecurityConfig`) |
| `exception` | Excepciones propias y `GlobalExceptionHandler` (convierte errores en JSON) |
| `config` | `DatosIniciales`: carga datos ficticios al arrancar |
| `util` | `Constantes`: nombres de roles y estados |

Flujo de una petición: **Controller → Service → Repository → BD**, y de vuelta Entidad → DTO → JSON.

## Seguridad
- Login/registro devuelven un **token JWT**. La app lo envía en cada petición: `Authorization: Bearer <token>`.
- `JwtFiltro` lee el token en cada petición y deja al usuario autenticado con su rol.
- Rutas: públicas (`/api/auth/**`, GET catálogo y listas) · logueado (favoritos, pedidos, perfil) · solo ADMIN (`/api/admin/**`).
- Contraseñas guardadas con **BCrypt**.

## Endpoints
| Método | Ruta | Acceso | Caso de uso |
|---|---|---|---|
| POST | `/api/auth/registro` | público | Registro con dirección |
| POST | `/api/auth/login` | público | Login |
| GET | `/api/productos?clasificacionId=&material=` | público | CU01 catálogo (filtros opcionales) |
| GET | `/api/productos/materiales` | público | materiales para el filtro |
| GET | `/api/productos/{id}` | público | CU01 detalle / QR |
| GET | `/api/clasificaciones`, `/api/estados-producto`, `/api/estados-pedido`, `/api/metodos-pago` | público | listas |
| GET/PUT | `/api/usuarios/perfil` | logueado | perfil |
| GET | `/api/favoritos` | logueado | CU02 |
| POST/DELETE | `/api/favoritos/{productoId}` | logueado | CU02 |
| POST | `/api/pedidos` | logueado | CU03/CU04 checkout |
| GET | `/api/pedidos/mis-pedidos`, `/api/pedidos/{id}` | logueado | historial |
| GET/POST | `/api/admin/productos` | ADMIN | CU06 inventario |
| PUT/DELETE | `/api/admin/productos/{id}` | ADMIN | CU06 (DELETE = inactivar) |
| PATCH | `/api/admin/productos/{id}/stock`, `/{id}/estado` | ADMIN | CU06 |
| POST | `/api/admin/clasificaciones` | ADMIN | CU06 |
| GET | `/api/admin/pedidos?estadoId=` | ADMIN | CU05 |
| PATCH | `/api/admin/pedidos/{id}/estado` | ADMIN | CU05 |

## Reglas de negocio
- **Stock**: al crear un pedido se valida y descuenta; con stock 0 el producto pasa a AGOTADO y vuelve a DISPONIBLE al reponer. Cancelar un pedido devuelve el stock.
- **Precio**: se toma de la BD al comprar y se guarda en `detalle_pedido.precio_unitario`.
- **Pago simulado**: siempre se aprueba (punto donde iría Webpay u otra pasarela).
- **Estados de pedido**: PENDIENTE → RECIBIDA → EN_PREPARACION → DESPACHADA → FINALIZADA (o CANCELADA). Un pedido FINALIZADA o CANCELADA ya no cambia.
- **Eliminar producto** = dejarlo INACTIVO (puede estar en pedidos antiguos).

## Cambios respecto al modelo relacional original
- `usuario.direccion` y `pedido.direccion_despacho` (dirección de despacho exigida por el profesor).
- `producto.material`: material corto (Greda, Lana, Mimbre...) para filtrar el catálogo; `descripcion_material` queda como detalle.
- Precios como enteros (`Integer`): los pesos chilenos no usan decimales.
