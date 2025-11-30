package com.fas.project.dto.user;

import java.util.Set;

import com.fas.project.model.Rol;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO {
    
    @NotBlank(message = "El campo de los nombres es obligatorio")
    @Size(max = 50, message = "Los nombres no deben exceder 50 caracteres")
    private String nombres;
    
    @NotBlank(message = "El campo de los apellidos es obligatorio")
    @Size(max = 50, message = "Los apellidos no deben exceder 50 caracteres")
    private String apellidos;
    
    @NotNull(message = "El documento es obligatorio")
    private Long numDocumento;
    
    @NotBlank(message = "La fecha de nacimiento es obligatoria")
    private String fechaNacimiento;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    @Size(max = 50, message = "El email no debe exceder 50 caracteres")
    private String email;
    
    @NotBlank(message = "El teléfono es obligatorio")
    @Size(max = 15, message = "El teléfono no debe exceder los 15 caracteres")
    private String telefono;
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;
    
    private Set<Rol> roles;
}