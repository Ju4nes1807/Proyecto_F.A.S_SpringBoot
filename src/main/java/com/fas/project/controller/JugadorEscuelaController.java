package com.fas.project.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fas.project.model.Jugador;
import com.fas.project.model.User;
import com.fas.project.repository.UserRepository;

@Controller
@RequestMapping("/jugador")
public class JugadorEscuelaController {

    private final UserRepository userRepository;

    public JugadorEscuelaController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasRole('JUGADOR')")
    @GetMapping("/escuela")
    public String verEscuela(Authentication authentication, Model model) {
        String email = authentication.getName();

        User userEntity = userRepository.findByEmail(email).orElse(null);

        if (userEntity != null && userEntity instanceof Jugador) {
            Jugador jugador = (Jugador) userEntity;

            if (jugador.getEscuela() != null) {
                model.addAttribute("escuela", jugador.getEscuela());
                model.addAttribute("ubicaciones", jugador.getEscuela().getUbicaciones());
            } else {
                model.addAttribute("escuela", null);
            }

            if (jugador.getCategoria() != null) {
                model.addAttribute("categoria", jugador.getCategoria().getNombre());
                model.addAttribute("rangoEdad", jugador.getCategoria().getRangoEdad());
            }

            model.addAttribute("posicion", jugador.getPosicion());
            model.addAttribute("numeroCamiseta", jugador.getNumeroCamiseta());
        }

        return "jugador/escuela";
    }
}