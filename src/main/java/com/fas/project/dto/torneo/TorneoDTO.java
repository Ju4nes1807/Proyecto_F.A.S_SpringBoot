package com.fas.project.dto.torneo;

import java.time.LocalDate;

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
public class TorneoDTO {
    private Integer idTorneo;
    private String nombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String descripcion;
    private Integer cupoMaximo;
    private Integer cuposDisponibles;
    private String estado;
    private String categoriaNombre;
    private Integer categoriaId;
    private String ubicacionLocalidad;
    private String ubicacionBarrio;
    private Integer ubicacionId;
    private Integer totalInscritos;
    private boolean puedeInscribirse;
    private boolean puedeCancelarInscripcion;
}