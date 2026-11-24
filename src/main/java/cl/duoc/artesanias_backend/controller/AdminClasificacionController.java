package cl.duoc.artesanias_backend.controller;

import cl.duoc.artesanias_backend.dto.ClasificacionRequest;
import cl.duoc.artesanias_backend.model.Clasificacion;
import cl.duoc.artesanias_backend.service.CatalogoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/clasificaciones")
@RequiredArgsConstructor
public class AdminClasificacionController {

    private final CatalogoService catalogoService;

    // POST /api/admin/clasificaciones  body: { "nombre": "Vidrio" }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Clasificacion crear(@Valid @RequestBody ClasificacionRequest datos) {
        return catalogoService.crearClasificacion(datos);
    }
}
