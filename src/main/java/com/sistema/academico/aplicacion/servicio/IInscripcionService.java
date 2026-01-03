package com.sistema.academico.aplicacion.servicio;

import com.sistema.academico.aplicacion.dto.request.InscripcionRequestDTO;
import com.sistema.academico.aplicacion.dto.response.InscripcionResponseDTO;
import com.sistema.academico.dominio.enumeracion.Rol;

import java.util.List;

/**
 * Puerto (Interfaz) para operaciones de Inscripción.
 */
public interface IInscripcionService {

    InscripcionResponseDTO crear(InscripcionRequestDTO request);

    InscripcionResponseDTO obtenerPorId(Long id);

    List<InscripcionResponseDTO> listarTodas();

    List<InscripcionResponseDTO> listarActivas();

    void retirar(Long id);

    void completar(Long id, Rol rolUsuarioActual);

    void eliminar(Long id, Rol rolUsuarioActual);

    // ========================================
    // NUEVOS MÉTODOS PARA DASHBOARDS
    // ========================================

    /**
     * Listar todas las inscripciones de un estudiante específico
     * @param estudianteId ID del estudiante
     * @return Lista de inscripciones del estudiante
     */
    List<InscripcionResponseDTO> listarPorEstudiante(Long estudianteId);

    /**
     * Listar inscripciones ACTIVAS de un estudiante específico
     * @param estudianteId ID del estudiante
     * @return Lista de inscripciones activas del estudiante
     */
    List<InscripcionResponseDTO> listarActivasPorEstudiante(Long estudianteId);

    /**
     * Listar todas las inscripciones de un curso específico
     * @param cursoId ID del curso
     * @return Lista de inscripciones del curso
     */
    List<InscripcionResponseDTO> listarPorCurso(Long cursoId);
}