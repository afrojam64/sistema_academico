package com.sistema.academico.infraestructura.controlador;

import com.sistema.academico.aplicacion.dto.request.CalificacionRequestDTO;
import com.sistema.academico.aplicacion.dto.response.CalificacionResponseDTO;
import com.sistema.academico.aplicacion.servicio.ICalificacionService;
import com.sistema.academico.dominio.enumeracion.Rol;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gestionar Calificaciones
 *
 * Endpoints disponibles:
 * - POST   /api/calificaciones                          → Registrar calificación
 * - GET    /api/calificaciones                          → Listar todas
 * - GET    /api/calificaciones/{id}                     → Obtener por ID
 * - GET    /api/calificaciones/inscripcion/{inscripcionId} → Listar por inscripción
 * - GET    /api/calificaciones/estudiante/{estudianteId} → Listar por estudiante
 * - GET    /api/calificaciones/curso/{cursoId}          → Listar por curso
 * - PUT    /api/calificaciones/{id}                     → Actualizar
 * - DELETE /api/calificaciones/{id}                     → Eliminar físicamente
 */
@RestController
@RequestMapping("/api/calificaciones")
@RequiredArgsConstructor
public class CalificacionController {

    private final ICalificacionService calificacionService;

    @PostMapping
    public ResponseEntity<CalificacionResponseDTO> registrar(@Valid @RequestBody CalificacionRequestDTO request) {
        CalificacionResponseDTO calificacion = calificacionService.registrar(request);
        return new ResponseEntity<>(calificacion, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CalificacionResponseDTO> obtenerPorId(@PathVariable Long id) {
        CalificacionResponseDTO calificacion = calificacionService.obtenerPorId(id);
        return ResponseEntity.ok(calificacion);
    }

    @GetMapping
    public ResponseEntity<List<CalificacionResponseDTO>> listarTodas() {
        List<CalificacionResponseDTO> calificaciones = calificacionService.listarTodas();
        return ResponseEntity.ok(calificaciones);
    }

    /**
     * Endpoint especial para obtener todas las calificaciones de una inscripción
     * Útil para ver el historial de notas de un estudiante en un curso
     */
    @GetMapping("/inscripcion/{inscripcionId}")
    public ResponseEntity<List<CalificacionResponseDTO>> listarPorInscripcion(@PathVariable Long inscripcionId) {
        List<CalificacionResponseDTO> calificaciones = calificacionService.listarPorInscripcion(inscripcionId);
        return ResponseEntity.ok(calificaciones);
    }

    // ========================================
    // ENDPOINTS PARA DASHBOARDS
    // ========================================

    /**
     * Listar todas las calificaciones de un estudiante (todas sus inscripciones)
     * Ruta: GET /api/calificaciones/estudiante/{estudianteId}
     * Útil para: Dashboard de estudiante (ver todas sus notas)
     */
    @GetMapping("/estudiante/{estudianteId}")
    public ResponseEntity<List<CalificacionResponseDTO>> listarPorEstudiante(@PathVariable Long estudianteId) {
        List<CalificacionResponseDTO> calificaciones = calificacionService.listarPorEstudiante(estudianteId);
        return ResponseEntity.ok(calificaciones);
    }

    /**
     * Listar todas las calificaciones de un curso específico
     * Ruta: GET /api/calificaciones/curso/{cursoId}
     * Útil para: Dashboard de profesor (ver notas de todos los estudiantes de un curso)
     */
    @GetMapping("/curso/{cursoId}")
    public ResponseEntity<List<CalificacionResponseDTO>> listarPorCurso(@PathVariable Long cursoId) {
        List<CalificacionResponseDTO> calificaciones = calificacionService.listarPorCurso(cursoId);
        return ResponseEntity.ok(calificaciones);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CalificacionResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CalificacionRequestDTO request) {
        CalificacionResponseDTO calificacion = calificacionService.actualizar(id, request);
        return ResponseEntity.ok(calificacion);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
            @RequestParam Rol rolUsuario) {
        calificacionService.eliminar(id, rolUsuario);
        return ResponseEntity.noContent().build();
    }
}