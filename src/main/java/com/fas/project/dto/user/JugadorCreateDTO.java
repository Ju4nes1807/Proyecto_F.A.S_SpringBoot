package com.fas.project.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JugadorCreateDTO {
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no debe exceder 50 caracteres")
    private String nombres;
    
    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 50, message = "Los apellidos no deben exceder 50 caracteres")
    private String apellidos;
    
    @NotNull(message = "El número de documento es obligatorio")
    private Long numDocumento;
    
    @NotBlank(message = "La fecha de nacimiento es obligatoria")
    private String fechaNacimiento;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    @Size(max = 50, message = "El email no debe exceder 50 caracteres")
    private String email;
    
    @NotBlank(message = "El teléfono es obligatorio")
    @Size(max = 15, message = "El teléfono no debe exceder 15 caracteres")
    private String telefono;
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;
    
    @NotNull(message = "La categoría es obligatoria")
    private Integer idCategoria;
    
    @NotBlank(message = "La posición es obligatoria")
    private String posicion;
    
    @NotNull(message = "El número de camiseta es obligatorio")
    @Min(value = 1, message = "El número de camiseta debe ser mayor a 0")
    @Max(value = 99, message = "El número de camiseta debe ser menor a 100")
    private Integer numeroCamiseta;
}