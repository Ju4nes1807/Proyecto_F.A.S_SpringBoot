package com.fas.project.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fas.project.model.Entrenamiento;

public interface EntrenamientoRepository extends JpaRepository<Entrenamiento, Integer> {

    @Query("""
        SELECT e FROM Entrenamiento e
        WHERE (:titulo IS NULL OR LOWER(e.titulo) LIKE LOWER(CONCAT('%', :titulo, '%')))
          AND (:fecha IS NULL OR e.fecha = :fecha)
          AND (:idCategoria IS NULL OR e.categoria.idCategoria = :idCategoria)
          AND (:idEscuela IS NULL OR e.escuela.idEscuela = :idEscuela)
          AND (:idUbicacion IS NULL OR e.ubicacion.idUbicacion = :idUbicacion)
    """)
    List<Entrenamiento> filtrar(
            @Param("titulo") String titulo,
            @Param("fecha") LocalDate fecha,
            @Param("idCategoria") Integer idCategoria,
            @Param("idEscuela") Integer idEscuela,
            @Param("idUbicacion") Integer idUbicacion
    );

    // consulta rápida para listar por escuela
    List<Entrenamiento> findByEscuelaIdEscuela(Integer idEscuela);

    // listar por escuela y categoria
    List<Entrenamiento> findByEscuelaIdEscuelaAndCategoriaIdCategoria(Integer idEscuela, Integer idCategoria);
}
