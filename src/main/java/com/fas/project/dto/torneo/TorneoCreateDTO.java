package com.fas.project.dto.torneo;

import java.time.LocalDate;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TorneoCreateDTO {
    
    @NotBlank(message = "El nombre del torneo es obligatorio")
    private String nombre;
    
    @NotNull(message = "La fecha de inicio es obligatoria")
    @Future(message = "La fecha de inicio debe ser futura")
    private LocalDate fechaInicio;
    
    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate fechaFin;
    
    private String descripcion;
    
    @NotNull(message = "El cupo máximo es obligatorio")
    @Min(value = 2, message = "El cupo máximo debe ser al menos 2")
    private Integer cupoMaximo;
    
    @NotNull(message = "La categoría es obligatoria")
    private Integer categoriaId;
    
    @NotNull(message = "La ubicación es obligatoria")
    private Integer ubicacionId;
}