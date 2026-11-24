package cl.duoc.artesanias_backend.exception;

// Se lanza cuando se rompe una regla del negocio (sin stock, pedido cancelado, etc.) -> responde 400
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
