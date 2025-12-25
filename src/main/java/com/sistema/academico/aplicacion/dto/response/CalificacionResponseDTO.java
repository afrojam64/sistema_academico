package com.sistema.academico.aplicacion.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalificacionResponseDTO {

    private Long id;
    private String nombreEvaluacion;
    private String nota;
    private Integer porcentaje;
    private String notaPonderada;
    private String fechaCalificacion;
    private String observaciones;
    private Boolean esAprobada;
}
