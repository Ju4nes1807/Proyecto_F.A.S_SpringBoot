package com.fas.project.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fas.project.dto.torneo.TorneoCreateDTO;
import com.fas.project.repository.CategoriaRepository;
import com.fas.project.repository.UbicacionRepository;
import com.fas.project.service.TorneoService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/torneos")
public class TorneoController {

    private final TorneoService torneoService;
    private final CategoriaRepository categoriaRepository;
    private final UbicacionRepository ubicacionRepository;

    public TorneoController(TorneoService torneoService,
            CategoriaRepository categoriaRepository,
            UbicacionRepository ubicacionRepository) {
        this.torneoService = torneoService;
        this.categoriaRepository = categoriaRepository;
        this.ubicacionRepository = ubicacionRepository;
    }

    // ===== ADMINISTRADOR =====

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public String listarTorneosAdmin(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String estado,
            Model model) {

        model.addAttribute("torneos", torneoService.listarTorneos(nombre, estado));
        model.addAttribute("requestNombre", nombre);
        model.addAttribute("requestEstado", estado);

        return "admin/torneo/torneos";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/crear")
    public String mostrarFormularioCrear(Model model) {
        if (!model.containsAttribute("torneoCreateDTO")) {
            model.addAttribute("torneoCreateDTO", new TorneoCreateDTO());
        }
        model.addAttribute("categorias", categoriaRepository.findAll());
        model.addAttribute("ubicaciones", ubicacionRepository.findAll());
        return "admin/torneo/crearTorneo";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/crear")
    public String crearTorneo(
            @Valid @ModelAttribute("torneoCreateDTO") TorneoCreateDTO torneoCreateDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categorias", categoriaRepository.findAll());
            model.addAttribute("ubicaciones", ubicacionRepository.findAll());
            return "admin/torneo/crearTorneo";
        }

        try {
            torneoService.crearTorneo(torneoCreateDTO);
            redirectAttributes.addFlashAttribute("success", "Torneo creado exitosamente");
            return "redirect:/torneos/admin";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("categorias", categoriaRepository.findAll());
            model.addAttribute("ubicaciones", ubicacionRepository.findAll());
            return "admin/torneo/crearTorneo";
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") Integer id, Model model) {
        try {
            model.addAttribute("torneoCreateDTO", torneoService.obtenerTorneoPorId(id));
            model.addAttribute("categorias", categoriaRepository.findAll());
            model.addAttribute("ubicaciones", ubicacionRepository.findAll());
            model.addAttribute("torneoId", id);
            return "admin/torneo/editarTorneo";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/torneos/admin";
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
@PostMapping("/admin/cambiar-estado/{id}")
public String cambiarEstadoTorneo(
        @PathVariable("id") Integer id,
        @RequestParam("nuevoEstado") String nuevoEstado,
        RedirectAttributes redirectAttributes) {
    
    try {
        torneoService.cambiarEstadoTorneo(id, nuevoEstado);
        redirectAttributes.addFlashAttribute("success", "Estado del torneo actualizado exitosamente");
    } catch (RuntimeException e) {
        redirectAttributes.addFlashAttribute("error", e.getMessage());
    }
    
    return "redirect:/torneos/admin";
}

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/editar/{id}")
    public String editarTorneo(
            @PathVariable("id") Integer id,
            @Valid @ModelAttribute("torneoCreateDTO") TorneoCreateDTO torneoCreateDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categorias", categoriaRepository.findAll());
            model.addAttribute("ubicaciones", ubicacionRepository.findAll());
            model.addAttribute("torneoId", id);
            return "admin/torneo/editarTorneo";
        }

        try {
            torneoService.actualizarTorneo(id, torneoCreateDTO);
            redirectAttributes.addFlashAttribute("success", "Torneo actualizado exitosamente");
            return "redirect:/torneos/admin";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("categorias", categoriaRepository.findAll());
            model.addAttribute("ubicaciones", ubicacionRepository.findAll());
            model.addAttribute("torneoId", id);
            return "admin/torneo/editarTorneo";
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/eliminar/{id}")
    public String eliminarTorneo(
            @PathVariable("id") Integer id,
            RedirectAttributes redirectAttributes) {

        try {
            torneoService.eliminarTorneo(id);
            redirectAttributes.addFlashAttribute("success", "Torneo eliminado exitosamente");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/torneos/admin";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/inscritos/{id}")
    public String verInscritos(@PathVariable("id") Integer id, Model model) {
        try {
            model.addAttribute("torneo", torneoService.obtenerTorneoPorId(id));
            model.addAttribute("inscritos", torneoService.listarInscripcionesTorneo(id));
            return "admin/torneo/inscritosTorneo";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/torneos/admin";
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/desinscribir/{inscripcionId}")
    public String desinscribirEscuela(
            @PathVariable("inscripcionId") Integer inscripcionId,
            @RequestParam("torneoId") Integer torneoId,
            RedirectAttributes redirectAttributes) {

        try {
            torneoService.desinscribirEscuela(inscripcionId);
            redirectAttributes.addFlashAttribute("success", "Escuela desinscrita exitosamente");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/torneos/admin/inscritos/" + torneoId;
    }

    // ===== LÍDER =====

    @PreAuthorize("hasRole('LIDER')")
    @GetMapping("/lider")
    public String listarTorneosLider(Model model) {
        model.addAttribute("torneosDisponibles", torneoService.listarTorneosDisponibles());
        model.addAttribute("misTorneos", torneoService.listarTorneosDelLider());
        return "lider/torneo/torneos";
    }

    @PreAuthorize("hasRole('LIDER')")
    @PostMapping("/lider/inscribir/{id}")
    public String inscribirEnTorneo(
            @PathVariable("id") Integer id,
            RedirectAttributes redirectAttributes) {

        try {
            torneoService.inscribirEscuela(id);
            redirectAttributes.addFlashAttribute("success",
                    "¡Inscripción exitosa! Tu escuela ha sido inscrita en el torneo.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/torneos/lider";
    }

    @PreAuthorize("hasRole('LIDER')")
    @PostMapping("/lider/cancelar/{id}")
    public String cancelarInscripcion(
            @PathVariable("id") Integer id,
            RedirectAttributes redirectAttributes) {

        try {
            torneoService.cancelarInscripcion(id);
            redirectAttributes.addFlashAttribute("success",
                    "Inscripción cancelada exitosamente");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/torneos/lider";
    }

    // ===== JUGADOR =====

    @PreAuthorize("hasRole('JUGADOR')")
    @GetMapping("/jugador")
    public String listarTorneosJugador(Model model) {
        model.addAttribute("torneos", torneoService.listarTorneosDelJugador());
        return "jugador/torneo/torneos";
    }
}