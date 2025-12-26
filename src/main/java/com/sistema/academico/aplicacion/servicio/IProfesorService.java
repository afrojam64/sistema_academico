package com.sistema.academico.aplicacion.servicio;

import com.sistema.academico.aplicacion.dto.request.ProfesorRequestDTO;
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
}