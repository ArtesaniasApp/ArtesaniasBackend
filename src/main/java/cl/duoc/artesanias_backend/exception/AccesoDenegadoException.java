package cl.duoc.artesanias_backend.exception;

// Se lanza cuando un usuario intenta ver algo que no le pertenece -> responde 403
public class AccesoDenegadoException extends RuntimeException {

    public AccesoDenegadoException(String mensaje) {
        super(mensaje);
    }
}
