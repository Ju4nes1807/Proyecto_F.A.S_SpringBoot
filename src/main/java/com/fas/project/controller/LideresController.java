package com.fas.project.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fas.project.service.UserService;

@Controller
@RequestMapping("/lideres")
public class LideresController {
    private final UserService userService;

    public LideresController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public String listarLideres(
            @RequestParam(required = false) String nombreCompleto,
            @RequestParam(required = false) String numDocumento,
            Model model) {

        model.addAttribute("lideres", userService.listarLideres(nombreCompleto, numDocumento));
        model.addAttribute("nombreCompleto", nombreCompleto);
        model.addAttribute("numDocumento", numDocumento);

        return "admin/usuarios/lideres";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarLider(@PathVariable("id") Integer id) {
        userService.deleteLider(id);
        return "redirect:/lideres";
    }
}
