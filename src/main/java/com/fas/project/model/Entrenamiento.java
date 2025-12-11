package com.fas.project.model;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_entrenamientos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Entrenamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_entrenamiento")
    private Integer idEntrenamiento;

    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(nullable = true, length = 255)
    private String descripcion;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    // Nombre de la cancha (string)
    @Column(nullable = false, length = 100)
    private String lugar;

    // ---------------------------
    // Relación con Escuela
    // ---------------------------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_escuela", nullable = false)
    private Escuela escuela;

    // ---------------------------
    // Relación con Categoría
    // ---------------------------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    // ---------------------------
    // Relación con Usuario líder
    // (ANTES: Lider, ahora User)
    // ---------------------------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lider", nullable = false)
    private User creador;

    // ---------------------------
    // Relación con Ubicación
    // (localidad + barrio)
    // ---------------------------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ubicacion", nullable = false)
    private Ubicacion ubicacion;
}
