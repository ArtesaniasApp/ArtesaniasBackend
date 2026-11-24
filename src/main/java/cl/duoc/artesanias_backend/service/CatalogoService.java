package cl.duoc.artesanias_backend.service;

import cl.duoc.artesanias_backend.dto.ClasificacionRequest;
import cl.duoc.artesanias_backend.exception.RecursoDuplicadoException;
import cl.duoc.artesanias_backend.model.Clasificacion;
import cl.duoc.artesanias_backend.model.EstadoPedido;
import cl.duoc.artesanias_backend.model.EstadoProducto;
import cl.duoc.artesanias_backend.model.MetodoPago;
import cl.duoc.artesanias_backend.repository.ClasificacionRepository;
import cl.duoc.artesanias_backend.repository.EstadoPedidoRepository;
import cl.duoc.artesanias_backend.repository.EstadoProductoRepository;
import cl.duoc.artesanias_backend.repository.MetodoPagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

// Tablas "de apoyo": clasificaciones, estados y métodos de pago
@Service
@RequiredArgsConstructor
public class CatalogoService {

    private final ClasificacionRepository clasificacionRepository;
    private final EstadoProductoRepository estadoProductoRepository;
    private final EstadoPedidoRepository estadoPedidoRepository;
    private final MetodoPagoRepository metodoPagoRepository;

    public List<Clasificacion> listarClasificaciones() {
        return clasificacionRepository.findAll();
    }

    public Clasificacion crearClasificacion(ClasificacionRequest datos) {
        String nombre = datos.nombre().trim();
        if (clasificacionRepository.existsByNombreIgnoreCase(nombre)) {
            throw new RecursoDuplicadoException("Ya existe la clasificación " + nombre);
        }
        return clasificacionRepository.save(new Clasificacion(null, nombre));
    }

    public List<EstadoProducto> listarEstadosProducto() {
        return estadoProductoRepository.findAll();
    }

    public List<EstadoPedido> listarEstadosPedido() {
        return estadoPedidoRepository.findAll();
    }

    public List<MetodoPago> listarMetodosPago() {
        return metodoPagoRepository.findAll();
    }
}
