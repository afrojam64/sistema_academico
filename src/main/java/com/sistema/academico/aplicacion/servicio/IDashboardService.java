package com.sistema.academico.aplicacion.servicio;

import com.sistema.academico.aplicacion.dto.response.DashboardEjecutivoDTO;

/**
 * Interfaz del servicio para Dashboard Ejecutivo
 */
public interface IDashboardService {

    /**
     * Genera el dashboard ejecutivo con todas las métricas del sistema
     *
     * @return DTO con resumen general, ocupación, periodo actual, top departamentos y alertas
     */
    DashboardEjecutivoDTO generarDashboardEjecutivo();
}