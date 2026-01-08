package com.sistema.academico.aplicacion.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InscripcionesPorPeriodoReporteDTO {

    // Información del periodo
    private String periodo;

    // Estadísticas generales
    private EstadisticasGenerales estadisticas;

    // Lista de inscripciones detalladas
    private List<InscripcionDetalle> inscripciones;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EstadisticasGenerales {
        private Integer totalInscripciones;
        private Integer estudiantesUnicos;
        private Integer cursosOfertados;
        private Integer cuposTotales;
        private Integer cuposOcupados;
        private Integer cuposDisponibles;
        private Double porcentajeOcupacion;

        // Distribución por estado
        private Integer inscripcionesActivas;
        private Integer inscripcionesRetiradas;
        private Integer inscripcionesCompletadas;

        // Distribución por carrera
        private List<CarreraDistribucion> distribucionCarreras;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CarreraDistribucion {
        private String carrera;
        private Integer totalInscripciones;
        private Integer estudiantesUnicos;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InscripcionDetalle {
        // Datos del estudiante
        private Long estudianteId;
        private String codigoEstudiante;
        private String nombreEstudiante;
        private String apellidoEstudiante;
        private String emailEstudiante;
        private String carrera;
        private Integer semestre;

        // Datos del curso
        private Long cursoId;
        private String codigoCurso;
        private String nombreCurso;
        private String nombreMateria;
        private String nombreProfesor;
        private String departamento;
        private Integer creditos;

        // Datos de la inscripción
        private Long inscripcionId;
        private LocalDate fechaInscripcion;
        private String estado;

        // Datos de calificaciones (si existen)
        private Integer numeroEvaluaciones;
        private Double promedioActual;
        private Double porcentajeEvaluado;
    }
}