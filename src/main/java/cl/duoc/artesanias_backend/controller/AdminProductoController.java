package cl.duoc.artesanias_backend.controller;

import cl.duoc.artesanias_backend.dto.EstadoRequest;
import cl.duoc.artesanias_backend.dto.ProductoRequest;
import cl.duoc.artesanias_backend.dto.ProductoResponse;
import cl.duoc.artesanias_backend.dto.StockRequest;
import cl.duoc.artesanias_backend.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Gestión de inventario (CU06). Todo /api/admin/** exige rol ADMIN (ver SecurityConfig)
@RestController
@RequestMapping("/api/admin/productos")
@RequiredArgsConstructor
public class AdminProductoController {

    private final ProductoService productoService;

    // GET /api/admin/productos -> inventario completo, incluidos los inactivos
    @GetMapping
    public List<ProductoResponse> listarInventario() {
        return productoService.listarInventario();
    }

    // POST /api/admin/productos
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoResponse crear(@Valid @RequestBody ProductoRequest datos) {
        return productoService.crear(datos);
    }

    // PUT /api/admin/productos/5
    @PutMapping("/{id}")
    public ProductoResponse actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequest datos) {
        return productoService.actualizar(id, datos);
    }

    // PATCH /api/admin/productos/5/stock   body: { "stock": 10 }
    @PatchMapping("/{id}/stock")
    public ProductoResponse actualizarStock(@PathVariable Long id, @Valid @RequestBody StockRequest datos) {
        return productoService.actualizarStock(id, datos.stock());
    }

    // PATCH /api/admin/productos/5/estado  body: { "estadoId": 3 }
    @PatchMapping("/{id}/estado")
    public ProductoResponse cambiarEstado(@PathVariable Long id, @Valid @RequestBody EstadoRequest datos) {
        return productoService.cambiarEstado(id, datos.estadoId());
    }

    // DELETE /api/admin/productos/5 -> lo deja INACTIVO (borrado lógico)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desactivar(@PathVariable Long id) {
        productoService.desactivar(id);
    }
}
