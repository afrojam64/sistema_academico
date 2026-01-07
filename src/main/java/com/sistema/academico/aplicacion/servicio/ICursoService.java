package com.sistema.academico.aplicacion.servicio;

import com.sistema.academico.aplicacion.dto.request.CursoRequestDTO;
import com.sistema.academico.aplicacion.dto.response.CursoResponseDTO;
import com.sistema.academico.aplicacion.dto.response.CursosActivosReporteDTO;
import com.sistema.academico.aplicacion.dto.response.OcupacionCursosReporteDTO;
import com.sistema.academico.dominio.enumeracion.Rol;

import java.util.List;

/**
 * Puerto (Interfaz) para operaciones de Curso.
 */
public interface ICursoService {

    CursoResponseDTO crear(CursoRequestDTO request);

    CursoResponseDTO obtenerPorId(Long id);

    List<CursoResponseDTO> listarTodos();

    List<CursoResponseDTO> listarActivos();

    List<CursoResponseDTO> listarCursosConCupos();

    CursoResponseDTO actualizar(Long id, CursoRequestDTO request);

    void desactivar(Long id, Rol rolUsuarioActual);

    void activar(Long id, Rol rolUsuarioActual);

    void eliminar(Long id, Rol rolUsuarioActual);

    // ========================================
    // NUEVOS MÉTODOS PARA DASHBOARD PROFESOR
    // ========================================

    /**
     * Listar todos los cursos de un profesor específico
     * @param profesorId ID del profesor
     * @return Lista de cursos del profesor
     */
    List<CursoResponseDTO> listarCursosPorProfesor(Long profesorId);

    /**
     * Listar cursos ACTIVOS de un profesor específico
     * @param profesorId ID del profesor
     * @return Lista de cursos activos del profesor
     */
    List<CursoResponseDTO> listarCursosActivosPorProfesor(Long profesorId);

    /**
     * Buscar cursos por código, nombre de materia o periodo
     * @param termino Término de búsqueda
     * @return Lista de cursos que coinciden con el término
     */
    List<CursoResponseDTO> buscarPorTermino(String termino);

    /**
     * Generar reporte de cursos activos
     * @return DTO con todos los cursos activos y sus estadísticas
     */
    CursosActivosReporteDTO generarReporteCursosActivos();

    /**
     * Generar reporte de análisis de ocupación de cursos
     * @return DTO con análisis estadístico de ocupación
     */
    OcupacionCursosReporteDTO generarReporteOcupacionCursos();
}