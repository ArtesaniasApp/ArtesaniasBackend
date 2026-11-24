package cl.duoc.artesanias_backend.controller;

import cl.duoc.artesanias_backend.model.Clasificacion;
import cl.duoc.artesanias_backend.model.EstadoPedido;
import cl.duoc.artesanias_backend.model.EstadoProducto;
import cl.duoc.artesanias_backend.model.MetodoPago;
import cl.duoc.artesanias_backend.service.CatalogoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Listas públicas que la app necesita para filtros y formularios
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CatalogoController {

    private final CatalogoService catalogoService;

    // GET /api/clasificaciones
    @GetMapping("/clasificaciones")
    public List<Clasificacion> listarClasificaciones() {
        return catalogoService.listarClasificaciones();
    }

    // GET /api/estados-producto
    @GetMapping("/estados-producto")
    public List<EstadoProducto> listarEstadosProducto() {
        return catalogoService.listarEstadosProducto();
    }

    // GET /api/estados-pedido
    @GetMapping("/estados-pedido")
    public List<EstadoPedido> listarEstadosPedido() {
        return catalogoService.listarEstadosPedido();
    }

    // GET /api/metodos-pago
    @GetMapping("/metodos-pago")
    public List<MetodoPago> listarMetodosPago() {
        return catalogoService.listarMetodosPago();
    }
}
