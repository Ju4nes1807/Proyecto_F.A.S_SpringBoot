package com.fas.project.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.fas.project.dto.categoria.CategoriaDTO;
import com.fas.project.mapper.MapperCategoria;
import com.fas.project.model.Categoria;
import com.fas.project.model.Escuela;
import com.fas.project.model.Lider;
import com.fas.project.model.User;
import com.fas.project.repository.CategoriaRepository;

@Service
public class CategoriaService {
    private final CategoriaRepository categoriaRepository;
    private final MapperCategoria mapperCategoria;
    private final UserService userService;

    public CategoriaService(CategoriaRepository categoriaRepository, MapperCategoria mapperCategoria,
            UserService userService) {
        this.categoriaRepository = categoriaRepository;
        this.mapperCategoria = mapperCategoria;
        this.userService = userService;
    }

    public Categoria crearCategoria(CategoriaDTO categoriaDTO, String username) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = ((UserDetails) auth.getPrincipal()).getUsername();

        User user = userService.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        if (!(user instanceof Lider lider)) {
            throw new RuntimeException("Solo los líderes pueden crear categorías");
        }

        Escuela escuela = lider.getEscuela();
        if (escuela == null) {
            throw new RuntimeException("El líder no está asociado a ninguna escuela");
        }

        Categoria categoria = mapperCategoria.toEntity(categoriaDTO);
        categoria.setEscuela(escuela);

        return categoriaRepository.save(categoria);
    }

    public List<CategoriaDTO> listarCategoriasDelLider() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = ((UserDetails) auth.getPrincipal()).getUsername();

        User user = userService.findByEmail(email);
        if (user == null || !(user instanceof Lider lider)) {
            throw new RuntimeException("Usuario no encontrado o no es un líder");
        }

        Escuela escuela = lider.getEscuela();
        if (escuela == null) {
            throw new RuntimeException("El líder no está asociado a ninguna escuela");
        }

        return categoriaRepository.findByEscuela(escuela).stream()
                .map(mapperCategoria::toDTO)
                .collect(Collectors.toList());
    }

    public Categoria obtenerCategoriaPorId(Integer idCategoria) {
        return categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + idCategoria));
    }

    public Categoria actualizarCategoria(Integer idCategoria, CategoriaDTO categoriaDTO) {
        Categoria categoriaExistente = obtenerCategoriaPorId(idCategoria);
        mapperCategoria.updateEntityFromDTO(categoriaDTO, categoriaExistente);
        return categoriaRepository.save(categoriaExistente);
    }

    public void eliminarCategoria(Integer idCategoria) {
        Categoria categoria = obtenerCategoriaPorId(idCategoria);
        categoriaRepository.delete(categoria);
    }
}
