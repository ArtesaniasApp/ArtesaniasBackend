package cl.duoc.artesanias_backend.security;

import cl.duoc.artesanias_backend.model.TipoUsuario;
import cl.duoc.artesanias_backend.model.Usuario;
import cl.duoc.artesanias_backend.util.Constantes;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService crearServicio(String secreto, long expiracionMs) {
        JwtService servicio = new JwtService();
        ReflectionTestUtils.setField(servicio, "secreto", secreto);
        ReflectionTestUtils.setField(servicio, "expiracionMs", expiracionMs);
        return servicio;
    }

    private Usuario crearUsuario() {
        Usuario usuario = new Usuario();
        usuario.setEmail("admin@test.cl");
        usuario.setTipoUsuario(new TipoUsuario(1L, Constantes.ROL_ADMIN));
        return usuario;
    }

    @Test
    void tokenGenerado_guardaEmailYRol() {
        JwtService servicio = crearServicio("ClaveDePruebaDeMasDe32Caracteres-2026", 60000);

        String token = servicio.generarToken(crearUsuario());
        Claims datos = servicio.leerToken(token);

        assertTrue(servicio.esValido(token));
        assertEquals("admin@test.cl", datos.getSubject());
        assertEquals(Constantes.ROL_ADMIN, datos.get("rol", String.class));
    }

    @Test
    void tokenVencido_noEsValido() {
        JwtService servicio = crearServicio("ClaveDePruebaDeMasDe32Caracteres-2026", -1000);

        assertFalse(servicio.esValido(servicio.generarToken(crearUsuario())));
    }

    @Test
    void tokenFirmadoConOtraClave_noEsValido() {
        JwtService otro = crearServicio("OtraClaveDistintaDeMasDe32Caracteres!!", 60000);
        JwtService servicio = crearServicio("ClaveDePruebaDeMasDe32Caracteres-2026", 60000);

        assertFalse(servicio.esValido(otro.generarToken(crearUsuario())));
    }

    @Test
    void textoQueNoEsToken_noEsValido() {
        JwtService servicio = crearServicio("ClaveDePruebaDeMasDe32Caracteres-2026", 60000);

        assertFalse(servicio.esValido("esto-no-es-un-token"));
    }
}
