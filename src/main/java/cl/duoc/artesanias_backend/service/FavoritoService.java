package cl.duoc.artesanias_backend.service;

import cl.duoc.artesanias_backend.dto.ProductoResponse;
import cl.duoc.artesanias_backend.exception.RecursoNoEncontradoException;
import cl.duoc.artesanias_backend.model.Favorito;
import cl.duoc.artesanias_backend.model.FavoritoId;
import cl.duoc.artesanias_backend.model.Producto;
import cl.duoc.artesanias_backend.model.Usuario;
import cl.duoc.artesanias_backend.repository.FavoritoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoritoService {

    private final FavoritoRepository favoritoRepository;
    private final UsuarioService usuarioService;
    private final ProductoService productoService;

    public List<ProductoResponse> listar(String email) {
        Usuario usuario = usuarioService.buscarPorEmail(email);
        return favoritoRepository.findByUsuarioIdOrderByFechaAgregadoDesc(usuario.getId())
                .stream()
                .map(Favorito::getProducto)
                .filter(producto -> !productoService.estaInactivo(producto))
                .map(ProductoResponse::desde)
                .toList();
    }

    @Transactional
    public void agregar(String email, Long productoId) {
        Usuario usuario = usuarioService.buscarPorEmail(email);
        Producto producto = productoService.buscarProductoVisible(productoId);
        FavoritoId id = new FavoritoId(usuario.getId(), producto.getId());

        // Si ya estaba en favoritos no se hace nada
        if (favoritoRepository.existsById(id)) {
            return;
        }

        favoritoRepository.save(new Favorito(id, usuario, producto, LocalDateTime.now()));
    }

    @Transactional
    public void eliminar(String email, Long productoId) {
        Usuario usuario = usuarioService.buscarPorEmail(email);
        FavoritoId id = new FavoritoId(usuario.getId(), productoId);

        if (!favoritoRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("El producto " + productoId + " no está en favoritos");
        }

        favoritoRepository.deleteById(id);
    }
}
