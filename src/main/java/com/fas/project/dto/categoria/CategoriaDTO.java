package com.fas.project.dto.categoria;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.*;
import com.fas.project.dto.escuela.EscuelaDTO;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaDTO {
    private Integer idCategoria;
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    private String nombre;
    @NotBlank(message = "El rango de edad es obligatorio")
    private String rangoEdad;

    private EscuelaDTO escuela;
}
