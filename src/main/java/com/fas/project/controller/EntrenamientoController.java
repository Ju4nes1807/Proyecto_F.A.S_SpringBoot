package com.fas.project.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fas.project.dto.entrenamiento.EntrenamientoDTO;
import com.fas.project.model.User;
import com.fas.project.repository.UserRepository;
import com.fas.project.service.EntrenamientoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/entrenamientos")
@RequiredArgsConstructor
public class EntrenamientoController {

    private final EntrenamientoService entrenamientoService;
    private final UserRepository userRepository; // para obtener el usuario autenticado si lo necesitas

    // Crear (líder) - body usa EntrenamientoDTO; idLider puede venir del DTO
    @PostMapping
    public ResponseEntity<EntrenamientoDTO> crear(@RequestBody EntrenamientoDTO dto, Authentication auth) {
        // opcional: forzar creador desde auth si prefieres
        if (auth != null && (dto.getIdLider() == null || dto.getIdLider() == 0)) {
            User u = userRepository.findByEmail(auth.getName()).orElse(null);
            if (u != null) dto.setIdLider(u.getIdUsuario());
        }
        EntrenamientoDTO creado = entrenamientoService.crearEntrenamiento(dto);
        return ResponseEntity.ok(creado);
    }

    // Listar / filtrar (público según rol; service se encargará de permisos)
    @GetMapping
    public ResponseEntity<List<EntrenamientoDTO>> listar(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String fecha,
            @RequestParam(required = false) Integer idCategoria,
            @RequestParam(required = false) Integer idEscuela,
            @RequestParam(required = false) Integer idUbicacion
    ) {
        List<EntrenamientoDTO> lista = entrenamientoService.filtrar(titulo, fecha, idCategoria, idEscuela, idUbicacion);
        return ResponseEntity.ok(lista);
    }

    // Obtener por id
    @GetMapping("/{id}")
    public ResponseEntity<EntrenamientoDTO> obtener(@PathVariable Integer id) {
        EntrenamientoDTO dto = entrenamientoService.obtenerPorId(id);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }

    // Actualizar (solo creador) - body usa EntrenamientoDTO
    @PutMapping("/{id}")
    public ResponseEntity<EntrenamientoDTO> actualizar(@PathVariable Integer id,
                                                       @RequestBody EntrenamientoDTO dto,
                                                       Authentication auth) {
        // opcional: forzar idLider desde auth
        if (auth != null && (dto.getIdLider() == null || dto.getIdLider() == 0)) {
            User u = userRepository.findByEmail(auth.getName()).orElse(null);
            if (u != null) dto.setIdLider(u.getIdUsuario());
        }
        EntrenamientoDTO actualizado = entrenamientoService.actualizarEntrenamiento(id, dto);
        return ResponseEntity.ok(actualizado);
    }

    // Eliminar (solo creador)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id, Authentication auth) {
        User u = null;
        if (auth != null) {
            u = userRepository.findByEmail(auth.getName()).orElse(null);
        }
        entrenamientoService.eliminarEntrenamiento(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/crearentrenamiento")
public ResponseEntity<EntrenamientoDTO> crear(@RequestBody EntrenamientoDTO dto) {
    EntrenamientoDTO creado = entrenamientoService.crearEntrenamiento(dto);
    return ResponseEntity.ok(creado);
}

}
