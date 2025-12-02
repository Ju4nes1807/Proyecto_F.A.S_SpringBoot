package com.fas.project.controller;

import java.util.Collections;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fas.project.dto.categoria.CategoriaDTO;
import com.fas.project.service.CategoriaService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {
    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @PreAuthorize("hasRole('LIDER')")
    @GetMapping("/crear")
    public String formInscripcionCategoria(Model model) {
        if (!model.containsAttribute("categoriaDTO")) {
            model.addAttribute("categoriaDTO", new CategoriaDTO());
        }
        return "lider/categoria/inscripcionCategoria";
    }

    @PreAuthorize("hasRole('LIDER')")
    @PostMapping("/crear")
    public String inscribirCategoria(@Valid @ModelAttribute("categoriaDTO") CategoriaDTO categoriaDTO, String username,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            return "lider/categoria/inscripcionCategoria";
        }
        categoriaService.crearCategoria(categoriaDTO, username);
        return "redirect:/categorias";
    }

    @PreAuthorize("hasRole('LIDER')")
    @GetMapping
    public String listarCategorias(Model model) {
        try {
            model.addAttribute("categorias", categoriaService.listarCategoriasDelLider());

        } catch (RuntimeException e) {
            if ("El líder no está asociado a ninguna escuela".equals(e.getMessage())) {
                model.addAttribute("warningMessage",
                        "¡Atención! No puedes registrar o ver categorías hasta que seas asociado a una escuela.");
                model.addAttribute("categorias", Collections.emptyList());

            } else {
                throw e;
            }
        }
        return "lider/categoria/categorias";
    }

    @PreAuthorize("hasRole('LIDER')")
    @GetMapping("/editar/{id}")
    public String editarCategoria(@PathVariable("id") Integer idCategoria, Model model) {
        model.addAttribute("categoriaDTO", categoriaService.obtenerCategoriaPorId(idCategoria));
        return "lider/categoria/editarCategoria";
    }

    @PreAuthorize("hasRole('LIDER')")
    @PostMapping("/editar/{id}")
    public String actualizarCategoria(@PathVariable("id") Integer idCategoria,
            @Valid @ModelAttribute("categoriaDTO") CategoriaDTO categoriaDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            return "lider/categoria/editarCategoria";
        }
        categoriaService.actualizarCategoria(idCategoria, categoriaDTO);
        return "redirect:/categorias";
    }

    @PreAuthorize("hasRole('LIDER')")
    @PostMapping("/eliminar/{id}")
    public String eliminarCategoria(@PathVariable("id") Integer idCategoria) {
        categoriaService.eliminarCategoria(idCategoria);
        return "redirect:/categorias";
    }
}
