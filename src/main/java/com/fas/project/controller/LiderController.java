package com.fas.project.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fas.project.model.Escuela;
import com.fas.project.model.Lider;
import com.fas.project.model.User;
import com.fas.project.repository.UserRepository;
import com.fas.project.service.UserService;
import com.fas.project.service.EscuelaService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/lider")
public class LiderController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final EscuelaService escuelaService;

    // Constructor para la inyección de dependencia
    public LiderController(UserRepository userRepository, UserService userService, EscuelaService escuelaService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.escuelaService = escuelaService;
    }

    @GetMapping("/dashboard")
    public String liderDashboard(Authentication authentication, Model model) {

        // 1. Obtiene el email del usuario autenticado
        String email = authentication.getName();

        // 2. Busca el usuario completo en BD
        User userEntity = userRepository.findByEmail(email).orElse(null);

        // 3. Manejo seguro de datos del usuario
        if (userEntity != null) {

            model.addAttribute("user", userEntity);

            // Obtener 1 rol para mostrarlo
            String rolPrincipal = userEntity.getRoles().stream()
                    .findFirst().map(Enum::name)
                    .orElse("ROL_DESCONOCIDO");

            model.addAttribute("userRol", rolPrincipal);

            Escuela escuela = null;
            boolean tieneEscuela = false;

            if (userEntity instanceof Lider lider) {
                escuela = escuelaService.obtenerEscuelaPorLider(lider);
                tieneEscuela = (escuela != null);
            }

            model.addAttribute("escuela", escuela);
            model.addAttribute("tieneEscuela", tieneEscuela);

            if (escuela != null) {
                model.addAttribute("ubicaciones", escuela.getUbicaciones());
            }

        } else {
            // Si por alguna razón el usuario no existe, evita que explosionen las
            // plantillas
            model.addAttribute("user", new User());
            model.addAttribute("tieneEscuela", false);
        }

        // Datos adicionales
        model.addAttribute("username", email);
        model.addAttribute("rol", "Líder");

        return "lider/dashboardLider";
    }

    @GetMapping("/perfil/modificar")
    public String showEditForm(@Valid Authentication authentication, Model model) {
        String email = authentication.getName();
        // El servicio retorna la entidad User o una nueva entidad vacía
        User userEntity = userService.findByEmail(email);

        model.addAttribute("user", userEntity);
        // Apuntar a la nueva plantilla
        return "lider/modificarPerfil";
    }

    // --- POST: Implementar la Actualización del Líder ---
    @PostMapping("/perfil/update")
    public String updateProfile(
            User userUpdateData,
            @RequestParam("password_actual") String currentPassword,
            @RequestParam("password_nueva") String newPassword,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        String currentEmail = authentication.getName();

        try {
            // Se usa la misma lógica de negocio del UserService
            userService.updateProfile(
                    currentEmail,
                    userUpdateData,
                    currentPassword,
                    newPassword);

            redirectAttributes.addFlashAttribute("status", "¡Perfil actualizado correctamente!");

        } catch (IllegalArgumentException e) {
            // Captura errores de validación (ej. contraseña incorrecta, campo fecha vacío)
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        // Redirige a la ruta del Líder
        return "redirect:/lider/perfil/modificar";
    }
}