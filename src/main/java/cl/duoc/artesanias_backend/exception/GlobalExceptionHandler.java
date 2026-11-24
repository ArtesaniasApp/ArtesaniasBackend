package cl.duoc.artesanias_backend.exception;

import cl.duoc.artesanias_backend.dto.ErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

// Atrapa las excepciones de todos los controladores y las convierte en una respuesta JSON ordenada
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> manejarNoEncontrado(RecursoNoEncontradoException ex) {
        return crearRespuesta(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ErrorResponse> manejarReglaNegocio(ReglaNegocioException ex) {
        return crearRespuesta(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<ErrorResponse> manejarCredencialesInvalidas(CredencialesInvalidasException ex) {
        return crearRespuesta(HttpStatus.UNAUTHORIZED, ex.getMessage(), null);
    }

    @ExceptionHandler(RecursoDuplicadoException.class)
    public ResponseEntity<ErrorResponse> manejarDuplicado(RecursoDuplicadoException ex) {
        return crearRespuesta(HttpStatus.CONFLICT, ex.getMessage(), null);
    }

    // Respaldo: la BD rechazó un valor único repetido (ej: dos registros simultáneos con el mismo email)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> manejarIntegridad(DataIntegrityViolationException ex) {
        return crearRespuesta(HttpStatus.CONFLICT, "El dato ya existe o entra en conflicto con otro registro", null);
    }

    @ExceptionHandler(AccesoDenegadoException.class)
    public ResponseEntity<ErrorResponse> manejarAccesoDenegado(AccesoDenegadoException ex) {
        return crearRespuesta(HttpStatus.FORBIDDEN, ex.getMessage(), null);
    }

    // Errores de @Valid: devuelve qué campo falló y por qué
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> manejarValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errores.put(error.getField(), error.getDefaultMessage());
        }
        return crearRespuesta(HttpStatus.BAD_REQUEST, "Datos inválidos", errores);
    }

    // JSON mal escrito o con tipos incorrectos
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> manejarJsonInvalido(HttpMessageNotReadableException ex) {
        return crearRespuesta(HttpStatus.BAD_REQUEST, "El cuerpo de la petición no es un JSON válido", null);
    }

    private ResponseEntity<ErrorResponse> crearRespuesta(HttpStatus estado, String mensaje, Map<String, String> errores) {
        ErrorResponse cuerpo = new ErrorResponse(estado.value(), mensaje, LocalDateTime.now(), errores);
        return ResponseEntity.status(estado).body(cuerpo);
    }
}
