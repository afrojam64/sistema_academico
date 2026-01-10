package com.sistema.academico.aplicacion.servicio;

import com.sistema.academico.aplicacion.dto.response.DistribucionCalificacionesDTO;

/**
 * Interfaz del servicio para Distribución de Calificaciones
 */
public interface IDistribucionCalificacionesService {

    /**
     * Genera el reporte completo de distribución de calificaciones
     * Incluye: estadísticas generales, distribución por rangos, departamentos, materias y periodos
     *
     * @return DTO con toda la distribución de calificaciones del sistema
     */
    DistribucionCalificacionesDTO generarReporteDistribucion();
}