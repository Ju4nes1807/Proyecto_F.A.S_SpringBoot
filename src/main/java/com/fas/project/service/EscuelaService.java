package com.fas.project.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fas.project.dto.escuela.EscuelaDTO;
import com.fas.project.mapper.MapperEscuela;
import com.fas.project.model.Escuela;
import com.fas.project.model.Lider;
import com.fas.project.model.Ubicacion;
import com.fas.project.model.User;
import com.fas.project.repository.EscuelaRepository;
import com.fas.project.repository.UbicacionRepository;
import com.fas.project.repository.UserRepository;

@Service
public class EscuelaService {
    private final UbicacionRepository ubicacionRepository;
    private final EscuelaRepository escuelaRepository;
    private final MapperEscuela mapperEscuela;
    private final UserRepository userRepository;

    public EscuelaService(UbicacionRepository ubicacionRepository, EscuelaRepository escuelaRepository,
            MapperEscuela mapperEscuela, UserRepository userRepository) {
        this.ubicacionRepository = ubicacionRepository;
        this.escuelaRepository = escuelaRepository;
        this.mapperEscuela = mapperEscuela;
        this.userRepository = userRepository;
    }

    public void registrarEscuela(EscuelaDTO escuelaDTO) {
        Escuela escuela = mapperEscuela.toEntity(escuelaDTO);

        @SuppressWarnings("null")
        Set<Ubicacion> ubicaciones = new HashSet<>(ubicacionRepository.findAllById(escuelaDTO.getUbicacionIds()));
        escuela.setUbicaciones(ubicaciones);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = ((UserDetails) auth.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        escuela.getLideres().add((Lider) user);
        ((Lider) user).setEscuela(escuela);

        escuelaRepository.save(escuela);
    }

    public List<Ubicacion> obtenerUbicaciones() {
        return ubicacionRepository.findAll();
    }

    public Escuela obtenerEscuelaPorLider(Lider lider) {
        if (!(lider instanceof Lider)) {
            throw new IllegalArgumentException("El usuario proporcionado no es un líder.");
        }
        return escuelaRepository.findByLideresContaining(lider)
                .orElse(null);
    }

    public List<Escuela> obtenerTodasLasEscuelas() {
        return escuelaRepository.findAllWithUbicacionesAndLideres();
    }

    public List<Escuela> buscarEscuelas(String nombre, String localidad) {
        return escuelaRepository.buscarPorNombreYLocalidad(
                nombre != null && !nombre.isBlank() ? nombre : null,
                localidad != null && !localidad.isBlank() ? localidad : null);
    }

    public EscuelaDTO obtenerPorId(Integer idEscuela) {
        @SuppressWarnings("null")
        Escuela escuela = escuelaRepository.findById(idEscuela)
                .orElseThrow(() -> new IllegalArgumentException("Escuela no encontrada con ID: " + idEscuela));
        return mapperEscuela.toDTO(escuela);
    }

    public Escuela editarEscuela(Integer idEscuela, EscuelaDTO escuelaDTO) {
        Escuela escuela = escuelaRepository.findById(idEscuela)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Escuela no encontrada con ID: " + idEscuela));

        mapperEscuela.updateEntityFromDTO(escuelaDTO, escuela);

        @SuppressWarnings("null")
        Set<Ubicacion> ubicaciones = new HashSet<>(ubicacionRepository.findAllById(escuelaDTO.getUbicacionIds()));
        escuela.setUbicaciones(ubicaciones);

        return escuelaRepository.save(escuela);
    }

    @Transactional
    public void eliminarEscuela(Integer idEscuela) {
        Escuela escuela = escuelaRepository.findById(idEscuela)
                .orElseThrow(() -> new IllegalArgumentException("Escuela no encontrada con ID: " + idEscuela));

        // Desasociar líderes si es necesario
        if (!escuela.getLideres().isEmpty()) {
            for (Lider lider : escuela.getLideres()) {
                lider.setEscuela(null);
            }
            escuela.getLideres().clear();
        }

        // Limpiar ubicaciones si quieres
        escuela.getUbicaciones().clear();

        // Eliminar escuela (jugadores se eliminan automáticamente)
        escuelaRepository.delete(escuela);
    }

}
