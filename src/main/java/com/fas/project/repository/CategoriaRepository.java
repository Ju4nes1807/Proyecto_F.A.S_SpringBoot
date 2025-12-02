package com.fas.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fas.project.model.Categoria;
import com.fas.project.model.Escuela;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    Categoria findByNombre(String nombre);

    List<Categoria> findByEscuela(Escuela escuela);
}
