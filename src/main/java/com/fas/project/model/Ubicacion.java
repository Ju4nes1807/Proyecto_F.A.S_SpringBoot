package com.fas.project.model;

import java.util.Set;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_ubicaciones", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "localidad", "barrio" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ubicacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUbicacion;
    @Column(nullable = false)
    private String localidad;
    @Column(nullable = false)
    private String barrio;

    public Ubicacion(String localidad) {
        this.localidad = localidad;
    }

    @ManyToMany(mappedBy = "ubicaciones")
    private Set<Escuela> escuelas;

    @OneToMany(mappedBy = "ubicacion")
    private Set<Cancha> canchas;
}
