package com.fas.project.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @OneToMany(mappedBy = "escuela", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<Lider> lideres = new ArrayList<>();

    @OneToMany(mappedBy = "escuela", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Jugador> jugadores = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "tb_escuela_ubicacion", joinColumns = @JoinColumn(name = "id_escuela"), inverseJoinColumns = @JoinColumn(name = "id_ubicacion"))
    private Set<Ubicacion> ubicaciones;

    @OneToMany(mappedBy = "escuela", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Categoria> categorias;
}
