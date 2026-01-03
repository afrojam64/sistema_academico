package com.sistema.academico.infraestructura.controlador;

import com.sistema.academico.aplicacion.dto.request.InscripcionRequestDTO;
import com.sistema.academico.aplicacion.dto.response.InscripcionResponseDTO;
import com.sistema.academico.aplicacion.servicio.IInscripcionService;
import com.sistema.academico.dominio.enumeracion.Rol;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gestionar Inscripciones
 *
 * Endpoints disponibles:
 * - POST   /api/inscripciones                          → Crear inscripción
 * - GET    /api/inscripciones                          → Listar todas
 * - GET    /api/inscripciones/activas                  → Listar activas
 * - GET    /api/inscripciones/{id}                     → Obtener por ID
 * - GET    /api/inscripciones/estudiante/{estudianteId} → Listar por estudiante
 * - GET    /api/inscripciones/estudiante/{estudianteId}/activas → Listar activas de estudiante
 * - GET    /api/inscripciones/curso/{cursoId}          → Listar por curso
 * - PATCH  /api/inscripciones/{id}/retirar             → Retirar inscripción
 * - PATCH  /api/inscripciones/{id}/completar           → Completar inscripción
 * - DELETE /api/inscripciones/{id}                     → Eliminar físicamente
 */
@RestController
@RequestMapping("/api/inscripciones")
@RequiredArgsConstructor
public class InscripcionController {

    private final IInscripcionService inscripcionService;

    @PostMapping
    public ResponseEntity<InscripcionResponseDTO> crear(@Valid @RequestBody InscripcionRequestDTO request) {
        InscripcionResponseDTO inscripcion = inscripcionService.crear(request);
        return new ResponseEntity<>(inscripcion, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InscripcionResponseDTO> obtenerPorId(@PathVariable Long id) {
        InscripcionResponseDTO inscripcion = inscripcionService.obtenerPorId(id);
        return ResponseEntity.ok(inscripcion);
    }

    @GetMapping
    public ResponseEntity<List<InscripcionResponseDTO>> listarTodas() {
        List<InscripcionResponseDTO> inscripciones = inscripcionService.listarTodas();
        return ResponseEntity.ok(inscripciones);
    }

    @GetMapping("/activas")
    public ResponseEntity<List<InscripcionResponseDTO>> listarActivas() {
        List<InscripcionResponseDTO> inscripciones = inscripcionService.listarActivas();
        return ResponseEntity.ok(inscripciones);
    }

    // ========================================
    // ENDPOINTS PARA DASHBOARDS
    // ========================================

    /**
     * Listar todas las inscripciones de un estudiante
     * Ruta: GET /api/inscripciones/estudiante/{estudianteId}
     * Útil para: Dashboard de estudiante
     */
    @GetMapping("/estudiante/{estudianteId}")
    public ResponseEntity<List<InscripcionResponseDTO>> listarPorEstudiante(@PathVariable Long estudianteId) {
        List<InscripcionResponseDTO> inscripciones = inscripcionService.listarPorEstudiante(estudianteId);
        return ResponseEntity.ok(inscripciones);
    }

    /**
     * Listar inscripciones ACTIVAS de un estudiante
     * Ruta: GET /api/inscripciones/estudiante/{estudianteId}/activas
     * Útil para: Dashboard de estudiante (cursos en progreso)
     */
    @GetMapping("/estudiante/{estudianteId}/activas")
    public ResponseEntity<List<InscripcionResponseDTO>> listarActivasPorEstudiante(@PathVariable Long estudianteId) {
        List<InscripcionResponseDTO> inscripciones = inscripcionService.listarActivasPorEstudiante(estudianteId);
        return ResponseEntity.ok(inscripciones);
    }

    /**
     * Listar todas las inscripciones de un curso
     * Ruta: GET /api/inscripciones/curso/{cursoId}
     * Útil para: Dashboard de profesor (ver estudiantes de un curso)
     */
    @GetMapping("/curso/{cursoId}")
    public ResponseEntity<List<InscripcionResponseDTO>> listarPorCurso(@PathVariable Long cursoId) {
        List<InscripcionResponseDTO> inscripciones = inscripcionService.listarPorCurso(cursoId);
        return ResponseEntity.ok(inscripciones);
    }

    /**
     * Retirar una inscripción (cambiar estado a RETIRADO)
     * El estudiante puede hacerlo
     */
    @PatchMapping("/{id}/retirar")
    public ResponseEntity<Void> retirar(@PathVariable Long id) {
        inscripcionService.retirar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Completar una inscripción (cambiar estado a COMPLETADO)
     * Solo puede hacerlo un ADMIN o superior
     */
    @PatchMapping("/{id}/completar")
    public ResponseEntity<Void> completar(
            @PathVariable Long id,
            @RequestParam Rol rolUsuario) {
        inscripcionService.completar(id, rolUsuario);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
            @RequestParam Rol rolUsuario) {
        inscripcionService.eliminar(id, rolUsuario);
        return ResponseEntity.noContent().build();
    }
}