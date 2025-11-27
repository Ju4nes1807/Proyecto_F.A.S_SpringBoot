package com.fas.project.model;

import java.util.Set;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_escuelas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Escuela {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idEscuela;
    @Column(nullable = false, length = 100)
    private String nombre;
    @Column(nullable = false, length = 30)
    private String telefono;
    @Column(nullable = true, unique = true, length = 50)
    private String email;

    @OneToMany(mappedBy = "escuela", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Lider> lideres;

    @OneToMany(mappedBy = "escuela", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Jugador> jugadores;

    @ManyToMany
    @JoinTable(name = "tb_escuela_ubicacion", joinColumns = @JoinColumn(name = "id_escuela"), inverseJoinColumns = @JoinColumn(name = "id_ubicacion"))
    private Set<Ubicacion> ubicaciones;

    @ManyToMany
    @JoinTable(name = "tb_escuela_categoria", joinColumns = @JoinColumn(name = "id_escuela"), inverseJoinColumns = @JoinColumn(name = "id_categoria"))
    private Set<Categoria> categorias;
}
