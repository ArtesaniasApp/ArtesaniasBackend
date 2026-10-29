package cl.duoc.artesanias_backend.controller;

import cl.duoc.artesanias_backend.dto.AuthResponse;
import cl.duoc.artesanias_backend.dto.LoginRequest;
import cl.duoc.artesanias_backend.dto.RegistroRequest;
import cl.duoc.artesanias_backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

// Rutas públicas para crear cuenta e iniciar sesión
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // POST /api/auth/registro
    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse registrar(@Valid @RequestBody RegistroRequest datos) {
        return authService.registrar(datos);
    }

    // POST /api/auth/login
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest datos) {
        return authService.login(datos);
    }
}
