package com.sistema.academico.aplicacion.dto.response;

import lombok.*;

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
    private String curso;
    private String materia;

    private String nombreEvaluacion;
    private String nota;
    private Integer porcentaje;
    private String notaPonderada;
    private String fechaCalificacion;
    private String observaciones;
    private Boolean esAprobada;
}
