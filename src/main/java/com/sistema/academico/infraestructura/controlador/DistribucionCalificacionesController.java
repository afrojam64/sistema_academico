package com.sistema.academico.infraestructura.controlador;

import com.sistema.academico.aplicacion.dto.response.DistribucionCalificacionesDTO;
import com.sistema.academico.aplicacion.servicio.IDistribucionCalificacionesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para Distribución de Calificaciones
 */
@RestController
@RequestMapping("/api/reportes/distribucion-calificaciones")
@RequiredArgsConstructor
public class DistribucionCalificacionesController {

    private final IDistribucionCalificacionesService distribucionService;

    /**
     * Endpoint para obtener el reporte completo de distribución de calificaciones
     *
     * GET /api/reportes/distribucion-calificaciones
     *
     * @return DistribucionCalificacionesDTO con estadísticas y distribución completa
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'PROFESOR')")
    public ResponseEntity<DistribucionCalificacionesDTO> obtenerDistribucionCalificaciones() {
        DistribucionCalificacionesDTO distribucion = distribucionService.generarReporteDistribucion();
        return ResponseEntity.ok(distribucion);
    }
}