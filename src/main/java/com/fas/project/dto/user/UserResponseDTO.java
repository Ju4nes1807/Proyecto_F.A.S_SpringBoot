package com.fas.project.dto.user;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponseDTO {
    private String nombres;
    private String apellidos;
    private String numDocumento;
    private String email;
    private String telefono;
    private String fechaNacimiento;
    private List<String> rol;
}
