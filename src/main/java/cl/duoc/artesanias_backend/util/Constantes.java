package cl.duoc.artesanias_backend.util;

// Nombres fijos que se usan en la base de datos y en la lógica.
// Tenerlos en un solo lugar evita errores de tipeo.
public final class Constantes {

    private Constantes() {
    }

    // tipo_usuario
    public static final String ROL_ADMIN = "ADMIN";
    public static final String ROL_CLIENTE = "CLIENTE";

    // estado_producto
    public static final String PRODUCTO_DISPONIBLE = "DISPONIBLE";
    public static final String PRODUCTO_AGOTADO = "AGOTADO";
    public static final String PRODUCTO_INACTIVO = "INACTIVO";

    // estado_pedido (en orden del flujo)
    public static final String PEDIDO_PENDIENTE = "PENDIENTE";
    public static final String PEDIDO_RECIBIDA = "RECIBIDA";
    public static final String PEDIDO_EN_PREPARACION = "EN_PREPARACION";
    public static final String PEDIDO_DESPACHADA = "DESPACHADA";
    public static final String PEDIDO_FINALIZADA = "FINALIZADA";
    public static final String PEDIDO_CANCELADA = "CANCELADA";
}
