package com.sistema.academico.infraestructura.controlador;

import com.sistema.academico.aplicacion.dto.response.DashboardEjecutivoDTO;
import com.sistema.academico.aplicacion.servicio.IDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para Dashboard Ejecutivo
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Endpoints para Dashboard Ejecutivo")
@SecurityRequirement(name = "bearer-key")
public class DashboardController {

    private final IDashboardService dashboardService;

    /**
     * Endpoint para obtener el dashboard ejecutivo completo
     *
     * GET /api/dashboard/ejecutivo
     *
     * @return DashboardEjecutivoDTO con todas las métricas del sistema
     */
    @GetMapping("/ejecutivo")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(
            summary = "Obtener Dashboard Ejecutivo",
            description = "Genera un resumen ejecutivo del sistema académico con indicadores clave: " +
                    "resumen general, periodo actual, ocupación de cursos, top departamentos y alertas. " +
                    "Solo accesible para SUPER_ADMIN y ADMIN.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Dashboard generado exitosamente"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "No autorizado - Token inválido o expirado"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Prohibido - No tiene permisos para acceder a este recurso"
                    )
            }
    )
    public ResponseEntity<DashboardEjecutivoDTO> obtenerDashboardEjecutivo() {
        DashboardEjecutivoDTO dashboard = dashboardService.generarDashboardEjecutivo();
        return ResponseEntity.ok(dashboard);
    }
}