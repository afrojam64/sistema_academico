package com.sistema.academico.infraestructura.controlador;

import com.sistema.academico.aplicacion.dto.request.CursoRequestDTO;
import com.sistema.academico.aplicacion.dto.response.CursoResponseDTO;
import com.sistema.academico.aplicacion.dto.response.CursosActivosReporteDTO;
import com.sistema.academico.aplicacion.dto.response.OcupacionCursosReporteDTO;
import com.sistema.academico.aplicacion.servicio.ICursoService;
import com.sistema.academico.dominio.entidad.Profesor;
import com.sistema.academico.dominio.enumeracion.Rol;
import com.sistema.academico.infraestructura.repositorio.ProfesorRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gestionar Cursos
 *
 * Endpoints disponibles:
 * - POST   /api/cursos                         → Crear curso
 * - GET    /api/cursos                         → Listar todos
 * - GET    /api/cursos/activos                 → Listar activos
 * - GET    /api/cursos/con-cupos               → Listar con cupos disponibles
 * - GET    /api/cursos/disponibles             → Listar cursos disponibles (con cupos) ✅ NUEVO
 * - GET    /api/cursos/profesor/{profesorId}   → Listar cursos de un profesor
 * - GET    /api/cursos/profesor/{profesorId}/activos → Listar cursos activos de un profesor
 * - GET    /api/cursos/{id}                    → Obtener por ID
 * - PUT    /api/cursos/{id}                    → Actualizar
 * - PATCH  /api/cursos/{id}/desactivar         → Desactivar
 * - PATCH  /api/cursos/{id}/activar            → Activar
 * - DELETE /api/cursos/{id}                    → Eliminar físicamente
 */
@RestController
@RequestMapping("/api/cursos")
@RequiredArgsConstructor
public class CursoController {

    private final ICursoService cursoService;
    private final ProfesorRepository profesorRepository;

    @PostMapping
    public ResponseEntity<CursoResponseDTO> crear(@Valid @RequestBody CursoRequestDTO request) {
        CursoResponseDTO curso = cursoService.crear(request);
        return new ResponseEntity<>(curso, HttpStatus.CREATED);
    }

    // ========================================
    // ENDPOINTS ESPECÍFICOS (ANTES DE /{id})
    // ========================================

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

    /**
     * ✅ NUEVO: Endpoint para estudiantes - cursos disponibles
     * Alias de /con-cupos para compatibilidad con frontend
     * Ruta: GET /api/cursos/disponibles
     */
    @GetMapping("/disponibles")
    public ResponseEntity<List<CursoResponseDTO>> listarCursosDisponibles() {
        List<CursoResponseDTO> cursos = cursoService.listarCursosConCupos();
        return ResponseEntity.ok(cursos);
    }

    // ========================================
    // ENDPOINTS PARA DASHBOARD PROFESOR
    // ========================================

    /**
     * Listar todos los cursos de un profesor
     * Ruta: GET /api/cursos/profesor/{profesorId}
     */
    @GetMapping("/profesor/{profesorId}")
    public ResponseEntity<List<CursoResponseDTO>> listarCursosPorProfesor(@PathVariable Long profesorId) {
        List<CursoResponseDTO> cursos = cursoService.listarCursosPorProfesor(profesorId);
        return ResponseEntity.ok(cursos);
    }

    /**
     * Listar cursos ACTIVOS de un profesor
     * Ruta: GET /api/cursos/profesor/{profesorId}/activos
     */
    @GetMapping("/profesor/{profesorId}/activos")
    public ResponseEntity<List<CursoResponseDTO>> listarCursosActivosProfesor(@PathVariable Long profesorId) {
        List<CursoResponseDTO> cursos = cursoService.listarCursosActivosPorProfesor(profesorId);
        return ResponseEntity.ok(cursos);
    }

    /**
     * Obtener cursos del profesor autenticado
     * Ruta: GET /api/cursos/mis-cursos
     */
    @GetMapping("/mis-cursos")
    public ResponseEntity<List<CursoResponseDTO>> obtenerMisCursos() {
        // Obtener usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String nombreUsuario = auth.getName();

        // Buscar profesor
        Profesor profesor = profesorRepository.findByUsuarioNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado"));

        // Obtener cursos activos del profesor
        List<CursoResponseDTO> cursos = cursoService.listarCursosActivosPorProfesor(profesor.getId());

        return ResponseEntity.ok(cursos);
    }

    /**
     * Buscar cursos por código, nombre de materia o periodo
     * Ruta: GET /api/cursos/buscar?termino=calculo
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<CursoResponseDTO>> buscarCursos(
            @RequestParam String termino) {

        List<CursoResponseDTO> cursos = cursoService.buscarPorTermino(termino);
        return ResponseEntity.ok(cursos);
    }

    /**
     * Generar reporte de cursos activos
     * Ruta: GET /api/cursos/reporte/activos
     */
    @GetMapping("/reporte/activos")
    public ResponseEntity<CursosActivosReporteDTO> generarReporteCursosActivos() {

        CursosActivosReporteDTO reporte = cursoService.generarReporteCursosActivos();

        return ResponseEntity.ok(reporte);
    }

    /**
     * Generar reporte de análisis de ocupación de cursos
     * Ruta: GET /api/cursos/reporte/ocupacion
     */
    @GetMapping("/reporte/ocupacion")
    public ResponseEntity<OcupacionCursosReporteDTO> generarReporteOcupacionCursos() {

        OcupacionCursosReporteDTO reporte = cursoService.generarReporteOcupacionCursos();

        return ResponseEntity.ok(reporte);
    }

    // ========================================
    // ENDPOINTS GENÉRICOS (AL FINAL)
    // ========================================

    /**
     * Obtener curso por ID
     * ⚠️ IMPORTANTE: Este endpoint DEBE ir AL FINAL
     * porque /{id} captura cualquier ruta
     */
    @GetMapping("/{id}")
    public ResponseEntity<CursoResponseDTO> obtenerPorId(@PathVariable Long id) {
        CursoResponseDTO curso = cursoService.obtenerPorId(id);
        return ResponseEntity.ok(curso);
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