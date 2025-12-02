package com.fas.project.model;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "tb_canchas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cancha {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCancha;
    @Column(nullable = false, length = 100)
    private String nombre;
    @Column(nullable = false, length = 200)
    private String tipoSuperficie;
    @Column(nullable = false, length = 100)
    private String direccion;
    @Column(nullable = false)
    private boolean estadoDisponible;
    @Column(nullable = false)
    private String horarioDisponibilidad;
    @Column(nullable = true, length = 500)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ubicacion_id", nullable = false)
    private Ubicacion ubicacion;
}
