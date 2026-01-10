package com.sistema.academico.infraestructura.controlador;

import com.sistema.academico.aplicacion.dto.response.TasaAprobacionDTO;
import com.sistema.academico.aplicacion.servicio.ITasaAprobacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para Tasa de Aprobación
 */
@RestController
@RequestMapping("/api/reportes/tasa-aprobacion")
@RequiredArgsConstructor
public class TasaAprobacionController {

    private final ITasaAprobacionService tasaAprobacionService;

    /**
     * Endpoint para obtener el reporte completo de tasas de aprobación
     *
     * GET /api/reportes/tasa-aprobacion
     *
     * @return TasaAprobacionDTO con todas las tasas de aprobación del sistema
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'PROFESOR')")
    public ResponseEntity<TasaAprobacionDTO> obtenerTasaAprobacion() {
        TasaAprobacionDTO tasa = tasaAprobacionService.generarReporteTasaAprobacion();
        return ResponseEntity.ok(tasa);
    }
}