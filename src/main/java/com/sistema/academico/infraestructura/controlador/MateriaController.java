package com.sistema.academico.infraestructura.controlador;

import com.sistema.academico.aplicacion.dto.request.MateriaRequestDTO;
import com.sistema.academico.aplicacion.dto.response.MateriaResponseDTO;
import com.sistema.academico.aplicacion.servicio.IMateriaService;
import com.sistema.academico.dominio.enumeracion.Rol;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gestionar Materias
 *
 * Endpoints disponibles:
 * - POST   /api/materias          → Crear materia
 * - GET    /api/materias          → Listar todas
 * - GET    /api/materias/activas  → Listar activas
 * - GET    /api/materias/{id}     → Obtener por ID
 * - PUT    /api/materias/{id}     → Actualizar
 * - PATCH  /api/materias/{id}/desactivar → Desactivar
 * - PATCH  /api/materias/{id}/activar    → Activar
 * - DELETE /api/materias/{id}     → Eliminar físicamente
 */
@RestController
@RequestMapping("/api/materias")
@RequiredArgsConstructor
public class MateriaController {

    private final IMateriaService materiaService;

    @PostMapping
    public ResponseEntity<MateriaResponseDTO> crear(@Valid @RequestBody MateriaRequestDTO request) {
        MateriaResponseDTO materia = materiaService.crear(request);
        return new ResponseEntity<>(materia, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MateriaResponseDTO> obtenerPorId(@PathVariable Long id) {
        MateriaResponseDTO materia = materiaService.obtenerPorId(id);
        return ResponseEntity.ok(materia);
    }

    @GetMapping
    public ResponseEntity<List<MateriaResponseDTO>> listarTodas() {
        List<MateriaResponseDTO> materias = materiaService.listarTodas();
        return ResponseEntity.ok(materias);
    }

    @GetMapping("/activas")
    public ResponseEntity<List<MateriaResponseDTO>> listarActivas() {
        List<MateriaResponseDTO> materias = materiaService.listarActivas();
        return ResponseEntity.ok(materias);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MateriaResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody MateriaRequestDTO request) {
        MateriaResponseDTO materia = materiaService.actualizar(id, request);
        return ResponseEntity.ok(materia);
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(
            @PathVariable Long id,
            @RequestParam Rol rolUsuario) {
        materiaService.desactivar(id, rolUsuario);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void> activar(
            @PathVariable Long id,
            @RequestParam Rol rolUsuario) {
        materiaService.activar(id, rolUsuario);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
            @RequestParam Rol rolUsuario) {
        materiaService.eliminar(id, rolUsuario);
        return ResponseEntity.noContent().build();
    }
}