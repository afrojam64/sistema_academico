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
}