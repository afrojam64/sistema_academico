package com.sistema.academico.aplicacion.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para reporte de calificaciones de un estudiante
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalificacionesEstudianteReporteDTO {

    // Datos del estudiante
    private Long estudianteId;
    private String nombreCompleto;
    private String codigo;
    private String email;

    // Calificaciones agrupadas por curso
    private List<CursoCalificaciones> cursos;

    // Estadísticas generales
    private Double promedioGeneral;
    private Integer totalCursos;
    private Integer cursosAprobados;
    private Integer cursosReprobados;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CursoCalificaciones {
        private String nombreCurso;
        private String codigoCurso;
        private String periodo;
        private List<DetalleCalificacion> calificaciones;
        private Double promedioFinal;
        private String estado; // APROBADO, REPROBADO, EN_CURSO
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetalleCalificacion {
        private String nombreEvaluacion;
        private Double nota;
        private Integer porcentaje;
        private Double notaPonderada;
        private String estado; // APROBADA, REPROBADA
        private String fechaRegistro;
    }
}