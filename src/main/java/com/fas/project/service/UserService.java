package com.fas.project.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fas.project.dto.user.RegisterRequestDTO;
import com.fas.project.model.Lider;
import com.fas.project.model.Rol;
import com.fas.project.model.User;
import com.fas.project.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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

        // Guardar (debido a @Transactional, esto se guarda automáticamente,
        // pero es buena práctica llamar a save si el framework lo requiere
        // explícitamente)
        userRepository.save(existingUser);
    }
}