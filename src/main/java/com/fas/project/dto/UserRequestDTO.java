package com.fas.project.dto;

import java.util.Set;

import com.fas.project.model.Rol;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {
    @NotBlank(message = "El campo de los nombres es obligatorio")
    private String nombres;
    @NotBlank(message = "El campo de los apellidos es obligatorio")
    private String apellidos;
    @NotBlank(message = "El documento es obligatorio")
    private Long numDocumento;
    @NotBlank(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser una fecha pasada")
    private String fechaNacimiento;
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String email;
    @NotBlank(message = "El teléfono es obligatorio")
    @Max(value = 15, message = "El teléfono no debe exceder los 15 caracteres")
    private String telefono;
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;
    @NotEmpty(message = "Al menos un rol debe ser asignado al usuario")
    @Valid
    private Set<Rol> roles;
}
