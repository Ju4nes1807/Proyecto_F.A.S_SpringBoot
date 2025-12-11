package com.fas.project.dto.entrenamiento;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntrenamientoDTO {

    private Integer id;

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 100, message = "El título no puede superar los 100 caracteres")
    private String titulo;

    @Size(max = 255, message = "La descripción no puede superar los 255 caracteres")
    private String descripcion;

    @NotBlank(message = "La fecha es obligatoria")
    private String fecha;

    @NotBlank(message = "La hora de inicio es obligatoria")
    private String horaInicio;

    @NotBlank(message = "La hora de fin es obligatoria")
    private String horaFin;

    @NotBlank(message = "El lugar es obligatorio")
    private String lugar;

    @NotNull(message = "Debe seleccionar una categoría")
    private Integer idCategoria;

    @NotNull(message = "Debe seleccionar una escuela")
    private Integer idEscuela;

    @NotNull(message = "Debe seleccionar un líder creador")
    private Integer idLider;

    @NotNull(message = "Debe seleccionar una ubicación")
    private Integer idUbicacion;

    // Datos para mostrar
    private String categoriaNombre;
    private String escuelaNombre;
    private String nombreLider;
    private String ubicacionNombre;
}

