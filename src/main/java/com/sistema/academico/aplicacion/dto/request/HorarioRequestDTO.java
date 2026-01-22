package com.sistema.academico.aplicacion.dto.request;

import com.sistema.academico.dominio.enumeracion.DiaSemana;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalTime;

/**
 * DTO para la creación o actualización de un Horario.
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
public class HorarioRequestDTO {

    @NotNull(message = "El día de la semana es obligatorio")
    private DiaSemana dia;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horaInicio;

    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime horaFin;

    private String aula;
}