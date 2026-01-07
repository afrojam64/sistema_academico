package com.sistema.academico.aplicacion.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para reporte de estudiantes en riesgo académico
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstudiantesEnRiesgoReporteDTO {

    // Datos generales del reporte
    private Integer totalEstudiantesEnRiesgo;
    private Double promedioGeneralRiesgo;

    // Lista de estudiantes en riesgo
    private List<EstudianteEnRiesgo> estudiantes;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EstudianteEnRiesgo {
        // Datos del estudiante
        private Long estudianteId;
        private String codigoEstudiante;
        private String nombreCompleto;
        private String cedula;
        private String email;

        // Estadísticas del estudiante
        private Double promedioGeneral; // Promedio de todos sus cursos
        private Integer totalCursosInscritos;
        private Integer cursosEnRiesgo;

        // Detalle de cursos en riesgo
        private List<CursoEnRiesgo> cursosConRiesgo;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CursoEnRiesgo {
        // Datos del curso
        private Long cursoId;
        private String codigoCurso;
        private String nombreMateria;
        private String periodo;

        // Datos del profesor
        private String nombreProfesor;

        // Calificaciones actuales
        private List<CalificacionDetalle> calificaciones;

        // Estadísticas del curso
        private Double promedioNotas; // Promedio simple de las notas (proyección)
        private Double notaAcumulada; // Suma de notas ponderadas
        private Integer porcentajeEvaluado; // Suma de porcentajes de evaluaciones registradas
        private Integer porcentajePendiente; // 100 - porcentajeEvaluado
        private Double notaNecesaria; // Nota que necesita en lo que falta para aprobar

        // Estado
        private String nivelRiesgo; // ALTO, MEDIO, BAJO
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CalificacionDetalle {
        private String nombreEvaluacion;
        private Double nota;
        private Integer porcentaje;
        private Double notaPonderada;
        private String fechaCalificacion;
    }
}