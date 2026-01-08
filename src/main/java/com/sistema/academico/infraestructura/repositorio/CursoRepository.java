package com.sistema.academico.infraestructura.repositorio;

import com.sistema.academico.dominio.entidad.Curso;
import com.sistema.academico.dominio.entidad.Materia;
import com.sistema.academico.dominio.entidad.Profesor;
import com.sistema.academico.dominio.enumeracion.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {

    // Buscar por código
    Optional<Curso> findByCodigo(String codigo);

    // Verificar si existe un código
    boolean existsByCodigo(String codigo);

    // Buscar por materia
    List<Curso> findByMateria(Materia materia);

    // Buscar por profesor
    List<Curso> findByProfesor(Profesor profesor);

    // Buscar por periodo
    List<Curso> findByPeriodo(String periodo);

    // Buscar por estado
    List<Curso> findByEstado(Estado estado);

    // Buscar cursos activos
    default List<Curso> findActivos() {
        return findByEstado(Estado.ACTIVO);
    }

    // Buscar cursos activos de un profesor
    List<Curso> findByProfesorAndEstado(Profesor profesor, Estado estado);

    // Buscar cursos con cupos disponibles
    @Query("SELECT c FROM Curso c WHERE c.cupoActual < c.cupoMaximo AND c.estado = 'ACTIVO'")
    List<Curso> findCursosConCuposDisponibles();

    /**
     * Obtiene la lista de periodos únicos disponibles en el sistema
     * Ordenados descendentemente (más reciente primero)
     */
    @Query("SELECT DISTINCT c.periodo FROM Curso c WHERE c.periodo IS NOT NULL ORDER BY c.periodo DESC")
    List<String> findDistinctPeriodos();
}