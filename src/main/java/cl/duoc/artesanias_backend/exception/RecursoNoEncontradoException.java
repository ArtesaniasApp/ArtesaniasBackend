package cl.duoc.artesanias_backend.exception;

// Se lanza cuando algo no existe en la base de datos -> responde 404
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
