package com.fas.project.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fas.project.dto.user.RegisterRequestDTO;
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

        // Crear usuario
        User user = new User();
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
        Set<Rol> roles = new HashSet<>();
        if (dto.getRoles() != null && !dto.getRoles().isEmpty()) {
            roles.addAll(dto.getRoles());
        } else {
            roles.add(Rol.JUGADOR); // Rol por defecto
        }
        user.setRoles(roles);

        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}