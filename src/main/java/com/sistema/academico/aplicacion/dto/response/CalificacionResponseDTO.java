package com.sistema.academico.aplicacion.dto.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalificacionResponseDTO {

    private Long id;
    private Long inscripcionId;

    //Campos adicionales paara mostrar en calificaciones
    private String estudiante;
    private String codigoEstudiante;
    private String estudianteNombre;
    private String curso;
    private String materia;

    private String nombreEvaluacion;
    private Double nota;
    private Integer porcentaje;
    private String notaPonderada;
    private LocalDate fechaCalificacion;
    private String observaciones;
    private Boolean esAprobada;
}
