package com.fas.project.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fas.project.dto.entrenamiento.EntrenamientoDTO;
import com.fas.project.mapper.MapperEntrenamiento;
import com.fas.project.model.*;
import com.fas.project.repository.*;

@Service
public class EntrenamientoService {

    @Autowired private EntrenamientoRepository entrenamientoRepository;
    @Autowired private EscuelaRepository escuelaRepository;
    @Autowired private CategoriaRepository categoriaRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private UbicacionRepository ubicacionRepository;

    @Autowired private MapperEntrenamiento mapper;

    // ============================================================
    // 1. CREAR ENTRENAMIENTO
    // ============================================================
    public EntrenamientoDTO crearEntrenamiento(EntrenamientoDTO dto) {

        Escuela escuela = escuelaRepository.findById(dto.getIdEscuela())
                .orElseThrow(() -> new RuntimeException("Escuela no encontrada"));

        Categoria categoria = categoriaRepository.findById(dto.getIdCategoria())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        User creador = userRepository.findById(dto.getIdLider())
                .orElseThrow(() -> new RuntimeException("Usuario creador no encontrado"));

        Ubicacion ubicacion = ubicacionRepository.findById(dto.getIdUbicacion())
                .orElseThrow(() -> new RuntimeException("Ubicación no encontrada"));

        if (creador.getEscuela() != null &&
            !creador.getEscuela().getIdEscuela().equals(escuela.getIdEscuela())) {
            throw new RuntimeException("El líder no pertenece a la escuela seleccionada");
        }

        // Mapear DTO -> Entity (mapper espera entidades ya cargadas)
        Entrenamiento ent = mapper.toEntity(dto, escuela, categoria, creador, ubicacion);

        // Guardar
        ent = entrenamientoRepository.save(ent);
        return mapper.toDTO(ent);
    }

    // ============================================================
    // 2. ACTUALIZAR ENTRENAMIENTO
    // ============================================================
    public EntrenamientoDTO actualizarEntrenamiento(Integer id, EntrenamientoDTO dto) {

        Entrenamiento ent = entrenamientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entrenamiento no encontrado"));

        // Traer las entidades que se van a actualizar
        Escuela escuela = escuelaRepository.findById(dto.getIdEscuela())
                .orElseThrow(() -> new RuntimeException("Escuela no encontrada"));

        Categoria categoria = categoriaRepository.findById(dto.getIdCategoria())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        User lider = userRepository.findById(dto.getIdLider())
                .orElseThrow(() -> new RuntimeException("Líder no encontrado"));

        Ubicacion ubicacion = ubicacionRepository.findById(dto.getIdUbicacion())
                .orElseThrow(() -> new RuntimeException("Ubicación no encontrada"));

        // Actualizamos campos simples con parseo de fecha/hora
        ent.setTitulo(dto.getTitulo());
        ent.setDescripcion(dto.getDescripcion());

        if (dto.getFecha() != null && !dto.getFecha().isBlank()) {
            ent.setFecha(LocalDate.parse(dto.getFecha()));
        }

        if (dto.getHoraInicio() != null && !dto.getHoraInicio().isBlank()) {
            ent.setHoraInicio(LocalTime.parse(dto.getHoraInicio()));
        }

        if (dto.getHoraFin() != null && !dto.getHoraFin().isBlank()) {
            ent.setHoraFin(LocalTime.parse(dto.getHoraFin()));
        }

        ent.setLugar(dto.getLugar());

        // Relaciones
        ent.setEscuela(escuela);
        ent.setCategoria(categoria);
        ent.setCreador(lider);
        ent.setUbicacion(ubicacion);

        // Guardar cambios
        ent = entrenamientoRepository.save(ent);

        return mapper.toDTO(ent);
    }

    // ============================================================
    // 3. ELIMINAR ENTRENAMIENTO
    // ============================================================
    public void eliminarEntrenamiento(Integer id) {
        if (!entrenamientoRepository.existsById(id)) {
            throw new RuntimeException("Entrenamiento no encontrado");
        }
        entrenamientoRepository.deleteById(id);
    }

    // ============================================================
    // 4. FILTRO
    // ============================================================
    public List<EntrenamientoDTO> filtrar(String titulo, String fechaStr,
            Integer idCategoria, Integer idEscuela, Integer idUbicacion) {

        LocalDate fecha = null;
        if (fechaStr != null && !fechaStr.isBlank()) {
            fecha = LocalDate.parse(fechaStr);
        }

        List<Entrenamiento> list = entrenamientoRepository.filtrar(
                (titulo == null || titulo.isBlank()) ? null : titulo,
                fecha,
                idCategoria,
                idEscuela,
                idUbicacion
        );

        return list.stream().map(mapper::toDTO).collect(Collectors.toList());
    }

    // ============================================================
    // 5. OBTENER POR ID
    // ============================================================
    public EntrenamientoDTO obtenerPorId(Integer id) {
        return entrenamientoRepository.findById(id)
                .map(mapper::toDTO)
                .orElse(null);
    }
}
