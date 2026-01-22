package com.sistema.academico.aplicacion.servicio;

import com.sistema.academico.aplicacion.dto.request.AsistenciaRequestDTO;
import com.sistema.academico.aplicacion.dto.response.AsistenciaResponseDTO;
import java.time.LocalDate;
import java.util.List;

/**
 * Interfaz para la gestión de asistencia de estudiantes.
 */
public interface IAsistenciaService {

    /**
     * Registra o actualiza la asistencia de un estudiante.
     * @param dto Datos de la asistencia
     * @return Asistencia registrada
     */
    AsistenciaResponseDTO registrarAsistencia(AsistenciaRequestDTO dto);

    /**
     * Registra asistencia masiva para un curso en una fecha.
     * @param cursoId ID del curso
     * @param fecha Fecha de la clase
     * @param dtos Lista de asistencias
     * @return Lista de asistencias procesadas
     */
    List<AsistenciaResponseDTO> registrarAsistenciaMasiva(Long cursoId, LocalDate fecha, List<AsistenciaRequestDTO> dtos);

    /**
     * Obtiene el historial de asistencia de una inscripción.
     * @param inscripcionId ID de la inscripción
     * @return Historial de asistencia
     */
    List<AsistenciaResponseDTO> obtenerAsistenciaPorInscripcion(Long inscripcionId);

    /**
     * Obtiene la asistencia de un curso en una fecha específica.
     * @param cursoId ID del curso
     * @param fecha Fecha a consultar
     * @return Lista de asistencias del curso en esa fecha
     */
    List<AsistenciaResponseDTO> obtenerAsistenciaPorCursoYFecha(Long cursoId, LocalDate fecha);
}