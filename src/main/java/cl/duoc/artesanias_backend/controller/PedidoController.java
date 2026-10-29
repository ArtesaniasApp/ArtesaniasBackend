package cl.duoc.artesanias_backend.controller;

import cl.duoc.artesanias_backend.dto.PedidoRequest;
import cl.duoc.artesanias_backend.dto.PedidoResponse;
import cl.duoc.artesanias_backend.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Pedidos del cliente logueado (CU04)
@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    // POST /api/pedidos -> checkout del carro
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponse crear(Authentication autenticacion, @Valid @RequestBody PedidoRequest datos) {
        return pedidoService.crear(autenticacion.getName(), datos);
    }

    // GET /api/pedidos/mis-pedidos
    @GetMapping("/mis-pedidos")
    public List<PedidoResponse> misPedidos(Authentication autenticacion) {
        return pedidoService.misPedidos(autenticacion.getName());
    }

    // GET /api/pedidos/7
    @GetMapping("/{id}")
    public PedidoResponse obtener(Authentication autenticacion, @PathVariable Long id) {
        return pedidoService.obtener(autenticacion.getName(), id);
    }
}
