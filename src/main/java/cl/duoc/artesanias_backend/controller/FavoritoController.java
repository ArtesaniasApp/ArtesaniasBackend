package cl.duoc.artesanias_backend.controller;

import cl.duoc.artesanias_backend.dto.ProductoResponse;
import cl.duoc.artesanias_backend.service.FavoritoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Favoritos del cliente logueado (CU02)
@RestController
@RequestMapping("/api/favoritos")
@RequiredArgsConstructor
public class FavoritoController {

    private final FavoritoService favoritoService;

    // GET /api/favoritos
    @GetMapping
    public List<ProductoResponse> listar(Authentication autenticacion) {
        return favoritoService.listar(autenticacion.getName());
    }

    // POST /api/favoritos/5
    @PostMapping("/{productoId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void agregar(Authentication autenticacion, @PathVariable Long productoId) {
        favoritoService.agregar(autenticacion.getName(), productoId);
    }

    // DELETE /api/favoritos/5
    @DeleteMapping("/{productoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(Authentication autenticacion, @PathVariable Long productoId) {
        favoritoService.eliminar(autenticacion.getName(), productoId);
    }
}
