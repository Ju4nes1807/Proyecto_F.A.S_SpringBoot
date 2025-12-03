package com.fas.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fas.project.model.Escuela;
import com.fas.project.model.Jugador;

@Repository
public interface JugadorRepository extends JpaRepository<Jugador, Integer> {
    
    List<Jugador> findByEscuela(Escuela escuela);
    
    @Query("SELECT j FROM Jugador j WHERE j.email = :email")
    Optional<Jugador> findByEmail(@Param("email") String email);
    
    @Query("SELECT j FROM Jugador j WHERE j.numDocumento = :numDocumento")
    Optional<Jugador> findByNumDocumento(@Param("numDocumento") Long numDocumento);
    
    @Query("""
        SELECT DISTINCT j FROM Jugador j 
        LEFT JOIN FETCH j.categoria c 
        LEFT JOIN FETCH j.escuela e 
        WHERE j.escuela = :escuela 
        AND (:nombre IS NULL OR LOWER(CONCAT(j.nombres, ' ', j.apellidos)) LIKE LOWER(CONCAT('%', :nombre, '%')))
        AND (:categoriaId IS NULL OR c.idCategoria = :categoriaId)
        AND (:posicion IS NULL OR LOWER(j.posicion) LIKE LOWER(CONCAT('%', :posicion, '%')))
    """)
    List<Jugador> buscarPorFiltros(
        @Param("escuela") Escuela escuela,
        @Param("nombre") String nombre,
        @Param("categoriaId") Integer categoriaId,
        @Param("posicion") String posicion
    );
    
    boolean existsByNumeroCamisetaAndEscuela(Integer numeroCamiseta, Escuela escuela);
    
    boolean existsByNumeroCamisetaAndEscuelaAndIdUsuarioNot(Integer numeroCamiseta, Escuela escuela, Integer idUsuario);
}