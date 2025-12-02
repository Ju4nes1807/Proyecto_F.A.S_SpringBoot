package com.fas.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fas.project.model.Escuela;

import java.util.List;
import java.util.Optional;

import com.fas.project.model.Lider;

public interface EscuelaRepository extends JpaRepository<Escuela, Integer> {
    boolean existsByEmail(String email);

    List<Escuela> findByNombre(String nombre);

    Optional<Escuela> findByLideresContaining(Lider lider);

    @Query("SELECT e FROM Escuela e LEFT JOIN FETCH e.ubicaciones u LEFT JOIN FETCH e.lideres l")
    List<Escuela> findAllWithUbicacionesAndLideres();

    @Query("""
                SELECT DISTINCT e
                FROM Escuela e
                LEFT JOIN e.ubicaciones u
                WHERE (:nombre IS NULL OR :nombre = '' OR LOWER(e.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))
                AND (:localidad IS NULL OR :localidad = '' OR LOWER(u.localidad) = LOWER(:localidad))
            """)
    List<Escuela> buscarPorNombreYLocalidad(
            @Param("nombre") String nombre,
            @Param("localidad") String localidad);
}
