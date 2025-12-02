package com.fas.project.dto.entrenamiento;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntrenamientoDTO {

    private Integer idEntrenamiento;
    private String titulo;
    private String fecha;
    private String hora_inicio;
    private String hora_fin;
    private String lugar;
    private String descripcion;
    private String id_escuela;
    private String id_categoria;
}
