package com.fas.project.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.fas.project.model.Escuela;
import com.fas.project.model.Lider;
import com.fas.project.model.User;
import com.fas.project.service.CustomUserDetailsService;
import com.fas.project.service.JugadorService;
import com.fas.project.service.ReporteService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/jugadores")
public class JugadorController {

    private final JugadorService jugadorService;
    private final ReporteService reporteService;

    public JugadorController(JugadorService jugadorService, ReporteService reporteService) {
        this.jugadorService = jugadorService;
        this.reporteService = reporteService;
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

    @PreAuthorize("hasRole('LIDER')")
    @GetMapping("/reporte/pdf")
    public void generarReportePdf(
            HttpServletResponse response,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Integer categoriaId,
            @RequestParam(required = false) String posicion) throws Exception {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();

        if (!(principal instanceof CustomUserDetailsService.CustomUserDetails)) {
            throw new RuntimeException("Usuario no autenticado correctamente");
        }

        CustomUserDetailsService.CustomUserDetails userDetails = (CustomUserDetailsService.CustomUserDetails) principal;
        User user = userDetails.getUser();

        if (!(user instanceof Lider)) {
            throw new RuntimeException("Solo los líderes pueden acceder a esta funcionalidad");
        }

        Lider lider = (Lider) user;
        Escuela escuela = lider.getEscuela();
        Integer idEscuela = escuela.getIdEscuela();

        // LIMPIAR PARÁMETROS: Convertir strings vacíos a null
        String nombreLimpio = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        String posicionLimpia = (posicion != null && !posicion.trim().isEmpty()) ? posicion.trim() : null;

        // Agregar comodines al nombre solo si no es null
        String nombreBusqueda = (nombreLimpio != null) ? "%" + nombreLimpio + "%" : null;

        // Llamar al servicio con parámetros limpios
        List<JugadorDTO> jugadores = jugadorService.obtenerReporteJugadores(
                idEscuela,
                nombreBusqueda,
                categoriaId,
                posicionLimpia);

        if (jugadores.isEmpty()) {
            throw new RuntimeException("No se encontraron jugadores para la escuela con ID: " + idEscuela);
        }

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=reporte_jugadores.pdf");

        reporteService.generarReporteJugadoresPDF(jugadores, response.getOutputStream());
    }

    @PreAuthorize("hasRole('LIDER')")
    @GetMapping("/reporte/excel")
    public void generarReporteExcel(
            HttpServletResponse response,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Integer categoriaId,
            @RequestParam(required = false) String posicion) throws Exception {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();

        if (!(principal instanceof CustomUserDetailsService.CustomUserDetails)) {
            throw new RuntimeException("Usuario no autenticado correctamente");
        }

        CustomUserDetailsService.CustomUserDetails userDetails = (CustomUserDetailsService.CustomUserDetails) principal;
        User user = userDetails.getUser();

        if (!(user instanceof Lider)) {
            throw new RuntimeException("Solo los líderes pueden acceder a esta funcionalidad");
        }

        Lider lider = (Lider) user;
        Escuela escuela = lider.getEscuela();
        Integer idEscuela = escuela.getIdEscuela();

        // LIMPIAR PARÁMETROS: Convertir strings vacíos a null
        String nombreLimpio = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        String posicionLimpia = (posicion != null && !posicion.trim().isEmpty()) ? posicion.trim() : null;

        // Agregar comodines al nombre solo si no es null
        String nombreBusqueda = (nombreLimpio != null) ? "%" + nombreLimpio + "%" : null;

        // Obtener jugadores con los mismos filtros
        List<JugadorDTO> jugadores = jugadorService.obtenerReporteJugadores(
                idEscuela,
                nombreBusqueda,
                categoriaId,
                posicionLimpia);

        if (jugadores.isEmpty()) {
            throw new RuntimeException("No se encontraron jugadores para la escuela con ID: " + idEscuela);
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=reporte_jugadores.xlsx");

        reporteService.generarReporteJugadoresExcel(jugadores, response.getOutputStream());
    }

}