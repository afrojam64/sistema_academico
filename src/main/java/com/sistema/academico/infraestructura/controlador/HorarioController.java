package com.sistema.academico.infraestructura.controlador;

import com.sistema.academico.aplicacion.dto.request.HorarioRequestDTO;
import com.sistema.academico.aplicacion.dto.response.HorarioResponseDTO;
import com.sistema.academico.aplicacion.servicio.IHorarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/horarios")
@RequiredArgsConstructor
public class HorarioController {

    private final IHorarioService horarioService;

    @PostMapping("/curso/{cursoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<HorarioResponseDTO> agregarHorario(
            @PathVariable Long cursoId,
            @Valid @RequestBody HorarioRequestDTO dto) {
        return new ResponseEntity<>(horarioService.agregarHorario(cursoId, dto), HttpStatus.CREATED);
    }

    @GetMapping("/curso/{cursoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'PROFESOR', 'ESTUDIANTE')")
    public ResponseEntity<List<HorarioResponseDTO>> obtenerHorariosPorCurso(@PathVariable Long cursoId) {
        return ResponseEntity.ok(horarioService.obtenerHorariosPorCurso(cursoId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Void> eliminarHorario(@PathVariable Long id) {
        horarioService.eliminarHorario(id);
        return ResponseEntity.noContent().build();
    }
}