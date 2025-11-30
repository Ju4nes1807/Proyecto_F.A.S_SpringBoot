package com.fas.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fas.project.dto.user.RegisterRequestDTO;
import com.fas.project.service.UserService;

import jakarta.validation.Valid;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {
        if (error != null) {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
        }
        if (logout != null) {
            model.addAttribute("message", "Sesión cerrada exitosamente");
        }
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerDTO", new RegisterRequestDTO());
        return "register";
    }

    @PostMapping("/proceso-registro")
    public String processRegister(@Valid @ModelAttribute("registerDTO") RegisterRequestDTO registerDTO,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        // Validar que no haya errores de validación
        if (result.hasErrors()) {
            return "register";
        }

        try {
            userService.registerUser(registerDTO);
            redirectAttributes.addFlashAttribute("success", "Registro exitoso. Por favor inicia sesión.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/landingpage")
    public String landingPage() {
        return "landingpage";
    }
}