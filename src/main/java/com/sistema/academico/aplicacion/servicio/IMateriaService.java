package com.sistema.academico.aplicacion.servicio;

import com.sistema.academico.aplicacion.dto.request.MateriaRequestDTO;
import com.sistema.academico.aplicacion.dto.response.MateriaResponseDTO;
import com.sistema.academico.dominio.enumeracion.Rol;

import java.util.List;

/**
 * Puerto (Interfaz) para operaciones de Materia.
 */
public interface IMateriaService {

    MateriaResponseDTO crear(MateriaRequestDTO request);

    MateriaResponseDTO obtenerPorId(Long id);

    List<MateriaResponseDTO> listarTodas();

    List<MateriaResponseDTO> listarActivas();

    MateriaResponseDTO actualizar(Long id, MateriaRequestDTO request);

    void desactivar(Long id, Rol rolUsuarioActual);

    void activar(Long id, Rol rolUsuarioActual);

    void eliminar(Long id, Rol rolUsuarioActual);
}