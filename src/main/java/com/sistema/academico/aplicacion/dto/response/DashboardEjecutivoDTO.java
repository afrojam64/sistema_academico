package com.sistema.academico.aplicacion.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para Dashboard Ejecutivo
 * Contiene un resumen general del sistema académico con indicadores clave
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardEjecutivoDTO {

    // Resumen General del Sistema
    private ResumenGeneral resumenGeneral;

    // Información del Periodo Académico Actual
    private PeriodoActual periodoActual;

    // Análisis de Ocupación de Cursos
    private OcupacionCursos ocupacionCursos;

    // Top 5 Departamentos por Inscripciones
    private List<DepartamentoTop> topDepartamentos;

    // Alertas del Sistema
    private Alertas alertas;

    // =========================================================================
    // CLASES INTERNAS
    // =========================================================================

    /**
     * Resumen General - Totales del sistema
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResumenGeneral {
        private Integer totalEstudiantes;          // Total estudiantes activos
        private Integer totalProfesores;           // Total profesores activos
        private Integer totalCursos;               // Total cursos activos
        private Integer totalMaterias;             // Total materias activas
        private Integer totalDepartamentos;        // Total departamentos
        private Integer totalInscripciones;        // Total inscripciones activas
    }

    /**
     * Periodo Actual - Información del periodo académico vigente
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PeriodoActual {
        private String periodo;                    // Nombre del periodo (ej: "2026-1")
        private Integer cursosOfertados;           // Cursos disponibles en el periodo
        private Integer estudiantesInscritos;      // Estudiantes únicos inscritos
        private Integer inscripcionesTotales;      // Total de inscripciones
        private Integer cuposDisponibles;          // Cupos libres
        private Double promedioInscripcionesPorEstudiante; // Promedio de inscripciones
    }

    /**
     * Ocupación de Cursos - Análisis de cupos
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OcupacionCursos {
        private Integer cursosCompletos;           // Cursos al 100%
        private Integer cursosAltaOcupacion;       // Cursos >80%
        private Integer cursosMediaOcupacion;      // Cursos 50-80%
        private Integer cursosBajaOcupacion;       // Cursos <50%
        private Double ocupacionPromedio;          // % promedio de ocupación
        private Integer totalCuposOfrecidos;       // Total de cupos disponibles
        private Integer totalCuposOcupados;        // Total de cupos ocupados
        private Integer cuposDisponibles;          // Cupos libres
    }

    /**
     * Departamento Top - Departamento con sus estadísticas
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DepartamentoTop {
        private String nombreDepartamento;         // Nombre del departamento
        private Integer totalInscripciones;        // Total de inscripciones
        private Integer totalEstudiantes;          // Estudiantes únicos
        private Integer totalCursos;               // Cursos activos
        private Double porcentaje;                 // % del total de inscripciones
    }

    /**
     * Alertas - Indicadores que requieren atención
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Alertas {
        private Integer estudiantesEnRiesgo;       // Estudiantes con promedio < 3.0
        private Integer estudiantesSinInscripciones; // Estudiantes activos sin inscripciones
        private Integer cursosConBajaOcupacion;    // Cursos con ocupación < 50%
    }
}