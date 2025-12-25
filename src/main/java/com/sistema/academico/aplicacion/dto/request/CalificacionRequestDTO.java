package com.sistema.academico.aplicacion.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalificacionRequestDTO {

    @NotNull(message = "La inscripción es obligatoria")
    private Long inscripcionId;

    @NotBlank(message = "El nombre de la evaluación es obligatorio")
    @Size(min = 3, max = 100)
    private String nombreEvaluacion;

    @NotNull(message = "La nota es obligatoria")
    @DecimalMin(value = "0.0", message = "La nota mínima es 0.0")
    @DecimalMax(value = "5.0", message = "La nota máxima es 5.0")
    private BigDecimal nota;

    @NotNull(message = "El porcentaje es obligatorio")
    @Min(value = 1)
    @Max(value = 100)
    private Integer porcentaje;

    @Size(max = 500)
    private String observaciones;
}