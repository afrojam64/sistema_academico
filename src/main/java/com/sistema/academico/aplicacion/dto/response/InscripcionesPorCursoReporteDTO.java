package com.sistema.academico.aplicacion.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para reporte de inscripciones por curso
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InscripcionesPorCursoReporteDTO {

    // Datos del curso
    private Long cursoId;
    private String codigoCurso;
    private String nombreCurso;
    private String nombreMateria;
    private String codigoMateria;
    private Integer creditos;
    private String periodo;
    private String fechaInicio;
    private String fechaFin;

    // Datos del profesor
    private String nombreProfesor;
    private String emailProfesor;
    private String nombreDepartamento;

    // Estadísticas de inscripciones
    private Integer cupoMaximo;
    private Integer totalInscripciones;
    private Integer inscripcionesActivas;
    private Integer inscripcionesRetiradas;
    private Integer inscripcionesCompletadas;
    private Integer cuposDisponibles;
    private Double porcentajeOcupacion;

    // Lista de estudiantes inscritos
    private List<EstudianteInscrito> estudiantes;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EstudianteInscrito {
        // Datos del estudiante
        private Long estudianteId;
        private String codigoEstudiante;
        private String nombreCompleto;
        private String cedula;
        private String email;
        private String telefono;

        // Datos de la inscripción
        private Long inscripcionId;
        private String fechaInscripcion;
        private String estadoInscripcion; // ACTIVO, RETIRADO, COMPLETADO
        private String fechaActualizacion;

        // Calificaciones (si tiene)
        private Boolean tieneCalificaciones;
        private Integer numeroCalificaciones;
        private Double promedioActual;
        private Integer porcentajeEvaluado;
    }
}