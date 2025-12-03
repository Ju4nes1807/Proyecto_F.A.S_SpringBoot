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

import com.fas.project.dto.user.JugadorCreateDTO;
import com.fas.project.dto.user.JugadorDTO;
import com.fas.project.service.JugadorService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/jugadores")
public class JugadorController {
    
    private final JugadorService jugadorService;

    public JugadorController(JugadorService jugadorService) {
        this.jugadorService = jugadorService;
    }

    @PreAuthorize("hasRole('LIDER')")
    @GetMapping
    public String listarJugadores(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Integer categoriaId,
            @RequestParam(required = false) String posicion,
            Model model) {
        
        try {
            var jugadores = jugadorService.buscarJugadoresPorFiltros(nombre, categoriaId, posicion);
            var categorias = jugadorService.obtenerCategoriasDelLider();
            
            model.addAttribute("jugadores", jugadores);
            model.addAttribute("categorias", categorias);
            model.addAttribute("requestNombre", nombre);
            model.addAttribute("requestCategoria", categoriaId);
            model.addAttribute("requestPosicion", posicion);
            
        } catch (RuntimeException e) {
            if ("El líder no está asociado a ninguna escuela".equals(e.getMessage())) {
                model.addAttribute("warningMessage", 
                    "¡Atención! No puedes registrar o ver jugadores hasta que seas asociado a una escuela.");
                model.addAttribute("jugadores", java.util.Collections.emptyList());
                model.addAttribute("categorias", java.util.Collections.emptyList());
            } else {
                throw e;
            }
        }
        
        return "lider/jugadores/jugadores";
    }

    @PreAuthorize("hasRole('LIDER')")
    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        try {
            if (!model.containsAttribute("jugadorCreateDTO")) {
                model.addAttribute("jugadorCreateDTO", new JugadorCreateDTO());
            }
            model.addAttribute("categorias", jugadorService.obtenerCategoriasDelLider());
            return "lider/jugadores/crearJugador";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/jugadores";
        }
    }

    @PreAuthorize("hasRole('LIDER')")
    @PostMapping("/crear")
    public String crearJugador(
            @Valid @ModelAttribute("jugadorCreateDTO") JugadorCreateDTO jugadorCreateDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("categorias", jugadorService.obtenerCategoriasDelLider());
            return "lider/jugadores/crearJugador";
        }
        
        try {
            jugadorService.crearJugador(jugadorCreateDTO);
            redirectAttributes.addFlashAttribute("success", "Jugador creado exitosamente");
            return "redirect:/jugadores";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("categorias", jugadorService.obtenerCategoriasDelLider());
            return "lider/jugadores/crearJugador";
        }
    }

    @PreAuthorize("hasRole('LIDER')")
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") Integer idJugador, Model model) {
        try {
            JugadorDTO jugadorDTO = jugadorService.obtenerJugadorPorId(idJugador);
            model.addAttribute("jugadorDTO", jugadorDTO);
            model.addAttribute("categorias", jugadorService.obtenerCategoriasDelLider());
            return "lider/jugadores/editarJugador";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/jugadores";
        }
    }

    @PreAuthorize("hasRole('LIDER')")
    @PostMapping("/editar/{id}")
    public String editarJugador(
            @PathVariable("id") Integer idJugador,
            @Valid @ModelAttribute("jugadorDTO") JugadorDTO jugadorDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("categorias", jugadorService.obtenerCategoriasDelLider());
            return "lider/jugadores/editarJugador";
        }
        
        try {
            jugadorService.actualizarJugador(idJugador, jugadorDTO);
            redirectAttributes.addFlashAttribute("success", "Jugador actualizado exitosamente");
            return "redirect:/jugadores";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("categorias", jugadorService.obtenerCategoriasDelLider());
            return "lider/jugadores/editarJugador";
        }
    }

    @PreAuthorize("hasRole('LIDER')")
    @PostMapping("/eliminar/{id}")
    public String eliminarJugador(
            @PathVariable("id") Integer idJugador,
            RedirectAttributes redirectAttributes) {
        
        try {
            jugadorService.eliminarJugador(idJugador);
            redirectAttributes.addFlashAttribute("success", "Jugador eliminado exitosamente");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/jugadores";
    }
}