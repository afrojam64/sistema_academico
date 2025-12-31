package com.sistema.academico.aplicacion.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

/**
 * DTO para crear un nuevo profesor
 * NO requiere usuarioId porque se crea automáticamente
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfesorRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 100)
    private String apellido;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "La cédula es obligatoria")
    @Size(min = 5, max = 20)
    private String cedula;

    @Size(max = 20)
    private String telefono;

    @Size(max = 200)
    private String especialidad;

    @NotNull(message = "El departamento es obligatorio")
    private Long departamentoId;

    private LocalDate fechaContratacion;
}