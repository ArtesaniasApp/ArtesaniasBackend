package cl.duoc.artesanias_backend.dto;

import java.time.LocalDateTime;
import java.util.Map;

// Formato único para todos los errores que devuelve la API
public record ErrorResponse(
        int estado,
        String mensaje,
        LocalDateTime fecha,
        Map<String, String> errores
) {
}
