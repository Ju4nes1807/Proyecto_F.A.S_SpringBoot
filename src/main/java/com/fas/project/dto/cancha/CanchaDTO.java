package com.fas.project.dto.cancha;

import com.fas.project.model.Ubicacion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CanchaDTO {
    private Integer idCancha;
    @NotBlank(message = "El nombre de la cancha es obligatorio")
    private String nombre;
    @NotBlank(message = "El tipo de superficie es obligatorio")
    private String tipoSuperficie;
    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;
    private boolean estadoDisponible;
    @NotBlank(message = "El horario de disponibilidad es obligatorio")
    private String horarioDisponibilidad;
    private String descripcion;
    @NotNull(message = "La ubicación es obligatoria")
    private Integer ubicacionId;

    private Ubicacion ubicacion;
}
