package com.fas.project.dto.escuela;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EscuelaDTO {
    private Integer idEscuela;
    @NotBlank(message = "El nombre de la escuela es obligatorio")
    private String nombre;
    @NotBlank(message = "El teléfono de la escuela es obligatorio")
    private String telefono;
    @Email(message = "El correo electrónico no es válido")
    @NotBlank(message = "El correo electrónico es obligatorio")
    private String email;
    @NotEmpty(message = "Debe seleccionar al menos una ubicación")
    private Set<Integer> ubicacionIds;
}
