package cl.duoc.artesanias_backend.exception;

// Login con email o contraseña incorrectos -> responde 401
public class CredencialesInvalidasException extends RuntimeException {

    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }
}
