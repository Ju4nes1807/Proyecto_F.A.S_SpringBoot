package com.fas.project.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fas.project.model.Torneo;

@Repository
public interface TorneoRepository extends JpaRepository<Torneo, Integer> {
    
    @Query("SELECT t FROM Torneo t " +
           "LEFT JOIN FETCH t.categoria " +
           "LEFT JOIN FETCH t.ubicacion " +
           "WHERE t.idTorneo = :id")
    Optional<Torneo> findByIdWithDetails(@Param("id") Integer id);
    
    @Query("SELECT DISTINCT t FROM Torneo t " +
           "LEFT JOIN FETCH t.categoria " +
           "LEFT JOIN FETCH t.ubicacion " +
           "WHERE (:nombre IS NULL OR LOWER(t.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
           "AND (:estado IS NULL OR t.estado = :estado)")
    List<Torneo> buscarPorFiltros(
        @Param("nombre") String nombre, 
        @Param("estado") Torneo.EstadoTorneo estado
    );
    
    @Query("SELECT t FROM Torneo t " +
           "LEFT JOIN FETCH t.categoria " +
           "LEFT JOIN FETCH t.ubicacion " +
           "WHERE t.estado = 'PROXIMO' " +
           "AND t.cuposDisponibles > 0 " +
           "AND t.fechaInicio > :today")
    List<Torneo> findTorneosDisponiblesParaInscripcion(@Param("today") LocalDate today);
    
    default List<Torneo> findTorneosDisponiblesParaInscripcion() {
        return findTorneosDisponiblesParaInscripcion(LocalDate.now());
    }
}