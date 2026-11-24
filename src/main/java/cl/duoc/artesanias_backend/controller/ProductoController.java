package cl.duoc.artesanias_backend.controller;

import cl.duoc.artesanias_backend.dto.ProductoResponse;
import cl.duoc.artesanias_backend.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Catálogo público (CU01): lo que ve el cliente
@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    // GET /api/productos              -> todo el catálogo
    // GET /api/productos?clasificacionId=2 -> solo una categoría
    @GetMapping
    public List<ProductoResponse> listar(@RequestParam(required = false) Long clasificacionId,
                                         @RequestParam(required = false) String material) {
        return productoService.listarCatalogo(clasificacionId, material);
    }

    // GET /api/productos/materiales -> ["Alpaca", "Greda", "Lana", ...] para el filtro de la app
    @GetMapping("/materiales")
    public List<String> listarMateriales() {
        return productoService.listarMateriales();
    }

    // GET /api/productos/5  (también lo usa la app al leer el QR de un producto)
    @GetMapping("/{id}")
    public ProductoResponse obtener(@PathVariable Long id) {
        return productoService.obtener(id);
    }
}
