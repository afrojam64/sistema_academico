package com.sistema.academico.infraestructura.repositorio;

import com.sistema.academico.dominio.entidad.Horario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para gestionar las operaciones de base de datos de la entidad Horario.
 *
 * UBICACIÓN EN ARQUITECTURA:
 * - Capa: INFRAESTRUCTURA
 * - Paquete: infraestructura.repositorio
 *
 * @author Sistema Académico
 * @version 1.0
 */
@Repository
public interface HorarioRepository extends JpaRepository<Horario, Long> {

    /**
     * Busca todos los horarios asociados a un curso específico.
     *
     * @param cursoId ID del curso
     * @return Lista de horarios del curso
     */
    List<Horario> findByCursoId(Long cursoId);
}