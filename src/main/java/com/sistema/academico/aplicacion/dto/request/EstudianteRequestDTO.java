package com.sistema.academico.aplicacion.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstudianteRequestDTO {

    // Datos del usuario (se crearán automáticamente)
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

    // Datos específicos del estudiante
    @NotBlank(message = "El código de estudiante es obligatorio")
    @Size(max = 20)
    private String codigoEstudiante;

    @Size(max = 100)
    private String carrera;

    @Min(value = 1, message = "El semestre debe ser al menos 1")
    @Max(value = 12, message = "El semestre no puede ser mayor a 12")
    private Integer semestre;

    private LocalDate fechaIngreso;
}