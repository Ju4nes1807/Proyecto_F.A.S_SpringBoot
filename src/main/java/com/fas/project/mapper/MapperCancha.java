package com.fas.project.mapper;

import org.springframework.stereotype.Component;

import com.fas.project.dto.cancha.CanchaDTO;
import com.fas.project.model.Cancha;
import com.fas.project.model.Ubicacion;

@Component
public class MapperCancha {
    public Cancha toEntity(CanchaDTO dto, Ubicacion ubicacion) {
        Cancha entity = new Cancha();
        entity.setIdCancha(dto.getIdCancha());
        entity.setNombre(dto.getNombre());
        entity.setTipoSuperficie(dto.getTipoSuperficie());
        entity.setDireccion(dto.getDireccion());
        entity.setEstadoDisponible(dto.isEstadoDisponible());
        entity.setHorarioDisponibilidad(dto.getHorarioDisponibilidad());
        entity.setDescripcion(dto.getDescripcion());
        entity.setUbicacion(ubicacion);
        return entity;
    }

    public void updateEntityFromDTO(CanchaDTO dto, Cancha entity, Ubicacion nuevaUbicacion) {
        entity.setNombre(dto.getNombre());
        entity.setTipoSuperficie(dto.getTipoSuperficie());
        entity.setDireccion(dto.getDireccion());
        entity.setEstadoDisponible(dto.isEstadoDisponible());
        entity.setHorarioDisponibilidad(dto.getHorarioDisponibilidad());
        entity.setDescripcion(dto.getDescripcion());
        entity.setUbicacion(nuevaUbicacion);
    }

    public CanchaDTO toDTO(Cancha entity) {
        CanchaDTO dto = new CanchaDTO();
        dto.setIdCancha(entity.getIdCancha());
        dto.setNombre(entity.getNombre());
        dto.setTipoSuperficie(entity.getTipoSuperficie());
        dto.setDireccion(entity.getDireccion());
        dto.setEstadoDisponible(entity.isEstadoDisponible());
        dto.setHorarioDisponibilidad(entity.getHorarioDisponibilidad());
        dto.setDescripcion(entity.getDescripcion());
        dto.setUbicacionId(entity.getUbicacion().getIdUbicacion());
        dto.setUbicacion(entity.getUbicacion());
        return dto;
    }
}
