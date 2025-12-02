package com.fas.project.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fas.project.service.EscuelaService;
import com.fas.project.dto.escuela.EscuelaDTO;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/escuelas")
public class EscuelaController {
    private final EscuelaService escuelaService;

    public EscuelaController(EscuelaService escuelaService) {
        this.escuelaService = escuelaService;
    }

    @PreAuthorize("hasRole('LIDER')")
    @GetMapping("/inscripcion")
    public String mostrarFormularioInscripcion(Model model) {
        if (!model.containsAttribute("escuelaDTO")) {
            model.addAttribute("escuelaDTO", new EscuelaDTO());
        }
        model.addAttribute("ubicaciones", escuelaService.obtenerUbicaciones());
        return "lider/escuela/inscripcionEscuela";
    }

    @PreAuthorize("hasRole('LIDER')")
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable("id") Integer idEscuela, Model model) {
        EscuelaDTO escuelaDTO = escuelaService.obtenerPorId(idEscuela);
        model.addAttribute("escuelaDTO", escuelaDTO);
        model.addAttribute("ubicaciones", escuelaService.obtenerUbicaciones());
        return "lider/escuela/editarEscuela";
    }

    @PreAuthorize("hasRole('LIDER')")
    @PostMapping("/guardar")
    public String inscribirEscuela(@Valid @ModelAttribute("escuelaDTO") EscuelaDTO escuelaDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("ubicaciones", escuelaService.obtenerUbicaciones());
            return "lider/escuela/inscripcionEscuela";
        }
        escuelaService.registrarEscuela(escuelaDTO);
        return "redirect:/lider/dashboard";
    }

    @PreAuthorize("hasRole('LIDER')")
    @PostMapping("/editar/{id}")
    public String editarEscuela(@PathVariable("id") Integer idEscuela,
            @Valid @ModelAttribute("escuelaDTO") EscuelaDTO escuelaDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("ubicaciones", escuelaService.obtenerUbicaciones());
            return "lider/escuela/editarEscuela";
        }
        escuelaService.editarEscuela(idEscuela, escuelaDTO);
        return "redirect:/lider/dashboard";
    }

    @PreAuthorize("hasAnyRole('LIDER','ADMIN')")
    @PostMapping("/eliminar/{id}")
    public String eliminarEscuela(@PathVariable("id") Integer idEscuela) {
        escuelaService.eliminarEscuela(idEscuela);
        return switch (getCurrentUserRole()) {
            case "LIDER" -> "redirect:/lider/dashboard";
            case "ADMIN" -> "redirect:/escuelas/admin/escuelas";
            default -> "redirect:/";
        };
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/escuelas")
    public String listarEscuelas(Model model) {
        model.addAttribute("escuelas", escuelaService.obtenerTodasLasEscuelas());
        return "admin/escuela/escuelas";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/escuelas/buscar")
    public String buscarEscuelas(@RequestParam(required = false) String nombre,
            @RequestParam(required = false) String localidad,
            Model model) {
        model.addAttribute("escuelas", escuelaService.buscarEscuelas(nombre, localidad));
        return "admin/escuela/escuelas";
    }

    private String getCurrentUserRole() {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
                .findFirst()
                .map(grantedAuthority -> grantedAuthority.getAuthority().replace("ROLE_", ""))
                .orElse("");
    }
}