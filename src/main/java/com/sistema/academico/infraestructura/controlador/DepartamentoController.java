package com.sistema.academico.infraestructura.controlador;

import com.sistema.academico.aplicacion.dto.request.DepartamentoRequestDTO;
import com.sistema.academico.aplicacion.dto.response.DepartamentoResponseDTO;
import com.sistema.academico.aplicacion.servicio.IDepartamentoService;
import com.sistema.academico.dominio.enumeracion.Rol;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gestionar Departamentos
 *
 * Endpoints disponibles:
 * - POST   /api/departamentos          → Crear departamento
 * - GET    /api/departamentos          → Listar todos
 * - GET    /api/departamentos/activos  → Listar activos
 * - GET    /api/departamentos/{id}     → Obtener por ID
 * - PUT    /api/departamentos/{id}     → Actualizar
 * - PATCH  /api/departamentos/{id}/desactivar → Desactivar
 * - PATCH  /api/departamentos/{id}/activar    → Activar
 * - DELETE /api/departamentos/{id}     → Eliminar físicamente
 */
@RestController
@RequestMapping("/api/departamentos")
@RequiredArgsConstructor
public class DepartamentoController {

    private final IDepartamentoService departamentoService;

    @PostMapping
    public ResponseEntity<DepartamentoResponseDTO> crear(@Valid @RequestBody DepartamentoRequestDTO request) {
        DepartamentoResponseDTO departamento = departamentoService.crear(request);
        return new ResponseEntity<>(departamento, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartamentoResponseDTO> obtenerPorId(@PathVariable Long id) {
        DepartamentoResponseDTO departamento = departamentoService.obtenerPorId(id);
        return ResponseEntity.ok(departamento);
    }

    @GetMapping
    public ResponseEntity<List<DepartamentoResponseDTO>> listarTodos() {
        List<DepartamentoResponseDTO> departamentos = departamentoService.listarTodos();
        return ResponseEntity.ok(departamentos);
    }

    @GetMapping("/activos")
    public ResponseEntity<List<DepartamentoResponseDTO>> listarActivos() {
        List<DepartamentoResponseDTO> departamentos = departamentoService.listarActivos();
        return ResponseEntity.ok(departamentos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartamentoResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody DepartamentoRequestDTO request) {
        DepartamentoResponseDTO departamento = departamentoService.actualizar(id, request);
        return ResponseEntity.ok(departamento);
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(
            @PathVariable Long id,
            @RequestParam Rol rolUsuario) {
        departamentoService.desactivar(id, rolUsuario);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void> activar(
            @PathVariable Long id,
            @RequestParam Rol rolUsuario) {
        departamentoService.activar(id, rolUsuario);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
            @RequestParam Rol rolUsuario) {
        departamentoService.eliminar(id, rolUsuario);
        return ResponseEntity.noContent().build();
    }
}