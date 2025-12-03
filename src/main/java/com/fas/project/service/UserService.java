package com.fas.project.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fas.project.dto.user.RegisterRequestDTO;
import com.fas.project.model.Escuela;
import com.fas.project.model.Lider;
import com.fas.project.model.Rol;
import com.fas.project.model.User;
import com.fas.project.repository.UserRepository;
import com.fas.project.repository.EscuelaRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EscuelaRepository escuelaRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
            EscuelaRepository escuelaRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.escuelaRepository = escuelaRepository;
    }

    @Transactional
    public User registerUser(RegisterRequestDTO dto) {
        // Validaciones
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        if (userRepository.existsByNumDocumento(dto.getNumDocumento())) {
            throw new RuntimeException("El número de documento ya está registrado");
        }

        // Crear usuario o líder según el rol
        User user;
        if (dto.getRoles().contains(Rol.LIDER)) {
            Lider lider = new Lider();
            user = lider; // para seguir usando el tipo User
        } else {
            user = new User();
        }

        // Campos comunes
        user.setNombres(dto.getNombres());
        user.setApellidos(dto.getApellidos());
        user.setNumDocumento(dto.getNumDocumento());

        // Convertir String a LocalDate
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        user.setFechaNacimiento(LocalDate.parse(dto.getFechaNacimiento(), formatter));

        user.setEmail(dto.getEmail());
        user.setTelefono(dto.getTelefono());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        // Asignar roles
        if (dto.getRoles() == null || dto.getRoles().isEmpty()) {
            throw new IllegalArgumentException("El usuario debe tener al menos un rol.");
        }
        user.setRoles(new HashSet<>(dto.getRoles()));

        // Guardar en la base
        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @Transactional
    public void updateProfile(
            String currentEmail,
            User userUpdateData,
            String currentPassword,
            String newPassword) throws IllegalArgumentException {

        // Código corregido dentro de tu UserService
        User existingUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuario autenticado no encontrado."));

        // A. VALIDACIÓN Y ACTUALIZACIÓN DE CONTRASEÑA
        boolean passwordChangeAttempted = !newPassword.isEmpty();

        if (passwordChangeAttempted) {
            // 1. Verifica la contraseña actual
            if (!passwordEncoder.matches(currentPassword, existingUser.getPassword())) {
                throw new IllegalArgumentException("La contraseña actual es incorrecta.");
            }

            // 2. Codifica y actualiza la nueva contraseña
            existingUser.setPassword(passwordEncoder.encode(newPassword));
        }

        // B. ACTUALIZACIÓN DE DATOS PERSONALES
        // Mapear los campos permitidos del formulario a la entidad existente
        existingUser.setNombres(userUpdateData.getNombres());
        existingUser.setApellidos(userUpdateData.getApellidos());
        existingUser.setNumDocumento(userUpdateData.getNumDocumento());
        existingUser.setTelefono(userUpdateData.getTelefono());
        existingUser.setFechaNacimiento(userUpdateData.getFechaNacimiento());

        userRepository.save(existingUser);
    }

    @Transactional(readOnly = true)
    public List<Lider> listarLideres() {
        return userRepository.findAllLideres();
    }

    @Transactional
    public void deleteLider(Integer liderId) {
        User user = userRepository.findById(liderId)
                .orElseThrow(() -> new RuntimeException("Líder no encontrado"));

        if (!(user instanceof Lider)) {
            throw new IllegalArgumentException("El usuario no es un líder");
        }

        Lider lider = (Lider) user;
        Escuela escuela = lider.getEscuela();

        // Si el líder tiene una escuela asociada
        if (escuela != null) {
            // Remover el líder de la lista de líderes de la escuela
            escuela.getLideres().remove(lider);

            // Desasociar la escuela del líder
            lider.setEscuela(null);

            // Verificar si la escuela tiene más líderes
            if (escuela.getLideres().isEmpty()) {
                // Si no tiene más líderes, eliminar la escuela
                escuelaRepository.delete(escuela);
            } else {
                // Si tiene más líderes, solo guardar los cambios
                escuelaRepository.save(escuela);
            }
        }

        // Finalmente eliminar el líder
        userRepository.delete(lider);
    }

    @Transactional(readOnly = true)
    public List<Lider> listarLideres(String nombreCompleto, String numDocumento) {
        // Si no hay filtros, retornar todos
        if ((nombreCompleto == null || nombreCompleto.trim().isEmpty()) &&
                (numDocumento == null || numDocumento.trim().isEmpty())) {
            return userRepository.findAllLideres();
        }

        // Si hay filtros, aplicarlos
        return userRepository.findLideresByFiltros(nombreCompleto, numDocumento);
    }
}