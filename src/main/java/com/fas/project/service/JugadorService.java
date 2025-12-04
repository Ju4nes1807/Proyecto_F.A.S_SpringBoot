package com.fas.project.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fas.project.dto.categoria.CategoriaDTO;
import com.fas.project.dto.user.JugadorCreateDTO;
import com.fas.project.dto.user.JugadorDTO;
import com.fas.project.mapper.MapperCategoria;
import com.fas.project.model.Categoria;
import com.fas.project.model.Escuela;
import com.fas.project.model.Jugador;
import com.fas.project.model.Lider;
import com.fas.project.model.Rol;
import com.fas.project.model.User;
import com.fas.project.repository.CategoriaRepository;
import com.fas.project.repository.JugadorRepository;
import com.fas.project.repository.UserRepository;

@Service
public class JugadorService {

    private final JugadorRepository jugadorRepository;
    private final UserRepository userRepository;
    private final CategoriaRepository categoriaRepository;
    private final PasswordEncoder passwordEncoder;
    private final MapperCategoria mapperCategoria;

    public JugadorService(
            JugadorRepository jugadorRepository,
            UserRepository userRepository,
            CategoriaRepository categoriaRepository,
            PasswordEncoder passwordEncoder,
            MapperCategoria mapperCategoria) {
        this.jugadorRepository = jugadorRepository;
        this.userRepository = userRepository;
        this.categoriaRepository = categoriaRepository;
        this.passwordEncoder = passwordEncoder;
        this.mapperCategoria = mapperCategoria;
    }

    private Escuela obtenerEscuelaDelLiderActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = ((UserDetails) auth.getPrincipal()).getUsername();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!(user instanceof Lider)) {
            throw new RuntimeException("Solo los líderes pueden gestionar jugadores");
        }

        Lider lider = (Lider) user;
        Escuela escuela = lider.getEscuela();

        if (escuela == null) {
            throw new RuntimeException("El líder no está asociado a ninguna escuela");
        }

        return escuela;
    }

    public List<CategoriaDTO> obtenerCategoriasDelLider() {
        Escuela escuela = obtenerEscuelaDelLiderActual();
        return categoriaRepository.findByEscuela(escuela).stream()
                .map(mapperCategoria::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public Jugador crearJugador(JugadorCreateDTO dto) {
        // Validar escuela del líder
        Escuela escuela = obtenerEscuelaDelLiderActual();

        // Validar email único
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Validar documento único
        if (userRepository.existsByNumDocumento(dto.getNumDocumento())) {
            throw new RuntimeException("El número de documento ya está registrado");
        }

        // Validar número de camiseta único en la escuela
        if (jugadorRepository.existsByNumeroCamisetaAndEscuela(dto.getNumeroCamiseta(), escuela)) {
            throw new RuntimeException("El número de camiseta ya está en uso en esta escuela");
        }

        // Obtener categoría
        Categoria categoria = categoriaRepository.findById(dto.getIdCategoria())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        // Validar que la categoría pertenezca a la escuela del líder
        if (!categoria.getEscuela().getIdEscuela().equals(escuela.getIdEscuela())) {
            throw new RuntimeException("La categoría no pertenece a tu escuela");
        }

        // Crear jugador
        Jugador jugador = new Jugador();
        jugador.setNombres(dto.getNombres());
        jugador.setApellidos(dto.getApellidos());
        jugador.setNumDocumento(dto.getNumDocumento());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        jugador.setFechaNacimiento(LocalDate.parse(dto.getFechaNacimiento(), formatter));

        jugador.setEmail(dto.getEmail());
        jugador.setTelefono(dto.getTelefono());
        jugador.setPassword(passwordEncoder.encode(dto.getPassword()));

        Set<Rol> roles = new HashSet<>();
        roles.add(Rol.JUGADOR);
        jugador.setRoles(roles);

        jugador.setCategoria(categoria);
        jugador.setEscuela(escuela);
        jugador.setPosicion(dto.getPosicion());
        jugador.setNumeroCamiseta(dto.getNumeroCamiseta());

        return jugadorRepository.save(jugador);
    }

    public List<JugadorDTO> buscarJugadoresPorFiltros(String nombre, Integer categoriaId, String posicion) {
        Escuela escuela = obtenerEscuelaDelLiderActual();

        List<Jugador> jugadores = jugadorRepository.buscarPorFiltros(escuela, nombre, categoriaId, posicion);

        return jugadores.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public JugadorDTO obtenerJugadorPorId(Integer idJugador) {
        Jugador jugador = jugadorRepository.findById(idJugador)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado"));

        // Validar que el jugador pertenezca a la escuela del líder
        Escuela escuela = obtenerEscuelaDelLiderActual();
        if (!jugador.getEscuela().getIdEscuela().equals(escuela.getIdEscuela())) {
            throw new RuntimeException("No tienes permiso para acceder a este jugador");
        }

        return convertirADTO(jugador);
    }

    @Transactional
    public Jugador actualizarJugador(Integer idJugador, JugadorDTO dto) {
        Jugador jugador = jugadorRepository.findById(idJugador)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado"));

        // Validar que el jugador pertenezca a la escuela del líder
        Escuela escuela = obtenerEscuelaDelLiderActual();
        if (!jugador.getEscuela().getIdEscuela().equals(escuela.getIdEscuela())) {
            throw new RuntimeException("No tienes permiso para modificar este jugador");
        }

        // Validar número de camiseta único
        if (jugadorRepository.existsByNumeroCamisetaAndEscuelaAndIdUsuarioNot(
                dto.getNumeroCamiseta(), escuela, idJugador)) {
            throw new RuntimeException("El número de camiseta ya está en uso en esta escuela");
        }

        // Obtener categoría
        Categoria categoria = categoriaRepository.findById(dto.getIdCategoria())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        // Validar que la categoría pertenezca a la escuela
        if (!categoria.getEscuela().getIdEscuela().equals(escuela.getIdEscuela())) {
            throw new RuntimeException("La categoría no pertenece a tu escuela");
        }

        // Actualizar datos
        jugador.setNombres(dto.getNombres());
        jugador.setApellidos(dto.getApellidos());
        jugador.setNumDocumento(dto.getNumDocumento());
        jugador.setEmail(dto.getEmail());
        jugador.setTelefono(dto.getTelefono());
        jugador.setCategoria(categoria);
        jugador.setPosicion(dto.getPosicion());
        jugador.setNumeroCamiseta(dto.getNumeroCamiseta());

        return jugadorRepository.save(jugador);
    }

    @Transactional
    public void eliminarJugador(Integer idJugador) {
        Jugador jugador = jugadorRepository.findById(idJugador)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado"));

        // Validar que el jugador pertenezca a la escuela del líder
        Escuela escuela = obtenerEscuelaDelLiderActual();
        if (!jugador.getEscuela().getIdEscuela().equals(escuela.getIdEscuela())) {
            throw new RuntimeException("No tienes permiso para eliminar este jugador");
        }

        jugadorRepository.delete(jugador);
    }

    private JugadorDTO convertirADTO(Jugador jugador) {
        return JugadorDTO.builder()
                .idUsuario(jugador.getIdUsuario())
                .nombres(jugador.getNombres())
                .apellidos(jugador.getApellidos())
                .numDocumento(jugador.getNumDocumento())
                .email(jugador.getEmail())
                .telefono(jugador.getTelefono())
                .fechaNacimiento(jugador.getFechaNacimiento().toString())
                .idCategoria(jugador.getCategoria().getIdCategoria())
                .nombreCategoria(jugador.getCategoria().getNombre())
                .posicion(jugador.getPosicion())
                .numeroCamiseta(jugador.getNumeroCamiseta())
                .nombreEscuela(jugador.getEscuela().getNombre())
                .build();
    }
}