package com.fas.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fas.project.model.Cancha;

public interface CanchaRepository extends JpaRepository<Cancha, Integer> {

    @Query("SELECT c FROM Cancha c JOIN FETCH c.ubicacion")
    List<Cancha> findAllWithUbicacion();

    @Query("SELECT c FROM Cancha c JOIN FETCH c.ubicacion WHERE c.idCancha = :id")
    Optional<Cancha> findByIdWithUbicacion(Integer id);

    @Query("SELECT DISTINCT c FROM Cancha c JOIN FETCH c.ubicacion u " +
            "WHERE (:nombre IS NULL OR LOWER(c.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
            "AND (:ubicacionId IS NULL OR u.idUbicacion = :ubicacionId)")
    List<Cancha> buscarPorFiltros(@Param("nombre") String nombre,
            @Param("ubicacionId") Integer ubicacionId);

}
