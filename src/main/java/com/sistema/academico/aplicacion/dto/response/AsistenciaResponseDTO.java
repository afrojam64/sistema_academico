package com.sistema.academico.aplicacion.dto.response;

import lombok.*;
import java.time.LocalDate;

/**
 * DTO para devolver información de una Asistencia.
 *
 * UBICACIÓN EN ARQUITECTURA:
 * - Capa: APLICACIÓN
 * - Paquete: aplicacion.dto.response
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsistenciaResponseDTO {

    private Long id;
    private Long inscripcionId;
    private String nombreEstudiante;
    private LocalDate fecha;
    private boolean presente;
    private String observaciones;
}