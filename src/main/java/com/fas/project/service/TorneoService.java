package com.fas.project.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fas.project.dto.torneo.InscripcionDTO;
import com.fas.project.dto.torneo.TorneoCreateDTO;
import com.fas.project.dto.torneo.TorneoDTO;
import com.fas.project.model.Categoria;
import com.fas.project.model.Escuela;
import com.fas.project.model.InscripcionTorneo;
import com.fas.project.model.Jugador;
import com.fas.project.model.Lider;
import com.fas.project.model.Torneo;
import com.fas.project.model.Ubicacion;
import com.fas.project.model.User;
import com.fas.project.repository.CategoriaRepository;
import com.fas.project.repository.InscripcionTorneoRepository;
import com.fas.project.repository.TorneoRepository;
import com.fas.project.repository.UbicacionRepository;
import com.fas.project.repository.UserRepository;

@Service
public class TorneoService {
    
    private final TorneoRepository torneoRepository;
    private final InscripcionTorneoRepository inscripcionRepository;
    private final CategoriaRepository categoriaRepository;
    private final UbicacionRepository ubicacionRepository;
    private final UserRepository userRepository;
    
    public TorneoService(TorneoRepository torneoRepository,
                        InscripcionTorneoRepository inscripcionRepository,
                        CategoriaRepository categoriaRepository,
                        UbicacionRepository ubicacionRepository,
                        UserRepository userRepository) {
        this.torneoRepository = torneoRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.categoriaRepository = categoriaRepository;
        this.ubicacionRepository = ubicacionRepository;
        this.userRepository = userRepository;
    }
    
    @Transactional
    public TorneoDTO crearTorneo(TorneoCreateDTO dto) {
        // Validaciones
        if (dto.getFechaFin().isBefore(dto.getFechaInicio())) {
            throw new RuntimeException("La fecha de fin debe ser posterior a la fecha de inicio");
        }
        
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
            .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        
        Ubicacion ubicacion = ubicacionRepository.findById(dto.getUbicacionId())
            .orElseThrow(() -> new RuntimeException("Ubicación no encontrada"));
        
        Torneo torneo = Torneo.builder()
            .nombre(dto.getNombre())
            .fechaInicio(dto.getFechaInicio())
            .fechaFin(dto.getFechaFin())
            .descripcion(dto.getDescripcion())
            .cupoMaximo(dto.getCupoMaximo())
            .cuposDisponibles(dto.getCupoMaximo())
            .estado(Torneo.EstadoTorneo.PROXIMO)
            .categoria(categoria)
            .ubicacion(ubicacion)
            .build();
        
        torneo = torneoRepository.save(torneo);
        return convertirADTO(torneo);
    }
    
    @Transactional
    public TorneoDTO actualizarTorneo(Integer id, TorneoCreateDTO dto) {
        Torneo torneo = torneoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Torneo no encontrado"));
        
        if (torneo.getEstado() == Torneo.EstadoTorneo.FINALIZADO) {
            throw new RuntimeException("No se puede modificar un torneo finalizado");
        }
        
        if (dto.getFechaFin().isBefore(dto.getFechaInicio())) {
            throw new RuntimeException("La fecha de fin debe ser posterior a la fecha de inicio");
        }
        
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
            .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        
        Ubicacion ubicacion = ubicacionRepository.findById(dto.getUbicacionId())
            .orElseThrow(() -> new RuntimeException("Ubicación no encontrada"));
        
        torneo.setNombre(dto.getNombre());
        torneo.setFechaInicio(dto.getFechaInicio());
        torneo.setFechaFin(dto.getFechaFin());
        torneo.setDescripcion(dto.getDescripcion());
        
        // Actualizar cupos solo si no hay inscritos o si aumenta el cupo
        int diferenciaCupos = dto.getCupoMaximo() - torneo.getCupoMaximo();
        torneo.setCupoMaximo(dto.getCupoMaximo());
        torneo.setCuposDisponibles(torneo.getCuposDisponibles() + diferenciaCupos);
        
        torneo.setCategoria(categoria);
        torneo.setUbicacion(ubicacion);
        
        torneo = torneoRepository.save(torneo);
        return convertirADTO(torneo);
    }
    
    @Transactional
    public void eliminarTorneo(Integer id) {
        Torneo torneo = torneoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Torneo no encontrado"));
        
        if (torneo.getEstado() == Torneo.EstadoTorneo.EN_CURSO) {
            throw new RuntimeException("No se puede eliminar un torneo en curso");
        }
        
        torneoRepository.delete(torneo);
    }
    
    public TorneoDTO obtenerTorneoPorId(Integer id) {
        Torneo torneo = torneoRepository.findByIdWithDetails(id)
            .orElseThrow(() -> new RuntimeException("Torneo no encontrado"));
        return convertirADTO(torneo);
    }
    
    public List<TorneoDTO> listarTorneos(String nombre, String estado) {
        Torneo.EstadoTorneo estadoEnum = null;
        if (estado != null && !estado.isEmpty()) {
            try {
                estadoEnum = Torneo.EstadoTorneo.valueOf(estado);
            } catch (IllegalArgumentException e) {
                // Ignorar estado inválido
            }
        }
        
        return torneoRepository.buscarPorFiltros(nombre, estadoEnum)
            .stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }
    
    public List<TorneoDTO> listarTorneosDisponibles() {
        return torneoRepository.findTorneosDisponiblesParaInscripcion()
            .stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public InscripcionDTO inscribirEscuela(Integer torneoId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = ((UserDetails) auth.getPrincipal()).getUsername();
        
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        if (!(user instanceof Lider)) {
            throw new RuntimeException("Solo los líderes pueden inscribir escuelas");
        }
        
        Lider lider = (Lider) user;
        Escuela escuela = lider.getEscuela();
        
        if (escuela == null) {
            throw new RuntimeException("El líder no tiene una escuela asignada");
        }
        
        Torneo torneo = torneoRepository.findById(torneoId)
            .orElseThrow(() -> new RuntimeException("Torneo no encontrado"));
        
        if (!torneo.puedeInscribirse()) {
            throw new RuntimeException("El torneo no está disponible para inscripciones");
        }
        
        if (inscripcionRepository.existsByTorneoAndEscuelaAndEstado(
                torneo, escuela, InscripcionTorneo.EstadoInscripcion.ACTIVA)) {
            throw new RuntimeException("La escuela ya está inscrita en este torneo");
        }
        
        InscripcionTorneo inscripcion = InscripcionTorneo.builder()
            .torneo(torneo)
            .escuela(escuela)
            .lider(lider)
            .fechaInscripcion(LocalDateTime.now())
            .estado(InscripcionTorneo.EstadoInscripcion.ACTIVA)
            .build();
        
        torneo.setCuposDisponibles(torneo.getCuposDisponibles() - 1);
        
        inscripcion = inscripcionRepository.save(inscripcion);
        torneoRepository.save(torneo);
        
        return convertirInscripcionADTO(inscripcion);
    }
    
    @Transactional
    public void cancelarInscripcion(Integer torneoId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = ((UserDetails) auth.getPrincipal()).getUsername();
        
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        if (!(user instanceof Lider)) {
            throw new RuntimeException("Solo los líderes pueden cancelar inscripciones");
        }
        
        Lider lider = (Lider) user;
        Escuela escuela = lider.getEscuela();
        
        if (escuela == null) {
            throw new RuntimeException("El líder no tiene una escuela asignada");
        }
        
        Torneo torneo = torneoRepository.findById(torneoId)
            .orElseThrow(() -> new RuntimeException("Torneo no encontrado"));
        
        if (!torneo.puedeCancelarInscripcion()) {
            throw new RuntimeException("Ya no es posible cancelar la inscripción. " +
                "La cancelación debe hacerse al menos 3 días antes del inicio del torneo.");
        }
        
        InscripcionTorneo inscripcion = inscripcionRepository
            .findInscripcionActiva(torneoId, escuela.getIdEscuela())
            .orElseThrow(() -> new RuntimeException("No se encontró una inscripción activa"));
        
        inscripcion.setEstado(InscripcionTorneo.EstadoInscripcion.CANCELADA);
        torneo.setCuposDisponibles(torneo.getCuposDisponibles() + 1);
        
        inscripcionRepository.save(inscripcion);
        torneoRepository.save(torneo);
    }
    
    @Transactional
    public void desinscribirEscuela(Integer inscripcionId) {
        InscripcionTorneo inscripcion = inscripcionRepository.findById(inscripcionId)
            .orElseThrow(() -> new RuntimeException("Inscripción no encontrada"));
        
        Torneo torneo = inscripcion.getTorneo();
        
        inscripcion.setEstado(InscripcionTorneo.EstadoInscripcion.CANCELADA);
        torneo.setCuposDisponibles(torneo.getCuposDisponibles() + 1);
        
        inscripcionRepository.save(inscripcion);
        torneoRepository.save(torneo);
    }
    
    public List<InscripcionDTO> listarInscripcionesTorneo(Integer torneoId) {
        return inscripcionRepository.findByTorneoIdWithDetails(torneoId)
            .stream()
            .map(this::convertirInscripcionADTO)
            .collect(Collectors.toList());
    }
    
    public List<TorneoDTO> listarTorneosDelLider() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = ((UserDetails) auth.getPrincipal()).getUsername();
        
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        if (!(user instanceof Lider)) {
            throw new RuntimeException("Solo los líderes pueden ver sus torneos");
        }
        
        Lider lider = (Lider) user;
        
        return inscripcionRepository.findByLiderWithTorneoDetails(lider)
            .stream()
            .map(inscripcion -> convertirADTO(inscripcion.getTorneo()))
            .collect(Collectors.toList());
    }
    
    public List<TorneoDTO> listarTorneosDelJugador() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = ((UserDetails) auth.getPrincipal()).getUsername();
        
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        if (!(user instanceof Jugador)) {
            throw new RuntimeException("Solo los jugadores pueden ver sus torneos");
        }
        
        Jugador jugador = (Jugador) user;
        Escuela escuela = jugador.getEscuela();
        
        if (escuela == null) {
            return List.of();
        }
        
        return inscripcionRepository.findByEscuelaWithTorneoDetails(escuela)
            .stream()
            .map(inscripcion -> convertirADTO(inscripcion.getTorneo()))
            .collect(Collectors.toList());
    }
    
    private TorneoDTO convertirADTO(Torneo torneo) {
        int totalInscritos = (int) inscripcionRepository
            .findByTorneoIdWithDetails(torneo.getIdTorneo())
            .stream()
            .filter(i -> i.getEstado() == InscripcionTorneo.EstadoInscripcion.ACTIVA)
            .count();
        
        return TorneoDTO.builder()
            .idTorneo(torneo.getIdTorneo())
            .nombre(torneo.getNombre())
            .fechaInicio(torneo.getFechaInicio())
            .fechaFin(torneo.getFechaFin())
            .descripcion(torneo.getDescripcion())
            .cupoMaximo(torneo.getCupoMaximo())
            .cuposDisponibles(torneo.getCuposDisponibles())
            .estado(torneo.getEstado().getDisplayName())
            .categoriaNombre(torneo.getCategoria().getNombre())
            .categoriaId(torneo.getCategoria().getIdCategoria())
            .ubicacionLocalidad(torneo.getUbicacion().getLocalidad())
            .ubicacionBarrio(torneo.getUbicacion().getBarrio())
            .ubicacionId(torneo.getUbicacion().getIdUbicacion())
            .totalInscritos(totalInscritos)
            .puedeInscribirse(torneo.puedeInscribirse())
            .puedeCancelarInscripcion(torneo.puedeCancelarInscripcion())
            .build();
    }
    
    private InscripcionDTO convertirInscripcionADTO(InscripcionTorneo inscripcion) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        return InscripcionDTO.builder()
            .idInscripcion(inscripcion.getIdInscripcion())
            .torneoId(inscripcion.getTorneo().getIdTorneo())
            .torneoNombre(inscripcion.getTorneo().getNombre())
            .escuelaId(inscripcion.getEscuela().getIdEscuela())
            .escuelaNombre(inscripcion.getEscuela().getNombre())
            .liderId(inscripcion.getLider().getIdUsuario())
            .liderNombre(inscripcion.getLider().getNombres() + " " + 
                        inscripcion.getLider().getApellidos())
            .fechaInscripcion(inscripcion.getFechaInscripcion().format(formatter))
            .estado(inscripcion.getEstado().getDisplayName())
            .build();
    }
}