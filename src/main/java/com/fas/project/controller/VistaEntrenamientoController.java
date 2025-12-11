package com.fas.project.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fas.project.dto.entrenamiento.EntrenamientoDTO;
import com.fas.project.model.Lider;
import com.fas.project.model.User;
import com.fas.project.service.CategoriaService;
import com.fas.project.service.EntrenamientoService;
import com.fas.project.service.UserService;

@Controller
@RequestMapping("/entrenamientos")
public class VistaEntrenamientoController {

    private final EntrenamientoService entrenamientoService;
    private final CategoriaService categoriaService;
    private final UserService userService;

    public VistaEntrenamientoController(EntrenamientoService entrenamientoService,
                                        CategoriaService categoriaService,
                                        UserService userService) {
        this.entrenamientoService = entrenamientoService;
        this.categoriaService = categoriaService;
        this.userService = userService;
    }

    // ================================================================
    // OBTENER USUARIO ACTUAL
    // ================================================================
    private User getUsuarioActual() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = ((UserDetails) principal).getUsername();
        return userService.findByEmail(email);
    }

    // ================================================================
    // LISTAR ENTRENAMIENTOS
    // ================================================================
    @GetMapping("/listar")
    public String listarEntrenamientos(Model model) {

        User usuario = getUsuarioActual();

        if (!(usuario instanceof Lider lider)) {
            throw new RuntimeException("Solo los líderes pueden ver entrenamientos.");
        }

        Integer idEscuela = lider.getEscuela().getIdEscuela();

        model.addAttribute("entrenamientos",
                entrenamientoService.filtrar(null, null, null, idEscuela, null));

        // 👉 Ruta corregida
         return "lider/entrenamientos/Entrenamientos";
    }

    // ================================================================
    // FORMULARIO CREAR
    // ================================================================
    @GetMapping("/crear")
    public String crearForm(Model model) {

        model.addAttribute("categorias", categoriaService.listarCategoriasDelLider());
        model.addAttribute("entrenamiento", new EntrenamientoDTO());

        // 👉 Ruta corregida
        return "lider/entrenamientos/CrearEntrenamiento";
    }

    // ================================================================
    // GUARDAR CREACIÓN
    // ================================================================
    @PostMapping("/crear")
    public String crearSubmit(@ModelAttribute("entrenamiento") EntrenamientoDTO dto) {

        User usuario = getUsuarioActual();
        Lider lider = (Lider) usuario;

        dto.setIdLider(usuario.getIdUsuario());
        dto.setIdEscuela(lider.getEscuela().getIdEscuela());

        entrenamientoService.crearEntrenamiento(dto);
        return "redirect:/entrenamientos/listar";
    }

    // ================================================================
    // FORMULARIO EDITAR
    // ================================================================
    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Integer id, Model model) {

        EntrenamientoDTO dto = entrenamientoService.obtenerPorId(id);

        model.addAttribute("categorias", categoriaService.listarCategoriasDelLider());
        model.addAttribute("entrenamiento", dto);

        return "lider/entrenamientos/EditarEntrenamiento";
    }

    @PostMapping("/editar/{id}")
    public String editarSubmit(@PathVariable Integer id,
                               @ModelAttribute("entrenamiento") EntrenamientoDTO dto) {

        User usuario = getUsuarioActual();
        Lider lider = (Lider) usuario;

        dto.setIdLider(usuario.getIdUsuario());
        dto.setIdEscuela(lider.getEscuela().getIdEscuela());

        entrenamientoService.actualizarEntrenamiento(id, dto);

        return "redirect:/entrenamientos/listar";
    }

    // ================================================================
    // ELIMINAR
    // ================================================================
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        entrenamientoService.eliminarEntrenamiento(id);
        return "redirect:/entrenamientos/listar";
    }
}
