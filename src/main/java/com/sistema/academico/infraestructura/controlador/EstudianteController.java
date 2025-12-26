package com.sistema.academico.infraestructura.controlador;

import com.sistema.academico.aplicacion.dto.request.EstudianteRequestDTO;
import com.sistema.academico.aplicacion.dto.response.EstudianteResponseDTO;
import com.sistema.academico.aplicacion.servicio.IEstudianteService;
import com.sistema.academico.dominio.enumeracion.Rol;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gestionar Estudiantes
 *
 * Endpoints disponibles:
 * - POST   /api/estudiantes          → Crear estudiante
 * - GET    /api/estudiantes          → Listar todos
 * - GET    /api/estudiantes/activos  → Listar activos
 * - GET    /api/estudiantes/{id}     → Obtener por ID
 * - PUT    /api/estudiantes/{id}     → Actualizar
 * - PATCH  /api/estudiantes/{id}/desactivar → Desactivar
 * - PATCH  /api/estudiantes/{id}/activar    → Activar
 * - DELETE /api/estudiantes/{id}     → Eliminar físicamente
 */
@RestController
@RequestMapping("/api/estudiantes")
@RequiredArgsConstructor
public class EstudianteController {

    private final IEstudianteService estudianteService;

    @PostMapping
    public ResponseEntity<EstudianteResponseDTO> crear(@Valid @RequestBody EstudianteRequestDTO request) {
        EstudianteResponseDTO estudiante = estudianteService.crear(request);
        return new ResponseEntity<>(estudiante, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstudianteResponseDTO> obtenerPorId(@PathVariable Long id) {
        EstudianteResponseDTO estudiante = estudianteService.obtenerPorId(id);
        return ResponseEntity.ok(estudiante);
    }

    @GetMapping
    public ResponseEntity<List<EstudianteResponseDTO>> listarTodos() {
        List<EstudianteResponseDTO> estudiantes = estudianteService.listarTodos();
        return ResponseEntity.ok(estudiantes);
    }

    @GetMapping("/activos")
    public ResponseEntity<List<EstudianteResponseDTO>> listarActivos() {
        List<EstudianteResponseDTO> estudiantes = estudianteService.listarActivos();
        return ResponseEntity.ok(estudiantes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstudianteResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody EstudianteRequestDTO request) {
        EstudianteResponseDTO estudiante = estudianteService.actualizar(id, request);
        return ResponseEntity.ok(estudiante);
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(
            @PathVariable Long id,
            @RequestParam Rol rolUsuario) {
        estudianteService.desactivar(id, rolUsuario);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void> activar(
            @PathVariable Long id,
            @RequestParam Rol rolUsuario) {
        estudianteService.activar(id, rolUsuario);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
            @RequestParam Rol rolUsuario) {
        estudianteService.eliminar(id, rolUsuario);
        return ResponseEntity.noContent().build();
    }
}