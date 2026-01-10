package com.sistema.academico.aplicacion.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para Reporte de Tasa de Aprobación
 * Contiene porcentajes de aprobación por materia, curso, periodo y departamento
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TasaAprobacionDTO {

    // Resumen General de Aprobación
    private ResumenGeneral resumenGeneral;

    // Tasa de Aprobación por Materia
    private List<TasaAprobacionMateria> tasaPorMateria;

    // Tasa de Aprobación por Curso
    private List<TasaAprobacionCurso> tasaPorCurso;

    // Tasa de Aprobación por Periodo
    private List<TasaAprobacionPeriodo> tasaPorPeriodo;

    // Tasa de Aprobación por Departamento
    private List<TasaAprobacionDepartamento> tasaPorDepartamento;

    // =========================================================================
    // CLASES INTERNAS
    // =========================================================================

    /**
     * Resumen General - Métricas globales de aprobación
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResumenGeneral {
        private Long totalEstudiantesEvaluados;    // Total de estudiantes con calificaciones
        private Long estudiantesAprobados;          // Estudiantes con promedio >= 3.0
        private Long estudiantesReprobados;         // Estudiantes con promedio < 3.0
        private Double tasaAprobacionGeneral;       // Porcentaje general de aprobación
        private Double promedioGeneralSistema;      // Promedio general de todas las calificaciones
    }

    /**
     * Tasa de Aprobación por Materia
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TasaAprobacionMateria {
        private String nombreMateria;               // Nombre de la materia
        private String codigoMateria;               // Código de la materia
        private String nombreDepartamento;          // Departamento al que pertenece
        private Long totalEstudiantes;              // Total de estudiantes inscritos
        private Long aprobados;                     // Estudiantes que aprobaron (>= 3.0)
        private Long reprobados;                    // Estudiantes que reprobaron (< 3.0)
        private Double tasaAprobacion;              // Porcentaje de aprobación
        private Double promedioGeneral;             // Promedio de notas de la materia
        private Integer ranking;                    // Posición en el ranking
    }

    /**
     * Tasa de Aprobación por Curso
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TasaAprobacionCurso {
        private String codigoCurso;                 // Código del curso
        private String nombreMateria;               // Nombre de la materia
        private String nombreProfesor;              // Profesor que dicta el curso
        private String periodo;                     // Periodo académico
        private Long totalEstudiantes;              // Total de estudiantes inscritos
        private Long aprobados;                     // Estudiantes que aprobaron
        private Long reprobados;                    // Estudiantes que reprobaron
        private Double tasaAprobacion;              // Porcentaje de aprobación
        private Double promedioGeneral;             // Promedio de notas del curso
    }

    /**
     * Tasa de Aprobación por Periodo
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TasaAprobacionPeriodo {
        private String periodo;                     // Periodo académico (Ej: "2026-1")
        private Long totalEstudiantes;              // Estudiantes únicos evaluados
        private Long aprobados;                     // Estudiantes que aprobaron
        private Long reprobados;                    // Estudiantes que reprobaron
        private Double tasaAprobacion;              // Porcentaje de aprobación
        private Double promedioGeneral;             // Promedio de notas del periodo
        private Double tendencia;                   // Diferencia % con periodo anterior
        private String tendenciaIndicador;          // "MEJORA", "ESTABLE", "DISMINUYE"
    }

    /**
     * Tasa de Aprobación por Departamento
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TasaAprobacionDepartamento {
        private String nombreDepartamento;          // Nombre del departamento
        private Long totalEstudiantes;              // Total de estudiantes evaluados
        private Long aprobados;                     // Estudiantes que aprobaron
        private Long reprobados;                    // Estudiantes que reprobaron
        private Double tasaAprobacion;              // Porcentaje de aprobación
        private Double promedioGeneral;             // Promedio de notas del departamento
        private Integer ranking;                    // Posición en el ranking (1 = mejor)
        private Long totalMaterias;                 // Cantidad de materias del departamento
    }
}