package com.sistema.academico.aplicacion.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalificacionBatchRequestDTO {
    @NotNull(message = "El ID del curso es obligatorio")
    private Long cursoId;

    @NotBlank(message = "El nombre de la evaluación es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombreEvaluacion;

    @NotNull(message = "El porcentaje es obligatorio")
    @Min(value = 1, message = "El porcentaje mínimo es 1")
    @Max(value = 100, message = "El porcentaje máximo es 100")
    private Integer porcentaje;

    @Size(max = 500, message = "Las observaciones no pueden exceder 500 caracteres")
    private String observaciones;

    @NotEmpty(message = "Debe haber al menos una calificación")
    @Valid
    private List<CalificacionIndividualDTO> calificaciones;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CalificacionIndividualDTO {
        @NotNull(message = "El ID de la inscripción es obligatorio")
        private Long inscripcionId;

        @NotNull(message = "La nota es obligatoria")
        @DecimalMin(value = "0.0", message = "La nota mínima es 0.0")
        @DecimalMax(value = "5.0", message = "La nota máxima es 5.0")
        private Double nota;
    }
}