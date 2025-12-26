package com.sistema.academico.aplicacion.servicio;

import com.sistema.academico.aplicacion.dto.request.DepartamentoRequestDTO;
import com.sistema.academico.aplicacion.dto.response.DepartamentoResponseDTO;
import com.sistema.academico.dominio.enumeracion.Rol;

import java.util.List;

/**
 * Puerto (Interfaz) para operaciones de Departamento.
 */
public interface IDepartamentoService {

    DepartamentoResponseDTO crear(DepartamentoRequestDTO request);

    DepartamentoResponseDTO obtenerPorId(Long id);

    List<DepartamentoResponseDTO> listarTodos();

    List<DepartamentoResponseDTO> listarActivos();

    DepartamentoResponseDTO actualizar(Long id, DepartamentoRequestDTO request);

    void desactivar(Long id, Rol rolUsuarioActual);

    void activar(Long id, Rol rolUsuarioActual);

    void eliminar(Long id, Rol rolUsuarioActual);
}