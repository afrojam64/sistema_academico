package com.sistema.academico.aplicacion.servicio;

import com.sistema.academico.aplicacion.dto.request.CalificacionRequestDTO;
import com.sistema.academico.aplicacion.dto.response.CalificacionResponseDTO;
import com.sistema.academico.aplicacion.dto.response.CalificacionesCursoReporteDTO;
import com.sistema.academico.aplicacion.dto.response.CalificacionesEstudianteReporteDTO;
import com.sistema.academico.aplicacion.dto.response.EstudiantesEnRiesgoReporteDTO;
import com.sistema.academico.dominio.enumeracion.Rol;

import java.time.LocalDate;
import java.util.List;

/**
 * Puerto (Interfaz) para operaciones de Calificación.
 */
public interface ICalificacionService {

    CalificacionResponseDTO registrar(CalificacionRequestDTO request);

    CalificacionResponseDTO obtenerPorId(Long id);

    List<CalificacionResponseDTO> listarTodas();

    List<CalificacionResponseDTO> listarPorInscripcion(Long inscripcionId);

    CalificacionResponseDTO actualizar(Long id, CalificacionRequestDTO request);

    void eliminar(Long id, Rol rolUsuarioActual);

    // ========================================
    // NUEVOS MÉTODOS PARA DASHBOARDS
    // ========================================

    /**
     * Listar todas las calificaciones de un estudiante (todas sus inscripciones)
     * @param estudianteId ID del estudiante
     * @return Lista de calificaciones del estudiante
     */
    List<CalificacionResponseDTO> listarPorEstudiante(Long estudianteId);

    /**
     * Listar todas las calificaciones de un curso específico
     * @param cursoId ID del curso
     * @return Lista de calificaciones del curso
     */
    List<CalificacionResponseDTO> listarPorCurso(Long cursoId);

    /**
     * Generar reporte de calificaciones de un estudiante
     * @param estudianteId ID del estudiante
     * @return DTO con todas las calificaciones agrupadas por curso
     */
    CalificacionesEstudianteReporteDTO generarReporteEstudiante(Long estudianteId);

    /**
     * Generar reporte de calificaciones de un curso
     * @param cursoId ID del curso
     * @param fechaInicio Fecha inicial del rango (opcional)
     * @param fechaFin Fecha final del rango (opcional)
     * @return DTO con todas las calificaciones y estadísticas del curso
     */
    CalificacionesCursoReporteDTO generarReporteCurso(
            Long cursoId,
            LocalDate fechaInicio,
            LocalDate fechaFin
    );

    /**
     * Generar reporte de estudiantes en riesgo académico
     * @param estudianteId ID del estudiante específico (opcional)
     *                     Si es null, devuelve todos los estudiantes en riesgo
     * @return DTO con estudiantes en riesgo y sus cursos problemáticos
     */
    EstudiantesEnRiesgoReporteDTO generarReporteEstudiantesEnRiesgo(Long estudianteId);
}