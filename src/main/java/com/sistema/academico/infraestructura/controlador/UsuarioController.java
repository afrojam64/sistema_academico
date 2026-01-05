package com.sistema.academico.infraestructura.controlador;

import com.sistema.academico.aplicacion.dto.request.CambiarContrasenaRequestDTO;
import com.sistema.academico.aplicacion.dto.request.UsuarioRequestDTO;
import com.sistema.academico.aplicacion.dto.response.UsuarioResponseDTO;
import com.sistema.academico.aplicacion.servicio.IUsuarioService;
import com.sistema.academico.dominio.enumeracion.Rol;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gestionar Usuarios
 *
 * Endpoints disponibles:
 * - POST   /api/usuarios          → Crear usuario
 * - GET    /api/usuarios          → Listar todos
 * - GET    /api/usuarios/activos  → Listar activos
 * - GET    /api/usuarios/{id}     → Obtener por ID
 * - PUT    /api/usuarios/{id}     → Actualizar
 * - PATCH  /api/usuarios/{id}/desactivar → Desactivar
 * - PATCH  /api/usuarios/{id}/activar    → Activar
 * - DELETE /api/usuarios/{id}     → Eliminar físicamente
 */
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final IUsuarioService usuarioService;

    /**
     * Crear un nuevo usuario
     *
     * POST /api/usuarios
     *
     * @param request Datos del usuario
     * @return Usuario creado con HTTP 201 CREATED
     */
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crear(@Valid @RequestBody UsuarioRequestDTO request) {
        UsuarioResponseDTO usuario = usuarioService.crear(request);
        return new ResponseEntity<>(usuario, HttpStatus.CREATED);
    }

    /**
     * Obtener un usuario por ID
     *
     * GET /api/usuarios/{id}
     *
     * @param id ID del usuario
     * @return Usuario encontrado con HTTP 200 OK
     */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> obtenerPorId(@PathVariable Long id) {
        UsuarioResponseDTO usuario = usuarioService.obtenerPorId(id);
        return ResponseEntity.ok(usuario);
    }

    /**
     * Listar todos los usuarios (incluyendo inactivos)
     *
     * GET /api/usuarios
     *
     * @return Lista de usuarios con HTTP 200 OK
     */
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        List<UsuarioResponseDTO> usuarios = usuarioService.listarTodos();
        return ResponseEntity.ok(usuarios);
    }

    /**
     * Listar solo usuarios activos
     *
     * GET /api/usuarios/activos
     *
     * @return Lista de usuarios activos con HTTP 200 OK
     */
    @GetMapping("/activos")
    public ResponseEntity<List<UsuarioResponseDTO>> listarActivos() {
        List<UsuarioResponseDTO> usuarios = usuarioService.listarActivos();
        return ResponseEntity.ok(usuarios);
    }

    /**
     * Actualizar un usuario existente
     *
     * PUT /api/usuarios/{id}
     *
     * @param id ID del usuario
     * @param request Datos actualizados
     * @return Usuario actualizado con HTTP 200 OK
     */
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRequestDTO request) {
        UsuarioResponseDTO usuario = usuarioService.actualizar(id, request);
        return ResponseEntity.ok(usuario);
    }

    /**
     * Desactivar un usuario (soft delete)
     *
     * PATCH /api/usuarios/{id}/desactivar
     *
     * Requiere rol con permisos de desactivación
     *
     * @param id ID del usuario
     * @param rolUsuario Rol del usuario que hace la petición
     * @return HTTP 204 NO CONTENT
     */
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(
            @PathVariable Long id,
            @RequestParam Rol rolUsuario) {
        usuarioService.desactivar(id, rolUsuario);
        return ResponseEntity.noContent().build();
    }

    /**
     * Activar un usuario desactivado
     *
     * PATCH /api/usuarios/{id}/activar
     *
     * Requiere rol con permisos de desactivación
     *
     * @param id ID del usuario
     * @param rolUsuario Rol del usuario que hace la petición
     * @return HTTP 204 NO CONTENT
     */
    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void> activar(
            @PathVariable Long id,
            @RequestParam Rol rolUsuario) {
        usuarioService.activar(id, rolUsuario);
        return ResponseEntity.noContent().build();
    }

    /**
     * Eliminar físicamente un usuario
     *
     * DELETE /api/usuarios/{id}
     *
     * Solo SUPER_ADMIN puede realizar esta operación
     *
     * @param id ID del usuario
     * @param rolUsuario Rol del usuario que hace la petición
     * @return HTTP 204 NO CONTENT
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
            @RequestParam Rol rolUsuario) {
        usuarioService.eliminar(id, rolUsuario);
        return ResponseEntity.noContent().build();
    }

    /**
     * Cambiar contraseña de un usuario
     * Ruta: PATCH /api/usuarios/{id}/cambiar-contrasena
     */
    @PatchMapping("/{id}/cambiar-contrasena")
    public ResponseEntity<Void> cambiarContrasena(
            @PathVariable Long id,
            @Valid @RequestBody CambiarContrasenaRequestDTO request) {

        usuarioService.cambiarContrasena(id, request.getNuevaContrasena());
        return ResponseEntity.noContent().build();
    }
}