package com.fas.project.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fas.project.model.Jugador;
import com.fas.project.model.User;
import com.fas.project.repository.UserRepository;
import com.fas.project.service.UserService;

@Controller
@RequestMapping("/jugador")
public class JugadorDashboardController {

    private final UserRepository userRepository;
    private final UserService userService;

    public JugadorDashboardController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String jugadorDashboard(Authentication authentication, Model model) {
        String email = authentication.getName();

        User userEntity = userRepository.findByEmail(email).orElse(null);

        if (userEntity != null && userEntity instanceof Jugador) {
            Jugador jugador = (Jugador) userEntity;
            
            model.addAttribute("user", jugador);
            model.addAttribute("username", email);
            model.addAttribute("rol", "Jugador");
            
            // Información adicional del jugador
            if (jugador.getCategoria() != null) {
                model.addAttribute("categoria", jugador.getCategoria().getNombre());
                model.addAttribute("rangoEdad", jugador.getCategoria().getRangoEdad());
            }
            
            if (jugador.getEscuela() != null) {
                model.addAttribute("escuela", jugador.getEscuela());
            }
            
            model.addAttribute("posicion", jugador.getPosicion());
            model.addAttribute("numeroCamiseta", jugador.getNumeroCamiseta());
        } else {
            model.addAttribute("user", new User());
        }

        return "jugador/dashboardJugador";
    }

    @GetMapping("/perfil/modificar")
    public String showEditForm(Authentication authentication, Model model) {
        String email = authentication.getName();
        User userEntity = userService.findByEmail(email);

        if (userEntity instanceof Jugador) {
            Jugador jugador = (Jugador) userEntity;
            model.addAttribute("user", jugador);
            model.addAttribute("jugador", jugador);
        }

        return "jugador/modificarPerfil";
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
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/jugador/perfil/modificar";
    }
}