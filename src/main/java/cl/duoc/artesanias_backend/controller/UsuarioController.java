package cl.duoc.artesanias_backend.controller;

import cl.duoc.artesanias_backend.dto.PerfilRequest;
import cl.duoc.artesanias_backend.dto.UsuarioResponse;
import cl.duoc.artesanias_backend.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

// Perfil del usuario logueado. Authentication lo entrega Spring a partir del token (getName() = email)
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    // GET /api/usuarios/perfil
    @GetMapping("/perfil")
    public UsuarioResponse obtenerPerfil(Authentication autenticacion) {
        return usuarioService.obtenerPerfil(autenticacion.getName());
    }

    // PUT /api/usuarios/perfil
    @PutMapping("/perfil")
    public UsuarioResponse actualizarPerfil(Authentication autenticacion,
                                            @Valid @RequestBody PerfilRequest datos) {
        return usuarioService.actualizarPerfil(autenticacion.getName(), datos);
    }
}
