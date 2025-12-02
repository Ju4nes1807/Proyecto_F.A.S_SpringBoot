package com.fas.project.dto.user;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JugadorDTO {
    @NotNull(message = "La categoría es obligatoria")
    private Integer idcategoria;
    @NotBlank(message = "La posición es obligatoria")
    private String posicion;
    @NotNull(message = "El número de camiseta es obligatorio")
    private Integer numeroCamiseta;
}
