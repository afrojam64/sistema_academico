package com.sistema.academico.aplicacion.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para reporte de cursos activos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CursosActivosReporteDTO {

    // Estadísticas generales
    private Integer totalCursosActivos;
    private Double promedioOcupacion;
    private Integer cursosConCupoCompleto;
    private Integer cursosBajaOcupacion; // < 50%
    private Integer totalEstudiantesInscritos;
    private Integer totalCuposDisponibles;

    // Lista de cursos
    private List<CursoActivo> cursos;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CursoActivo {
        // Datos del curso
        private Long cursoId;
        private String nombreCurso;
        private String codigoCurso;
        private String periodo;
        private String fechaInicio;
        private String fechaFin;

        // Datos de la materia
        private String nombreMateria;
        private String codigoMateria;
        private Integer creditos;

        // Datos del departamento
        private String nombreDepartamento;

        // Datos del profesor
        private String nombreProfesor;
        private String emailProfesor;

        // Estadísticas de ocupación
        private Integer cupoMaximo;
        private Integer cupoActual;
        private Integer cuposDisponibles;
        private Double porcentajeOcupacion;

        // Estado
        private String estadoOcupacion; // COMPLETO, ALTO (>80%), MEDIO (50-80%), BAJO (<50%)
        private String estado; // ACTIVO, INACTIVO
    }
}