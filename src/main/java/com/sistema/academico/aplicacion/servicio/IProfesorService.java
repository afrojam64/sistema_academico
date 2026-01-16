package com.sistema.academico.aplicacion.servicio;

import com.sistema.academico.aplicacion.dto.request.ProfesorRequestDTO;
import com.sistema.academico.aplicacion.dto.response.CursosPorProfesorReporteDTO;
import com.sistema.academico.aplicacion.dto.response.ProfesorResponseDTO;
import com.sistema.academico.dominio.enumeracion.Rol;

import java.util.List;

/**
 * Puerto (Interfaz) para operaciones de Profesor.
 */
public interface IProfesorService {

    ProfesorResponseDTO crear(ProfesorRequestDTO request);

    ProfesorResponseDTO obtenerPorId(Long id);

    List<ProfesorResponseDTO> listarTodos();

    List<ProfesorResponseDTO> listarActivos();

    ProfesorResponseDTO actualizar(Long id, ProfesorRequestDTO request);

    void desactivar(Long id, Rol rolUsuarioActual);

    void activar(Long id, Rol rolUsuarioActual);

    void eliminar(Long id, Rol rolUsuarioActual);

    /**
     * Cambiar contraseña de un profesor
     */
    void cambiarContrasena(Long id, String nuevaContrasena);

    /**
     * Generar reporte de carga académica por profesor
     * @param profesorId ID del profesor específico (opcional)
     *                   Si es null, devuelve todos los profesores con sus cursos
     * @return DTO con profesores y sus cursos asignados
     */
    CursosPorProfesorReporteDTO generarReporteCursosPorProfesor(Long profesorId);

    /**
     * Buscar profesores por término de búsqueda
     * @param termino Término de búsqueda (nombre, apellido o email)
     * @return Lista de profesores que coinciden con el término
     */
    List<ProfesorResponseDTO> buscarPorTermino(String termino);

    /**
     * Listar solo profesores que tienen cursos activos asignados
     */
    List<ProfesorResponseDTO> listarProfesoresConCursosActivos();
}