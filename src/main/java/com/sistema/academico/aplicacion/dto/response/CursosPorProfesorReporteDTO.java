package com.sistema.academico.aplicacion.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para reporte de carga académica por profesor
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CursosPorProfesorReporteDTO {

    // Estadísticas generales (cuando se consultan todos los profesores)
    private Integer totalProfesores;
    private Integer totalCursosAsignados;
    private Integer totalEstudiantes;
    private Double promedioOcupacion;

    // Lista de profesores con sus cursos
    private List<ProfesorConCursos> profesores;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfesorConCursos {
        // Datos del profesor
        private Long profesorId;
        private String nombreCompleto;
        private String email;
        private String telefono;
        private String especialidad;
        private String nombreDepartamento;
        private String fechaContratacion;
        private String estado;

        // Estadísticas del profesor
        private Integer totalCursosActivos;
        private Integer totalEstudiantes;
        private Double promedioOcupacion;
        private String cargaAcademica; // BAJA, MEDIA, ALTA

        // Lista de cursos asignados
        private List<CursoAsignado> cursos;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CursoAsignado {
        private Long cursoId;
        private String nombreCurso;
        private String codigoCurso;
        private String nombreMateria;
        private Integer creditos;
        private String periodo;
        private String fechaInicio;
        private String fechaFin;
        private Integer cupoMaximo;
        private Integer cupoActual;
        private Integer cuposDisponibles;
        private Double porcentajeOcupacion;
        private String estadoCurso;
    }
}