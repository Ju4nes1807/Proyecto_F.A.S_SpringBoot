package com.fas.project.model;

import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_categorias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCategoria;
    @Column(nullable = false, length = 50)
    private String nombre;
    @Column(nullable = false, length = 30)
    private String rangoEdad;

    @OneToMany(mappedBy = "categoria", fetch = FetchType.LAZY)
    private List<Jugador> jugadores;

    @ManyToOne
    @JoinColumn(name = "escuela_id", nullable = false)
    private Escuela escuela;

}
