package com.sistema.academico.infraestructura.controlador;

import com.sistema.academico.aplicacion.dto.request.CursoRequestDTO;
import com.sistema.academico.aplicacion.dto.response.CursoResponseDTO;
import com.sistema.academico.aplicacion.servicio.ICursoService;
import com.sistema.academico.dominio.enumeracion.Rol;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gestionar Cursos
 *
 * Endpoints disponibles:
 * - POST   /api/cursos                → Crear curso
 * - GET    /api/cursos                → Listar todos
 * - GET    /api/cursos/activos        → Listar activos
 * - GET    /api/cursos/con-cupos      → Listar con cupos disponibles
 * - GET    /api/cursos/{id}           → Obtener por ID
 * - PUT    /api/cursos/{id}           → Actualizar
 * - PATCH  /api/cursos/{id}/desactivar → Desactivar
 * - PATCH  /api/cursos/{id}/activar    → Activar
 * - DELETE /api/cursos/{id}           → Eliminar físicamente
 */
@RestController
@RequestMapping("/api/cursos")
@RequiredArgsConstructor
public class CursoController {

    private final ICursoService cursoService;

    @PostMapping
    public ResponseEntity<CursoResponseDTO> crear(@Valid @RequestBody CursoRequestDTO request) {
        CursoResponseDTO curso = cursoService.crear(request);
        return new ResponseEntity<>(curso, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CursoResponseDTO> obtenerPorId(@PathVariable Long id) {
        CursoResponseDTO curso = cursoService.obtenerPorId(id);
        return ResponseEntity.ok(curso);
    }

    @GetMapping
    public ResponseEntity<List<CursoResponseDTO>> listarTodos() {
        List<CursoResponseDTO> cursos = cursoService.listarTodos();
        return ResponseEntity.ok(cursos);
    }

    @GetMapping("/activos")
    public ResponseEntity<List<CursoResponseDTO>> listarActivos() {
        List<CursoResponseDTO> cursos = cursoService.listarActivos();
        return ResponseEntity.ok(cursos);
    }

    /**
     * Endpoint especial para listar cursos con cupos disponibles
     * Útil para el proceso de inscripción
     */
    @GetMapping("/con-cupos")
    public ResponseEntity<List<CursoResponseDTO>> listarCursosConCupos() {
        List<CursoResponseDTO> cursos = cursoService.listarCursosConCupos();
        return ResponseEntity.ok(cursos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CursoResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CursoRequestDTO request) {
        CursoResponseDTO curso = cursoService.actualizar(id, request);
        return ResponseEntity.ok(curso);
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(
            @PathVariable Long id,
            @RequestParam Rol rolUsuario) {
        cursoService.desactivar(id, rolUsuario);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void> activar(
            @PathVariable Long id,
            @RequestParam Rol rolUsuario) {
        cursoService.activar(id, rolUsuario);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
            @RequestParam Rol rolUsuario) {
        cursoService.eliminar(id, rolUsuario);
        return ResponseEntity.noContent().build();
    }
}