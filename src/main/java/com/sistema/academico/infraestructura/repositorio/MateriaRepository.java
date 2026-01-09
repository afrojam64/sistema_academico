package com.sistema.academico.infraestructura.repositorio;

import com.sistema.academico.dominio.entidad.Departamento;
import com.sistema.academico.dominio.entidad.Materia;
import com.sistema.academico.dominio.enumeracion.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MateriaRepository extends JpaRepository<Materia, Long> {

    /**
     * Buscar materias por estado
     */
    List<Materia> findByEstado(Estado estado);

    /**
     * Buscar materia por código
     */
    Optional<Materia> findByCodigo(String codigo);

    /**
     * Verificar si existe una materia con un código específico
     */
    boolean existsByCodigo(String codigo);

    /**
     * Buscar materias por departamento
     */
    List<Materia> findByDepartamento(Departamento departamento);

    /**
     * Buscar materias por ID de departamento
     */
    List<Materia> findByDepartamentoId(Long departamentoId);

    /**
     * Buscar materias activas de un departamento
     */
    List<Materia> findByDepartamentoAndEstado(Departamento departamento, Estado estado);

    /**
     * Buscar materias activas por ID de departamento
     */
    List<Materia> findByDepartamentoIdAndEstado(Long departamentoId, Estado estado);

    // Contar materias por estado
    Long countByEstado(Estado estado);
}