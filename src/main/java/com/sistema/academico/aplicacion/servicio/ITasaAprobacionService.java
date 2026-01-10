package com.sistema.academico.aplicacion.servicio;

import com.sistema.academico.aplicacion.dto.response.TasaAprobacionDTO;

/**
 * Interfaz del servicio para Tasa de Aprobación
 */
public interface ITasaAprobacionService {

    /**
     * Genera el reporte completo de tasas de aprobación
     * Incluye: resumen general, tasa por materia, curso, periodo y departamento
     *
     * @return DTO con todas las tasas de aprobación del sistema
     */
    TasaAprobacionDTO generarReporteTasaAprobacion();
}