package com.sistema.academico.aplicacion.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para reporte de calificaciones de un curso
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalificacionesCursoReporteDTO {

    // Datos del curso
    private Long cursoId;
    private String codigoCurso;
    private String nombreMateria;
    private String periodo;
    private String nombreProfesor;
    private Integer cupoMaximo;
    private Integer estudiantesInscritos;

    // Lista de estudiantes con sus calificaciones
    private List<EstudianteCalificaciones> estudiantes;

    // Estadísticas generales
    private Estadisticas estadisticas;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EstudianteCalificaciones {
        private Long estudianteId;
        private String codigoEstudiante;
        private String nombreCompleto;
        private String cedula;
        private String estadoInscripcion; // ACTIVO, RETIRADO, COMPLETADO
        private List<DetalleCalificacion> calificaciones;
        private Double promedioFinal;
        private String estado; // APROBADO, REPROBADO, EN_CURSO
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetalleCalificacion {
        private Long calificacionId;
        private String nombreEvaluacion;
        private Double nota;
        private Integer porcentaje;
        private Double notaPonderada;
        private String fechaRegistro;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Estadisticas {
        private Integer totalEstudiantes;
        private Integer estudiantesActivos;
        private Integer estudiantesRetirados;
        private Integer estudiantesCompletados;

        private Double promedioGeneral;
        private Double mejorPromedio;
        private Double peorPromedio;

        private Integer aprobados;
        private Integer reprobados;
        private Integer enCurso;

        private Double tasaAprobacion; // Porcentaje
        private Integer estudiantesRiesgo; // Promedio < 3.0

        // Distribución de notas
        private DistribucionNotas distribucion;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DistribucionNotas {
        private Integer rango0a2; // 0.0 - 2.9
        private Integer rango3a3; // 3.0 - 3.9
        private Integer rango4a5; // 4.0 - 5.0
    }
}