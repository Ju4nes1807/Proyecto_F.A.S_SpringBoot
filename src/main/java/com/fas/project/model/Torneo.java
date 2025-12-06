package com.fas.project.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_torneos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Torneo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idTorneo;
    
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Column(nullable = false)
    private LocalDate fechaInicio;
    
    @Column(nullable = false)
    private LocalDate fechaFin;
    
    @Column(length = 500)
    private String descripcion;
    
    @Column(nullable = false)
    private Integer cupoMaximo;
    
    @Column(nullable = false)
    private Integer cuposDisponibles;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoTorneo estado;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ubicacion_id", nullable = false)
    private Ubicacion ubicacion;
    
    @OneToMany(mappedBy = "torneo", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<InscripcionTorneo> inscripciones = new ArrayList<>();
    
    public enum EstadoTorneo {
        PROXIMO("Próximo"),
        EN_CURSO("En Curso"),
        FINALIZADO("Finalizado"),
        CANCELADO("Cancelado");
        
        private final String displayName;
        
        EstadoTorneo(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public boolean puedeInscribirse() {
        return cuposDisponibles > 0 && 
               (estado == EstadoTorneo.PROXIMO) &&
               LocalDate.now().isBefore(fechaInicio);
    }
    
    public boolean puedeCancelarInscripcion() {
        return estado == EstadoTorneo.PROXIMO &&
               LocalDate.now().isBefore(fechaInicio.minusDays(3));
    }
}