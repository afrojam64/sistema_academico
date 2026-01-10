package com.sistema.academico.aplicacion.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para Reporte de Distribución de Calificaciones
 * Contiene estadísticas y distribución de notas por diferentes criterios
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistribucionCalificacionesDTO {

    // Estadísticas Generales del Sistema
    private EstadisticasGenerales estadisticasGenerales;

    // Distribución por Rangos de Notas
    private DistribucionRangos distribucionRangos;

    // Distribución por Departamento
    private List<DistribucionDepartamento> distribucionPorDepartamento;

    // Distribución por Materia (Top 10)
    private List<DistribucionMateria> topMaterias;

    // Distribución por Periodo Académico
    private List<DistribucionPeriodo> distribucionPorPeriodo;

    // =========================================================================
    // CLASES INTERNAS
    // =========================================================================

    /**
     * Estadísticas Generales - Métricas globales del sistema
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EstadisticasGenerales {
        private Long totalCalificaciones;           // Total de calificaciones registradas
        private Double promedioGeneral;             // Promedio general del sistema
        private Double notaMasAlta;                 // Nota más alta registrada
        private Double notaMasBaja;                 // Nota más baja registrada
        private Double desviacionEstandar;          // Desviación estándar (opcional)
    }

    /**
     * Distribución por Rangos - Agrupación de notas en rangos
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DistribucionRangos {
        private RangoNota rango0a2;                 // Reprobado: 0.0 - 2.9
        private RangoNota rango3a3;                 // Aceptable: 3.0 - 3.9
        private RangoNota rango4a5;                 // Sobresaliente: 4.0 - 5.0
    }

    /**
     * Rango de Nota - Conteo y porcentaje de un rango específico
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RangoNota {
        private String rangoLabel;                  // Ej: "0.0 - 2.9"
        private String categoria;                   // Ej: "Reprobado"
        private Long cantidad;                      // Cantidad de calificaciones
        private Double porcentaje;                  // Porcentaje del total
        private String color;                       // Color para gráficos (Ej: "#ef4444")
    }

    /**
     * Distribución por Departamento - Estadísticas por departamento
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DistribucionDepartamento {
        private String nombreDepartamento;          // Nombre del departamento
        private Long totalCalificaciones;           // Total de calificaciones
        private Double promedioNotas;               // Promedio de notas
        private Long cantidadRango0a2;              // Cantidad en rango bajo
        private Long cantidadRango3a3;              // Cantidad en rango medio
        private Long cantidadRango4a5;              // Cantidad en rango alto
        private Double porcentajeAprobacion;        // % de notas >= 3.0
    }

    /**
     * Distribución por Materia - Estadísticas por materia
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DistribucionMateria {
        private String nombreMateria;               // Nombre de la materia
        private String nombreDepartamento;          // Departamento al que pertenece
        private Long totalCalificaciones;           // Total de calificaciones
        private Double promedioNotas;               // Promedio de notas
        private Long cantidadRango0a2;              // Cantidad en rango bajo
        private Long cantidadRango3a3;              // Cantidad en rango medio
        private Long cantidadRango4a5;              // Cantidad en rango alto
        private Double porcentajeAprobacion;        // % de notas >= 3.0
    }

    /**
     * Distribución por Periodo - Estadísticas por periodo académico
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DistribucionPeriodo {
        private String periodo;                     // Periodo académico (Ej: "2026-1")
        private Long totalCalificaciones;           // Total de calificaciones
        private Double promedioNotas;               // Promedio de notas
        private Long cantidadRango0a2;              // Cantidad en rango bajo
        private Long cantidadRango3a3;              // Cantidad en rango medio
        private Long cantidadRango4a5;              // Cantidad en rango alto
        private Double porcentajeAprobacion;        // % de notas >= 3.0
    }
}