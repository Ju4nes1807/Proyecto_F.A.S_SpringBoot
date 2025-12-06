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
public class InscripcionDTO {
    
    private Integer idInscripcion;
    private Integer torneoId;
    private String torneoNombre;
    private Integer escuelaId;
    private String escuelaNombre;
    private Integer liderId;
    private String liderNombre;
    private String fechaInscripcion;
    private String estado;
}