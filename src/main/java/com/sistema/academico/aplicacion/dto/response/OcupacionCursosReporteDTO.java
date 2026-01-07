package com.sistema.academico.aplicacion.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para reporte de análisis de ocupación de cursos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OcupacionCursosReporteDTO {

    // Estadísticas generales
    private Integer totalCursos;
    private Double promedioOcupacionGlobal;
    private Integer totalCuposOfrecidos;
    private Integer totalCuposOcupados;
    private Integer totalCuposDisponibles;

    // Distribución por rangos de ocupación
    private DistribucionOcupacion distribucion;

    // Análisis por departamento
    private List<OcupacionPorDepartamento> departamentos;

    // Top cursos
    private List<CursoDetalle> cursosCompletos;      // 100%
    private List<CursoDetalle> cursosMasDemandados;  // >90%
    private List<CursoDetalle> cursosMenosDemandados; // <25%

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DistribucionOcupacion {
        private Integer rango0a25;      // 0-25%
        private Integer rango25a50;     // 25-50%
        private Integer rango50a75;     // 50-75%
        private Integer rango75a100;    // 75-100%
        private Integer rango100;       // 100% (completos)

        // Porcentajes
        private Double porcentaje0a25;
        private Double porcentaje25a50;
        private Double porcentaje50a75;
        private Double porcentaje75a100;
        private Double porcentaje100;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OcupacionPorDepartamento {
        private String nombreDepartamento;
        private Integer totalCursos;
        private Double promedioOcupacion;
        private Integer cursosCompletos;
        private Integer totalEstudiantes;
        private Integer totalCupos;
        private Double porcentajeOcupacion;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CursoDetalle {
        private Long cursoId;
        private String nombreCurso;
        private String codigoCurso;
        private String nombreMateria;
        private String nombreDepartamento;
        private String nombreProfesor;
        private String periodo;
        private Integer cupoMaximo;
        private Integer cupoActual;
        private Double porcentajeOcupacion;
    }
}