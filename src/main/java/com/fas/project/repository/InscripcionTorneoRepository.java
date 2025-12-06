package com.fas.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fas.project.model.Escuela;
import com.fas.project.model.InscripcionTorneo;
import com.fas.project.model.Lider;
import com.fas.project.model.Torneo;

@Repository
public interface InscripcionTorneoRepository extends JpaRepository<InscripcionTorneo, Integer> {
    
    boolean existsByTorneoAndEscuelaAndEstado(
        Torneo torneo, 
        Escuela escuela, 
        InscripcionTorneo.EstadoInscripcion estado
    );
    
    @Query("SELECT i FROM InscripcionTorneo i " +
           "LEFT JOIN FETCH i.torneo t " +
           "LEFT JOIN FETCH i.escuela e " +
           "LEFT JOIN FETCH i.lider l " +
           "WHERE i.torneo.idTorneo = :torneoId AND i.escuela.idEscuela = :escuelaId " +
           "AND i.estado = 'ACTIVA'")
    Optional<InscripcionTorneo> findInscripcionActiva(
        @Param("torneoId") Integer torneoId, 
        @Param("escuelaId") Integer escuelaId
    );
    
    @Query("SELECT i FROM InscripcionTorneo i " +
           "LEFT JOIN FETCH i.torneo " +
           "LEFT JOIN FETCH i.escuela " +
           "LEFT JOIN FETCH i.lider " +
           "WHERE i.torneo.idTorneo = :torneoId")
    List<InscripcionTorneo> findByTorneoIdWithDetails(@Param("torneoId") Integer torneoId);
    
    @Query("SELECT i FROM InscripcionTorneo i " +
           "LEFT JOIN FETCH i.torneo " +
           "WHERE i.lider = :lider AND i.estado = 'ACTIVA'")
    List<InscripcionTorneo> findByLiderWithTorneoDetails(@Param("lider") Lider lider);
    
    @Query("SELECT i FROM InscripcionTorneo i " +
           "LEFT JOIN FETCH i.torneo " +
           "WHERE i.escuela = :escuela AND i.estado = 'ACTIVA'")
    List<InscripcionTorneo> findByEscuelaWithTorneoDetails(@Param("escuela") Escuela escuela);
}