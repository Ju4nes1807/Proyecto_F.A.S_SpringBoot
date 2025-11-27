package com.fas.project.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@PrimaryKeyJoinColumn(name = "id_usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Lider extends User {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_escuela", nullable = true)
    private Escuela escuela;
}
