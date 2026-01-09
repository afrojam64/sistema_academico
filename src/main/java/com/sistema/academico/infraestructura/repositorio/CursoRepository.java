package com.sistema.academico.infraestructura.repositorio;

import com.sistema.academico.dominio.entidad.Curso;
import com.sistema.academico.dominio.entidad.Materia;
import com.sistema.academico.dominio.entidad.Profesor;
import com.sistema.academico.dominio.enumeracion.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    /**
     * Queries adicionales para Dashboard Ejecutivo
     * Agregar estas queries al final del archivo CursoRepository.java
     */



    // Contar cursos completos (ocupación = 100%)
    @Query("SELECT COUNT(c) FROM Curso c " +
            "WHERE c.estado = 'ACTIVO' AND c.cupoActual = c.cupoMaximo")
    Long countCursosCompletos();

    // Contar cursos con alta ocupación (>80% y <100%)
    @Query("SELECT COUNT(c) FROM Curso c " +
            "WHERE c.estado = 'ACTIVO' " +
            "AND (c.cupoActual * 100.0 / c.cupoMaximo) > 80 " +
            "AND c.cupoActual < c.cupoMaximo")
    Long countCursosAltaOcupacion();

    // Contar cursos con media ocupación (50-80%)
    @Query("SELECT COUNT(c) FROM Curso c " +
            "WHERE c.estado = 'ACTIVO' " +
            "AND (c.cupoActual * 100.0 / c.cupoMaximo) >= 50 " +
            "AND (c.cupoActual * 100.0 / c.cupoMaximo) <= 80")
    Long countCursosMediaOcupacion();

    // Contar cursos con baja ocupación (<50%)
    @Query("SELECT COUNT(c) FROM Curso c " +
            "WHERE c.estado = 'ACTIVO' " +
            "AND (c.cupoActual * 100.0 / c.cupoMaximo) < 50")
    Long countCursosBajaOcupacion();

    // Calcular promedio de ocupación de todos los cursos activos
    @Query("SELECT AVG(c.cupoActual * 100.0 / c.cupoMaximo) FROM Curso c " +
            "WHERE c.estado = 'ACTIVO' AND c.cupoMaximo > 0")
    Double calcularPromedioOcupacion();

    // Calcular total de cupos ofrecidos
    @Query("SELECT SUM(c.cupoMaximo) FROM Curso c WHERE c.estado = 'ACTIVO'")
    Integer calcularTotalCuposOfrecidos();

    // Calcular total de cupos ocupados
    @Query("SELECT SUM(c.cupoActual) FROM Curso c WHERE c.estado = 'ACTIVO'")
    Integer calcularTotalCuposOcupados();

    // Obtener periodos académicos activos ordenados descendentemente
    @Query("SELECT c.periodo FROM Curso c " +
            "WHERE c.estado = 'ACTIVO' " +
            "GROUP BY c.periodo " +
            "ORDER BY c.periodo DESC")
    List<String> findPeriodosActivos();

    // Contar cursos activos por periodo
    @Query("SELECT COUNT(c) FROM Curso c " +
            "WHERE c.estado = 'ACTIVO' AND c.periodo = :periodo")
    Long countCursosPorPeriodo(@Param("periodo") String periodo);

    // Contar cursos por estado
    Long countByEstado(Estado estado);
}