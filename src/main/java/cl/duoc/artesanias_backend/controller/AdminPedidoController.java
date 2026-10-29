package cl.duoc.artesanias_backend.controller;

import cl.duoc.artesanias_backend.dto.EstadoRequest;
import cl.duoc.artesanias_backend.dto.PedidoResponse;
import cl.duoc.artesanias_backend.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Gestión de logística y estado de pedidos (CU05). Solo ADMIN
@RestController
@RequestMapping("/api/admin/pedidos")
@RequiredArgsConstructor
public class AdminPedidoController {

    private final PedidoService pedidoService;

    // GET /api/admin/pedidos            -> todos
    // GET /api/admin/pedidos?estadoId=1 -> filtrados por estado
    @GetMapping
    public List<PedidoResponse> listar(@RequestParam(required = false) Long estadoId) {
        return pedidoService.listarTodos(estadoId);
    }

    // PATCH /api/admin/pedidos/7/estado  body: { "estadoId": 3 }
    @PatchMapping("/{id}/estado")
    public PedidoResponse cambiarEstado(@PathVariable Long id, @Valid @RequestBody EstadoRequest datos) {
        return pedidoService.cambiarEstado(id, datos.estadoId());
    }
}
