package cl.duoc.artesanias_backend.security;

import cl.duoc.artesanias_backend.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

// Crea y valida los tokens JWT
@Service
public class JwtService {

    // Valores leídos desde application.properties
    @Value("${jwt.secreto}")
    private String secreto;

    @Value("${jwt.expiracion-ms}")
    private long expiracionMs;

    // Clave con la que se firma el token (si alguien modifica el token, la firma deja de calzar)
    private SecretKey obtenerClave() {
        return Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
    }

    // Genera un token que guarda el email (subject) y el rol del usuario
    public String generarToken(Usuario usuario) {
        Date ahora = new Date();
        Date vencimiento = new Date(ahora.getTime() + expiracionMs);

        return Jwts.builder()
                .subject(usuario.getEmail())
                .claim("rol", usuario.getTipoUsuario().getNombre())
                .issuedAt(ahora)
                .expiration(vencimiento)
                .signWith(obtenerClave())
                .compact();
    }

    // Lee el contenido del token. Lanza JwtException si es inválido o está vencido
    public Claims leerToken(String token) {
        return Jwts.parser()
                .verifyWith(obtenerClave())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean esValido(String token) {
        try {
            leerToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
