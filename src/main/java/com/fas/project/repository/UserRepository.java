package com.fas.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fas.project.model.Jugador;
import com.fas.project.model.Lider;
import com.fas.project.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
        Optional<User> findByEmail(String email);

        boolean existsByEmail(String email);

        boolean existsByNumDocumento(Long numDocumento);

        @Query("SELECT u FROM User u WHERE TYPE(u) = Lider")
        List<Lider> findAllLideres();

        @Query("SELECT l FROM Lider l WHERE " +
                        "(:nombreCompleto IS NULL OR LOWER(CONCAT(l.nombres, ' ', l.apellidos)) LIKE LOWER(CONCAT('%', :nombreCompleto, '%'))) AND "
                        +
                        "(:numDocumento IS NULL OR CAST(l.numDocumento AS string) LIKE CONCAT('%', :numDocumento, '%'))")
        List<Lider> findLideresByFiltros(@Param("nombreCompleto") String nombreCompleto,
                        @Param("numDocumento") String numDocumento);

        @Query("SELECT j FROM Jugador j " +
                        "WHERE j.escuela.idEscuela = :idEscuela " +
                        "AND (:nombre IS NULL OR CONCAT(j.nombres, ' ', j.apellidos) LIKE :nombre) " +
                        "AND (:categoriaId IS NULL OR j.categoria.idCategoria = :categoriaId) " +
                        "AND (:posicion IS NULL OR j.posicion = :posicion)")
        List<Jugador> obtenerJugadoresPorEscuela(@Param("idEscuela") Integer idEscuela,
                        @Param("nombre") String nombre,
                        @Param("categoriaId") Integer categoriaId,
                        @Param("posicion") String posicion);
}