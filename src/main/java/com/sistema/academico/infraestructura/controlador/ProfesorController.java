package com.sistema.academico.infraestructura.controlador;

import com.sistema.academico.aplicacion.dto.request.ProfesorRequestDTO;
import com.sistema.academico.aplicacion.dto.response.ProfesorResponseDTO;
import com.sistema.academico.aplicacion.servicio.IProfesorService;
import com.sistema.academico.dominio.enumeracion.Rol;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gestionar Profesores
 *
 * Endpoints disponibles:
 * - POST   /api/profesores          → Crear profesor
 * - GET    /api/profesores          → Listar todos
 * - GET    /api/profesores/activos  → Listar activos
 * - GET    /api/profesores/{id}     → Obtener por ID
 * - PUT    /api/profesores/{id}     → Actualizar
 * - PATCH  /api/profesores/{id}/desactivar → Desactivar
 * - PATCH  /api/profesores/{id}/activar    → Activar
 * - DELETE /api/profesores/{id}     → Eliminar físicamente
 */
@RestController
@RequestMapping("/api/profesores")
@RequiredArgsConstructor
public class ProfesorController {

    private final IProfesorService profesorService;

    @PostMapping
    public ResponseEntity<ProfesorResponseDTO> crear(@Valid @RequestBody ProfesorRequestDTO request) {
        ProfesorResponseDTO profesor = profesorService.crear(request);
        return new ResponseEntity<>(profesor, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfesorResponseDTO> obtenerPorId(@PathVariable Long id) {
        ProfesorResponseDTO profesor = profesorService.obtenerPorId(id);
        return ResponseEntity.ok(profesor);
    }

    @GetMapping
    public ResponseEntity<List<ProfesorResponseDTO>> listarTodos() {
        List<ProfesorResponseDTO> profesores = profesorService.listarTodos();
        return ResponseEntity.ok(profesores);
    }

    @GetMapping("/activos")
    public ResponseEntity<List<ProfesorResponseDTO>> listarActivos() {
        List<ProfesorResponseDTO> profesores = profesorService.listarActivos();
        return ResponseEntity.ok(profesores);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfesorResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProfesorRequestDTO request) {
        ProfesorResponseDTO profesor = profesorService.actualizar(id, request);
        return ResponseEntity.ok(profesor);
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(
            @PathVariable Long id,
            @RequestParam Rol rolUsuario) {
        profesorService.desactivar(id, rolUsuario);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void> activar(
            @PathVariable Long id,
            @RequestParam Rol rolUsuario) {
        profesorService.activar(id, rolUsuario);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
            @RequestParam Rol rolUsuario) {
        profesorService.eliminar(id, rolUsuario);
        return ResponseEntity.noContent().build();
    }
}