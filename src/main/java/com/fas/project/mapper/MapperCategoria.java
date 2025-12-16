package com.fas.project.mapper;

import org.springframework.stereotype.Component;

import com.fas.project.dto.categoria.CategoriaDTO;
import com.fas.project.dto.escuela.EscuelaDTO;
import com.fas.project.model.Categoria;

@Component
public class MapperCategoria {
    private final MapperEscuela mapperEscuela;

    public MapperCategoria(MapperEscuela mapperEscuela) {
        this.mapperEscuela = mapperEscuela;
    }

    public Categoria toEntity(CategoriaDTO dto) {
        Categoria entity = new Categoria();
        entity.setIdCategoria(dto.getIdCategoria());
        entity.setNombre(dto.getNombre());
        entity.setRangoEdad(dto.getRangoEdad());
        return entity;
    }

    public void updateEntityFromDTO(CategoriaDTO dto, Categoria entity) {
        entity.setNombre(dto.getNombre());
        entity.setRangoEdad(dto.getRangoEdad());
    }

    public CategoriaDTO toDTO(Categoria entity) {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setIdCategoria(entity.getIdCategoria());
        dto.setNombre(entity.getNombre());
        dto.setRangoEdad(entity.getRangoEdad());
        EscuelaDTO escuelaDTO = mapperEscuela.toDTO(entity.getEscuela());
        dto.setEscuela(escuelaDTO);
        return dto;
    }
}