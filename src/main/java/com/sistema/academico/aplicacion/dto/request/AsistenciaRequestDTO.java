package com.sistema.academico.aplicacion.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;

/**
 * DTO para registrar la asistencia de un estudiante.
 *
 * UBICACIÓN EN ARQUITECTURA:
 * - Capa: APLICACIÓN
 * - Paquete: aplicacion.dto.request
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsistenciaRequestDTO {

    @NotNull(message = "La inscripción es obligatoria")
    private Long inscripcionId;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @NotNull(message = "El estado de asistencia es obligatorio")
    private Boolean presente;

    private String observaciones;
}