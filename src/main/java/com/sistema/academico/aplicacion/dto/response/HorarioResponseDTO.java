package com.sistema.academico.aplicacion.dto.response;

import com.sistema.academico.dominio.enumeracion.DiaSemana;
import lombok.*;
import java.time.LocalTime;

/**
 * DTO para devolver información de un Horario.
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
public class HorarioResponseDTO {

    private Long id;
    private DiaSemana dia;
    private String diaNombre; // Para mostrar "Lunes" en lugar de "LUNES"
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String aula;
}