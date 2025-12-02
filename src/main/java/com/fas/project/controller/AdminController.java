package com.fas.project.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fas.project.model.User; // Tu entidad User
import com.fas.project.repository.UserRepository; // ¡Inyéctalo!
import com.fas.project.service.UserService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final UserService userService;

    // Constructor para la inyección de dependencia
    public AdminController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String adminDashboard(Authentication authentication, Model model) {

        // 1. Obtiene el email (username) del usuario autenticado
        String email = authentication.getName();

        // 2. Busca la entidad User completa en la base de datos
        User userEntity = userRepository.findByEmail(email)
                // Asegúrate de manejar el caso Optional/NotFound
                .orElse(null);

        if (userEntity != null) {
            model.addAttribute("user", userEntity);
        } else {
            // Manejo de error si no se encuentra (opcional, pero recomendado)
            model.addAttribute("user", new User()); // Pasa un objeto User vacío para evitar null
        }

        // 4. Atributos adicionales
        model.addAttribute("username", email);
        model.addAttribute("rol", "Administrador");

        return "admin/dashboardAdmin";
    }

    @GetMapping("/perfil/modificar")
    public String showEditForm(Authentication authentication, Model model) {
        String email = authentication.getName();
        User userEntity = userService.findByEmail(email);

        model.addAttribute("user", userEntity);
        return "admin/modificarPerfil";
    }

    @PostMapping("/perfil/update")
    public String updateProfile(
            User userUpdateData,
            @RequestParam("password_actual") String currentPassword,
            @RequestParam("password_nueva") String newPassword,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        String currentEmail = authentication.getName();

        try {
            userService.updateProfile(
                    currentEmail,
                    userUpdateData,
                    currentPassword,
                    newPassword);
            redirectAttributes.addFlashAttribute("status", "¡Perfil actualizado correctamente!");
        } catch (IllegalArgumentException e) {
            // Captura los errores lanzados por el servicio (ej. contraseña incorrecta)
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/perfil/modificar";
    }
}
