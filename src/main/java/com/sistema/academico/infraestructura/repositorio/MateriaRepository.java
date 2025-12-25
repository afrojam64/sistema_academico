package com.sistema.academico.infraestructura.repositorio;

import com.sistema.academico.dominio.entidad.Materia;
import com.sistema.academico.dominio.entidad.Profesor;
import com.sistema.academico.dominio.enumeracion.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MateriaRepository extends JpaRepository<Materia, Long> {

    // Buscar por código
    Optional<Materia> findByCodigo(String codigo);

    // Verificar si existe un código
    boolean existsByCodigo(String codigo);

    // Buscar por profesor
    List<Materia> findByProfesor(Profesor profesor);

    // Buscar por estado
    List<Materia> findByEstado(Estado estado);

    // Buscar materias activas
    default List<Materia> findActivas() {
        return findByEstado(Estado.ACTIVO);
    }

    // Buscar materias activas de un profesor
    List<Materia> findByProfesorAndEstado(Profesor profesor, Estado estado);
}