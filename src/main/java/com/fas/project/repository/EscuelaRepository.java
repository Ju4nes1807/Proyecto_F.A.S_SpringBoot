package com.fas.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fas.project.model.Escuela;

import java.util.List;
import java.util.Optional;

import com.fas.project.model.Lider;

public interface EscuelaRepository extends JpaRepository<Escuela, Integer> {
    boolean existsByEmail(String email);

    List<Escuela> findByNombre(String nombre);

    Optional<Escuela> findByLideresContaining(Lider lider);
}
