package com.fas.project.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fas.project.dto.cancha.CanchaDTO;
import com.fas.project.mapper.MapperCancha;
import com.fas.project.model.Cancha;
import com.fas.project.repository.CanchaRepository;
import com.fas.project.model.Ubicacion;
import com.fas.project.repository.UbicacionRepository;

@Service
public class CanchaService {
    private final CanchaRepository canchaRepository;
    private final MapperCancha mapperCancha;
    private final UbicacionRepository ubicacionRepository;

    public CanchaService(CanchaRepository canchaRepository, MapperCancha mapperCancha,
            UbicacionRepository ubicacionRepository) {
        this.canchaRepository = canchaRepository;
        this.mapperCancha = mapperCancha;
        this.ubicacionRepository = ubicacionRepository;
    }

    public Ubicacion obtenerUbicacionPorId(Integer id) {
        return ubicacionRepository.findById(id).orElse(null);
    }

    public CanchaDTO obtenerCanchaPorId(Integer id) {
        return canchaRepository.findByIdWithUbicacion(id)
                .map(mapperCancha::toDTO)
                .orElse(null);
    }

    public List<CanchaDTO> obtenerTodasLasCanchas() {
        return canchaRepository.findAllWithUbicacion().stream()
                .map(mapperCancha::toDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    public CanchaDTO crearCancha(CanchaDTO canchaDTO) {
        Ubicacion ubicacion = obtenerUbicacionPorId(canchaDTO.getUbicacionId());
        Cancha cancha = mapperCancha.toEntity(canchaDTO, ubicacion);
        Cancha canchaGuardada = canchaRepository.save(cancha);
        return mapperCancha.toDTO(canchaGuardada);
    }

    public CanchaDTO actualizarCancha(Integer id, CanchaDTO canchaDTO) {
        return canchaRepository.findByIdWithUbicacion(id)
                .map(existingCancha -> {
                    Ubicacion nuevaUbicacion = obtenerUbicacionPorId(canchaDTO.getUbicacionId());
                    mapperCancha.updateEntityFromDTO(canchaDTO, existingCancha, nuevaUbicacion);
                    Cancha canchaActualizada = canchaRepository.save(existingCancha);
                    return mapperCancha.toDTO(canchaActualizada);
                })
                .orElse(null);
    }

    public void eliminarCancha(Integer id) {
        canchaRepository.deleteById(id);
    }

    public List<CanchaDTO> buscarPorFiltros(String nombre, Integer ubicacionId) {
        return canchaRepository.buscarPorFiltros(nombre, ubicacionId)
                .stream()
                .map(mapperCancha::toDTO)
                .collect(Collectors.toList());
    }

}