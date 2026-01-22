package com.sistema.academico.infraestructura.controlador;

import com.sistema.academico.aplicacion.dto.request.AsistenciaRequestDTO;
import com.sistema.academico.aplicacion.dto.response.AsistenciaResponseDTO;
import com.sistema.academico.aplicacion.servicio.IAsistenciaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/asistencias")
@RequiredArgsConstructor
public class AsistenciaController {

    private final IAsistenciaService asistenciaService;

    @PostMapping
    @PreAuthorize("hasAnyRole('PROFESOR', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<AsistenciaResponseDTO> registrarAsistencia(@Valid @RequestBody AsistenciaRequestDTO dto) {
        return new ResponseEntity<>(asistenciaService.registrarAsistencia(dto), HttpStatus.CREATED);
    }

    @PostMapping("/masiva/curso/{cursoId}")
    @PreAuthorize("hasAnyRole('PROFESOR', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<AsistenciaResponseDTO>> registrarAsistenciaMasiva(
            @PathVariable Long cursoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestBody List<AsistenciaRequestDTO> dtos) {
        return ResponseEntity.ok(asistenciaService.registrarAsistenciaMasiva(cursoId, fecha, dtos));
    }

    @GetMapping("/inscripcion/{inscripcionId}")
    @PreAuthorize("hasAnyRole('ESTUDIANTE', 'PROFESOR', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<AsistenciaResponseDTO>> obtenerHistorial(@PathVariable Long inscripcionId) {
        // TODO: Validar que si es ESTUDIANTE, solo pueda ver su propia inscripción
        return ResponseEntity.ok(asistenciaService.obtenerAsistenciaPorInscripcion(inscripcionId));
    }

    @GetMapping("/curso/{cursoId}")
    @PreAuthorize("hasAnyRole('PROFESOR', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<AsistenciaResponseDTO>> obtenerPorCursoYFecha(
            @PathVariable Long cursoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(asistenciaService.obtenerAsistenciaPorCursoYFecha(cursoId, fecha));
    }
}