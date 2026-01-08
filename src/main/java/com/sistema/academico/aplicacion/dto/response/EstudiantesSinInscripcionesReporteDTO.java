package com.sistema.academico.aplicacion.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para reporte de estudiantes sin inscripciones activas
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstudiantesSinInscripcionesReporteDTO {

    // Estadísticas generales
    private EstadisticasGenerales estadisticas;

    // Lista de estudiantes sin inscripciones
    private List<EstudianteSinInscripcion> estudiantes;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EstadisticasGenerales {
        private Integer totalEstudiantes;
        private Integer estudiantesActivos;
        private Integer estudiantesSinInscripciones;
        private Double porcentajeSinInscripciones;

        // Distribución por carrera
        private List<CarreraDistribucion> distribucionCarreras;

        // Distribución por semestre
        private List<SemestreDistribucion> distribucionSemestres;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CarreraDistribucion {
        private String carrera;
        private Integer totalEstudiantes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SemestreDistribucion {
        private Integer semestre;
        private Integer totalEstudiantes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EstudianteSinInscripcion {
        // Datos básicos del estudiante
        private Long estudianteId;
        private String codigoEstudiante;
        private String nombreCompleto;
        private String cedula;
        private String email;
        private String telefono;

        // Datos académicos
        private String carrera;
        private Integer semestre;
        private String fechaIngreso;

        // Datos de usuario
        private String nombreUsuario;
        private String estadoUsuario;

        // Información adicional
        private Integer diasDesdeIngreso;
        private Boolean esNuevoIngreso; // Menos de 30 días
    }
}