package com.sistema.academico.aplicacion.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CursoRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100)
    private String nombre;

    @NotBlank(message = "El código es obligatorio")
    @Size(min = 3, max = 20)
    private String codigo;

    @NotNull(message = "La materia es obligatoria")
    private Long materiaId;

    @NotNull(message = "El profesor es obligatorio")
    private Long profesorId;

    @NotNull(message = "El cupo máximo es obligatorio")
    @Min(value = 1)
    @Max(value = 100)
    private Integer cupoMaximo;

    @NotBlank(message = "El periodo es obligatorio")
    @Size(max = 20)
    private String periodo;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate fechaFin;
}