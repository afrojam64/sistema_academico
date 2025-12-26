package com.sistema.academico.aplicacion.servicio;

import com.sistema.academico.aplicacion.dto.request.CursoRequestDTO;
import com.sistema.academico.aplicacion.dto.response.CursoResponseDTO;
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
}