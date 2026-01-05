package com.sistema.academico.aplicacion.servicio;

import com.sistema.academico.aplicacion.dto.request.UsuarioRequestDTO;
import com.sistema.academico.aplicacion.dto.response.UsuarioResponseDTO;
import com.sistema.academico.dominio.enumeracion.Rol;

import java.util.List;

/**
 * Puerto (Interfaz) para operaciones de Usuario.
 * Define el contrato que debe cumplir cualquier implementación del servicio de usuarios.
 */
public interface IUsuarioService {

    /**
     * Crea un nuevo usuario
     * @param request DTO con datos del usuario
     * @return DTO con datos del usuario creado
     */
    UsuarioResponseDTO crear(UsuarioRequestDTO request);

    /**
     * Obtiene un usuario por su ID
     * @param id ID del usuario
     * @return DTO con datos del usuario
     */
    UsuarioResponseDTO obtenerPorId(Long id);

    /**
     * Lista todos los usuarios
     * @return Lista de DTOs con datos de usuarios
     */
    List<UsuarioResponseDTO> listarTodos();

    /**
     * Lista usuarios activos
     * @return Lista de DTOs con datos de usuarios activos
     */
    List<UsuarioResponseDTO> listarActivos();

    /**
     * Actualiza un usuario
     * @param id ID del usuario
     * @param request DTO con datos a actualizar
     * @return DTO con datos del usuario actualizado
     */
    UsuarioResponseDTO actualizar(Long id, UsuarioRequestDTO request);

    /**
     * Desactiva un usuario (soft delete)
     * @param id ID del usuario
     * @param rolUsuarioActual Rol del usuario que ejecuta la acción
     */
    void desactivar(Long id, Rol rolUsuarioActual);

    /**
     * Activa un usuario
     * @param id ID del usuario
     * @param rolUsuarioActual Rol del usuario que ejecuta la acción
     */
    void activar(Long id, Rol rolUsuarioActual);

    /**
     * Elimina físicamente un usuario (hard delete)
     * Solo SUPER_ADMIN puede ejecutar esta operación
     * @param id ID del usuario
     * @param rolUsuarioActual Rol del usuario que ejecuta la acción
     */
    void eliminar(Long id, Rol rolUsuarioActual);

    /**
     * Cambiar contraseña de un usuario
     * @param id ID del usuario
     * @param nuevaContrasena Nueva contraseña (se encriptará automáticamente)
     */
    void cambiarContrasena(Long id, String nuevaContrasena);
}