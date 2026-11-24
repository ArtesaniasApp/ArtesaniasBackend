package cl.duoc.artesanias_backend.exception;

// Se intenta crear algo que ya existe (ej: email repetido) -> responde 409
public class RecursoDuplicadoException extends RuntimeException {

    public RecursoDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
