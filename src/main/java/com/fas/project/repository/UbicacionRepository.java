package com.fas.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fas.project.model.Ubicacion;

public interface UbicacionRepository extends JpaRepository<Ubicacion, Integer> {
    Ubicacion findByLocalidadAndBarrio(String localidad, String barrio);
}
