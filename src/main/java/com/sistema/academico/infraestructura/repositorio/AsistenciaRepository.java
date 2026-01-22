package com.sistema.academico.infraestructura.repositorio;

import com.sistema.academico.dominio.entidad.Asistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio para gestionar las operaciones de base de datos de la entidad Asistencia.
 *
 * UBICACIÓN EN ARQUITECTURA:
 * - Capa: INFRAESTRUCTURA
 * - Paquete: infraestructura.repositorio
 *
 * @author Sistema Académico
 * @version 1.0
 */
@Repository
public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {

    /**
     * Busca las asistencias de una inscripción específica.
     *
     * @param inscripcionId ID de la inscripción
     * @return Lista de asistencias
     */
    List<Asistencia> findByInscripcionId(Long inscripcionId);

    /**
     * Busca las asistencias de una inscripción en un rango de fechas.
     *
     * @param inscripcionId ID de la inscripción
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Lista de asistencias en el rango
     */
    List<Asistencia> findByInscripcionIdAndFechaBetween(Long inscripcionId, LocalDate fechaInicio, LocalDate fechaFin);
}