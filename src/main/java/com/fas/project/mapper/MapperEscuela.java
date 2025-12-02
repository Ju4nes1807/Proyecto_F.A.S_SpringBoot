package com.fas.project.mapper;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.fas.project.dto.escuela.EscuelaDTO;
import com.fas.project.model.Escuela;
import com.fas.project.model.Ubicacion;

@Component
public class MapperEscuela {
    public Escuela toEntity(EscuelaDTO dto) {
        Escuela entity = new Escuela();
        entity.setIdEscuela(dto.getIdEscuela());
        entity.setNombre(dto.getNombre());
        entity.setTelefono(dto.getTelefono());
        entity.setEmail(dto.getEmail());

        return entity;
    }

    public void updateEntityFromDTO(EscuelaDTO dto, Escuela entity) {
        entity.setNombre(dto.getNombre());
        entity.setTelefono(dto.getTelefono());
        entity.setEmail(dto.getEmail());
    }

    public EscuelaDTO toDTO(Escuela entity) {
        EscuelaDTO dto = new EscuelaDTO();
        dto.setIdEscuela(entity.getIdEscuela());
        dto.setNombre(entity.getNombre());
        dto.setTelefono(entity.getTelefono());
        dto.setEmail(entity.getEmail());

        Set<Integer> ubicacionIds = entity.getUbicaciones().stream()
                .map(Ubicacion::getIdUbicacion)
                .collect(java.util.stream.Collectors.toSet());
        dto.setUbicacionIds(ubicacionIds);

        return dto;
    }
}
