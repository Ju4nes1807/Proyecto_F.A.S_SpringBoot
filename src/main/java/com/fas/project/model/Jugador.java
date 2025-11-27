package com.fas.project.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@PrimaryKeyJoinColumn(name = "id_usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Jugador extends User {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_escuela", nullable = false)
    private Escuela escuela;

    private String posicion;
    private Integer numeroCamiseta;
}
