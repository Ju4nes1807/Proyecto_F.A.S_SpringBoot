package com.fas.project.dto.torneo;

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
public class TorneoReporteDTO {
    private Integer idTorneo;
    private String nombre;
    private String fechaInicio;
    private String fechaFin;
    private String descripcion;
    private Integer cupoMaximo;
    private Integer cuposDisponibles;
    private String estado;
    private String categoriaNombre;
    private String ubicacionLocalidad;
    private String ubicacionBarrio;
    private Integer totalInscritos;
}