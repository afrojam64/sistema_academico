package com.sistema.academico.aplicacion.servicio;

import com.sistema.academico.aplicacion.dto.request.CalificacionRequestDTO;
import com.sistema.academico.aplicacion.dto.response.CalificacionResponseDTO;
import com.sistema.academico.dominio.enumeracion.Rol;

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
}