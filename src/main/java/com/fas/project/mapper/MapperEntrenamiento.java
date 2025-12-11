package com.fas.project.mapper;

import org.springframework.stereotype.Component;

import com.fas.project.dto.entrenamiento.EntrenamientoDTO;
import com.fas.project.model.Entrenamiento;
import com.fas.project.model.Categoria;
import com.fas.project.model.Escuela;
import com.fas.project.model.User;
import com.fas.project.model.Ubicacion;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class MapperEntrenamiento {

    /**
     * Convierte DTO -> Entity
     * Recibe las entidades relacionadas ya cargadas por el Service (escuela, categoria, creador, ubicacion).
     */
    public Entrenamiento toEntity(EntrenamientoDTO dto,
                                  Escuela escuela,
                                  Categoria categoria,
                                  User creador,
                                  Ubicacion ubicacion) {

        Entrenamiento entity = new Entrenamiento();

        if (dto.getId() != null) {
            entity.setIdEntrenamiento(dto.getId());
        }

        entity.setTitulo(dto.getTitulo());
        entity.setDescripcion(dto.getDescripcion());

        // parseo de fecha/hora (asumo formato yyyy-MM-dd y HH:mm)
        if (dto.getFecha() != null && !dto.getFecha().isBlank()) {
            entity.setFecha(LocalDate.parse(dto.getFecha()));
        }
        if (dto.getHoraInicio() != null && !dto.getHoraInicio().isBlank()) {
            entity.setHoraInicio(LocalTime.parse(dto.getHoraInicio()));
        }
        if (dto.getHoraFin() != null && !dto.getHoraFin().isBlank()) {
            entity.setHoraFin(LocalTime.parse(dto.getHoraFin()));
        }

        // lugar = nombre de la cancha (string)
        entity.setLugar(dto.getLugar());

        // relaciones (se pasan ya cargadas desde el service)
        entity.setEscuela(escuela);
        entity.setCategoria(categoria);
        entity.setCreador(creador);

        // añadimos la relación ubicacion (asegúrate de que Entrenamiento tiene ese campo)
        entity.setUbicacion(ubicacion);

        return entity;
    }

    /**
     * Convierte Entity -> DTO (para respuestas). Llena también nombres para mostrar.
     */
    public EntrenamientoDTO toDTO(Entrenamiento entity) {
        if (entity == null) return null;

        EntrenamientoDTO dto = new EntrenamientoDTO();

        dto.setId(entity.getIdEntrenamiento());
        dto.setTitulo(entity.getTitulo());
        dto.setDescripcion(entity.getDescripcion());

        if (entity.getFecha() != null) dto.setFecha(entity.getFecha().toString());
        if (entity.getHoraInicio() != null) dto.setHoraInicio(entity.getHoraInicio().toString());
        if (entity.getHoraFin() != null) dto.setHoraFin(entity.getHoraFin().toString());

        dto.setLugar(entity.getLugar());

        if (entity.getCategoria() != null) {
            dto.setIdCategoria(entity.getCategoria().getIdCategoria());
            dto.setCategoriaNombre(entity.getCategoria().getNombre());
        }

        if (entity.getEscuela() != null) {
            dto.setIdEscuela(entity.getEscuela().getIdEscuela());
            dto.setEscuelaNombre(entity.getEscuela().getNombre());
        }

        if (entity.getCreador() != null) {
            dto.setIdLider(entity.getCreador().getIdUsuario());
            dto.setNombreLider(entity.getCreador().getNombres() + " " + entity.getCreador().getApellidos());
        }

        if (entity.getUbicacion() != null) {
            dto.setIdUbicacion(entity.getUbicacion().getIdUbicacion());
            dto.setUbicacionNombre(entity.getUbicacion().getLocalidad() + " - " + entity.getUbicacion().getBarrio());
        }

        return dto;
    }
}