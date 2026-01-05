package com.sistema.academico.aplicacion.servicio;

import com.sistema.academico.aplicacion.dto.request.EstudianteRequestDTO;
import com.sistema.academico.aplicacion.dto.response.EstudianteResponseDTO;
import com.sistema.academico.dominio.enumeracion.Rol;

import java.util.List;

/**
 * Puerto (Interfaz) para operaciones de Estudiante.
 */
public interface IEstudianteService {

    EstudianteResponseDTO crear(EstudianteRequestDTO request);

    EstudianteResponseDTO obtenerPorId(Long id);

    List<EstudianteResponseDTO> listarTodos();

    List<EstudianteResponseDTO> listarActivos();

    EstudianteResponseDTO actualizar(Long id, EstudianteRequestDTO request);

    void desactivar(Long id, Rol rolUsuarioActual);

    void activar(Long id, Rol rolUsuarioActual);

    void eliminar(Long id, Rol rolUsuarioActual);

    /**
     * Buscar estudiantes por nombre, apellido o cédula
     * @param termino Término de búsqueda
     * @return Lista de estudiantes que coinciden con el término
     */
    List<EstudianteResponseDTO> buscarPorTermino(String termino);
}