package com.fas.project.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

import org.springframework.validation.BindingResult;

import com.fas.project.dto.cancha.CanchaDTO;
import com.fas.project.model.Ubicacion;
import com.fas.project.repository.UbicacionRepository;
import com.fas.project.service.CanchaService;

@Controller
@RequestMapping("/canchas")
public class CanchaController {
    private final CanchaService canchaService;
    private final UbicacionRepository ubicacionRepository;

    public CanchaController(CanchaService canchaService, UbicacionRepository ubicacionRepository) {
        this.canchaService = canchaService;
        this.ubicacionRepository = ubicacionRepository;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LIDER')")
    @GetMapping // Solo /canchas
    public String listarCanchas(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Integer ubicacionId,
            Model model) {

        if (nombre != null && nombre.trim().isEmpty())
            nombre = null;

        List<Ubicacion> ubicaciones = ubicacionRepository.findAll();
        model.addAttribute("ubicaciones", ubicaciones);

        // Si NO hay filtros, trae todas
        List<CanchaDTO> canchas = canchaService.buscarPorFiltros(nombre, ubicacionId);

        model.addAttribute("requestNombre", nombre);
        model.addAttribute("requestUbicacion", ubicacionId);
        model.addAttribute("canchas", canchas);

        return switch (getCurrentUserRole()) {
            case "ADMIN" -> "admin/cancha/canchas";
            case "LIDER" -> "lider/cancha/canchas";
            default -> "error/403";
        };
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/crear")
    public String mostrarFormularioCreacion(Model model) {
        if (!model.containsAttribute("canchaDTO")) {
            model.addAttribute("canchaDTO", new CanchaDTO());
        }
        model.addAttribute("ubicaciones", ubicacionRepository.findAll());
        return "admin/cancha/inscripcionCancha";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/crear")
    public String gurdarCancha(@Valid @ModelAttribute("canchaDTO") CanchaDTO canchaDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("ubicaciones", ubicacionRepository.findAll());
            return "admin/cancha/inscripcionCancha";
        }
        canchaService.crearCancha(canchaDTO);
        return "redirect:/canchas";

    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@ModelAttribute("id") Integer idCancha, Model model) {
        CanchaDTO canchaDTO = canchaService.obtenerCanchaPorId(idCancha);
        model.addAttribute("canchaDTO", canchaDTO);
        model.addAttribute("ubicaciones", ubicacionRepository.findAll());
        return "admin/cancha/editarCancha";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/editar/{id}")
    public String actualizarCancha(@ModelAttribute("id") Integer idCancha,
            @Valid @ModelAttribute("canchaDTO") CanchaDTO canchaDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("ubicaciones", ubicacionRepository.findAll());
            return "admin/cancha/editarCancha";
        }
        canchaService.actualizarCancha(idCancha, canchaDTO);
        return "redirect:/canchas";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/eliminar/{id}")
    public String eliminarCancha(@ModelAttribute("id") Integer idCancha) {
        canchaService.eliminarCancha(idCancha);
        return "redirect:/canchas";
    }

    private String getCurrentUserRole() {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
                .findFirst()
                .map(grantedAuthority -> grantedAuthority.getAuthority().replace("ROLE_", ""))
                .orElse("");
    }
}