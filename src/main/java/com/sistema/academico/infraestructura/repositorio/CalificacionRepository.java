package com.sistema.academico.infraestructura.repositorio;

import com.sistema.academico.dominio.entidad.Calificacion;
import com.sistema.academico.dominio.entidad.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CalificacionRepository extends JpaRepository<Calificacion, Long> {

    // Buscar calificaciones de una inscripción
    List<Calificacion> findByInscripcion(Inscripcion inscripcion);

    // Buscar calificaciones de una inscripción ordenadas por fecha
    List<Calificacion> findByInscripcionOrderByFechaCalificacionAsc(Inscripcion inscripcion);

    // Calcular promedio ponderado de una inscripción
    @Query("SELECT SUM(c.nota * c.porcentaje) / 100.0 FROM Calificacion c WHERE c.inscripcion = :inscripcion")
    BigDecimal calcularPromedioPonderado(@Param("inscripcion") Inscripcion inscripcion);

    // Verificar si una inscripción tiene todas las calificaciones
    @Query("SELECT CASE WHEN SUM(c.porcentaje) = 100 THEN true ELSE false END FROM Calificacion c WHERE c.inscripcion = :inscripcion")
    Boolean tieneCalificacionCompleta(@Param("inscripcion") Inscripcion inscripcion);

    /**
     * Queries adicionales para Distribución de Calificaciones
     * Agregar estas queries al final del archivo CalificacionRepository.java
     * (ANTES de cerrar la interfaz con "}")
     */

// =========================================================================
// ESTADÍSTICAS GENERALES
// =========================================================================

// Contar total de calificaciones
    @Query("SELECT COUNT(c) FROM Calificacion c")
    Long contarTotalCalificaciones();

    // Calcular promedio general del sistema
    @Query("SELECT AVG(c.nota) FROM Calificacion c")
    Double calcularPromedioGeneralSistema();

    // Obtener nota más alta
    @Query("SELECT MAX(c.nota) FROM Calificacion c")
    Double obtenerNotaMasAlta();

    // Obtener nota más baja
    @Query("SELECT MIN(c.nota) FROM Calificacion c")
    Double obtenerNotaMasBaja();

// =========================================================================
// DISTRIBUCIÓN POR RANGOS
// =========================================================================

    // Contar calificaciones en rango 0.0-2.9 (Reprobado)
    @Query("SELECT COUNT(c) FROM Calificacion c WHERE c.nota >= 0.0 AND c.nota < 3.0")
    Long contarCalificacionesRango0a2();

    // Contar calificaciones en rango 3.0-3.9 (Aceptable)
    @Query("SELECT COUNT(c) FROM Calificacion c WHERE c.nota >= 3.0 AND c.nota < 4.0")
    Long contarCalificacionesRango3a3();

    // Contar calificaciones en rango 4.0-5.0 (Sobresaliente)
    @Query("SELECT COUNT(c) FROM Calificacion c WHERE c.nota >= 4.0 AND c.nota <= 5.0")
    Long contarCalificacionesRango4a5();

// =========================================================================
// DISTRIBUCIÓN POR DEPARTAMENTO
// =========================================================================

    // Estadísticas por departamento
    @Query("SELECT d.nombre, " +
            "COUNT(c), " +
            "AVG(c.nota), " +
            "SUM(CASE WHEN c.nota >= 0.0 AND c.nota < 3.0 THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN c.nota >= 3.0 AND c.nota < 4.0 THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN c.nota >= 4.0 AND c.nota <= 5.0 THEN 1 ELSE 0 END) " +
            "FROM Calificacion c " +
            "JOIN c.inscripcion i " +
            "JOIN i.curso cu " +
            "JOIN cu.materia m " +
            "JOIN m.departamento d " +
            "GROUP BY d.nombre " +
            "ORDER BY COUNT(c) DESC")
    List<Object[]> obtenerDistribucionPorDepartamento();

// =========================================================================
// DISTRIBUCIÓN POR MATERIA
// =========================================================================

    // Estadísticas por materia (Top 10)
    @Query("SELECT m.nombre, " +
            "d.nombre, " +
            "COUNT(c), " +
            "AVG(c.nota), " +
            "SUM(CASE WHEN c.nota >= 0.0 AND c.nota < 3.0 THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN c.nota >= 3.0 AND c.nota < 4.0 THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN c.nota >= 4.0 AND c.nota <= 5.0 THEN 1 ELSE 0 END) " +
            "FROM Calificacion c " +
            "JOIN c.inscripcion i " +
            "JOIN i.curso cu " +
            "JOIN cu.materia m " +
            "JOIN m.departamento d " +
            "GROUP BY m.nombre, d.nombre " +
            "ORDER BY COUNT(c) DESC")
    List<Object[]> obtenerDistribucionPorMateria();

// =========================================================================
// DISTRIBUCIÓN POR PERIODO
// =========================================================================

    // Estadísticas por periodo académico
    @Query("SELECT cu.periodo, " +
            "COUNT(c), " +
            "AVG(c.nota), " +
            "SUM(CASE WHEN c.nota >= 0.0 AND c.nota < 3.0 THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN c.nota >= 3.0 AND c.nota < 4.0 THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN c.nota >= 4.0 AND c.nota <= 5.0 THEN 1 ELSE 0 END) " +
            "FROM Calificacion c " +
            "JOIN c.inscripcion i " +
            "JOIN i.curso cu " +
            "GROUP BY cu.periodo " +
            "ORDER BY cu.periodo DESC")
    List<Object[]> obtenerDistribucionPorPeriodo();

// =========================================================================
// QUERIES AUXILIARES
// =========================================================================

    // Contar calificaciones aprobadas (>= 3.0)
    @Query("SELECT COUNT(c) FROM Calificacion c WHERE c.nota >= 3.0")
    Long contarCalificacionesAprobadas();

    // Contar calificaciones reprobadas (< 3.0)
    @Query("SELECT COUNT(c) FROM Calificacion c WHERE c.nota < 3.0")
    Long contarCalificacionesReprobadas();

    /**
        * Queries adicionales para Tasa de Aprobación
     * VERSIÓN CORREGIDA - Usa "nombre" y "apellido" (singular) en lugar de plural
     */

// =========================================================================
// TASA DE APROBACIÓN - RESUMEN GENERAL
// =========================================================================

    // Contar estudiantes únicos con calificaciones
    @Query("SELECT COUNT(DISTINCT i.estudiante.id) FROM Calificacion c " +
            "JOIN c.inscripcion i " +
            "WHERE i.estado = 'ACTIVO'")
    Long contarEstudiantesEvaluados();

    // Contar estudiantes con promedio >= 3.0 (aprobados)
    @Query(value = "SELECT COUNT(DISTINCT estudiante_id) FROM (" +
            "    SELECT i.estudiante_id, AVG(c.nota) as promedio " +
            "    FROM calificaciones c " +
            "    INNER JOIN inscripciones i ON c.inscripcion_id = i.id " +
            "    WHERE i.estado = 'ACTIVO' " +
            "    GROUP BY i.estudiante_id " +
            "    HAVING AVG(c.nota) >= 3.0" +
            ") AS aprobados", nativeQuery = true)
    Long contarEstudiantesAprobados();

    // Contar estudiantes con promedio < 3.0 (reprobados)
    @Query(value = "SELECT COUNT(DISTINCT estudiante_id) FROM (" +
            "    SELECT i.estudiante_id, AVG(c.nota) as promedio " +
            "    FROM calificaciones c " +
            "    INNER JOIN inscripciones i ON c.inscripcion_id = i.id " +
            "    WHERE i.estado = 'ACTIVO' " +
            "    GROUP BY i.estudiante_id " +
            "    HAVING AVG(c.nota) < 3.0" +
            ") AS reprobados", nativeQuery = true)
    Long contarEstudiantesReprobados();

// =========================================================================
// TASA DE APROBACIÓN POR MATERIA
// =========================================================================

    @Query(value = "SELECT " +
            "    m.nombre, " +
            "    m.codigo, " +
            "    d.nombre, " +
            "    COUNT(DISTINCT i.estudiante_id) as total, " +
            "    SUM(CASE WHEN prom.promedio >= 3.0 THEN 1 ELSE 0 END) as aprobados, " +
            "    SUM(CASE WHEN prom.promedio < 3.0 THEN 1 ELSE 0 END) as reprobados, " +
            "    AVG(prom.promedio) as promedio_general " +
            "FROM materias m " +
            "INNER JOIN departamentos d ON m.departamento_id = d.id " +
            "INNER JOIN cursos cu ON cu.materia_id = m.id " +
            "INNER JOIN inscripciones i ON i.curso_id = cu.id " +
            "INNER JOIN (" +
            "    SELECT c.inscripcion_id, AVG(c.nota) as promedio " +
            "    FROM calificaciones c " +
            "    GROUP BY c.inscripcion_id" +
            ") prom ON prom.inscripcion_id = i.id " +
            "WHERE i.estado = 'ACTIVO' AND m.estado = 'ACTIVO' " +
            "GROUP BY m.id, m.nombre, m.codigo, d.nombre " +
            "ORDER BY (SUM(CASE WHEN prom.promedio >= 3.0 THEN 1 ELSE 0 END) * 100.0 / COUNT(DISTINCT i.estudiante_id)) DESC",
            nativeQuery = true)
    List<Object[]> obtenerTasaAprobacionPorMateria();

// =========================================================================
// TASA DE APROBACIÓN POR CURSO (CORREGIDO - nombre y apellido singular)
// =========================================================================

    @Query(value = "SELECT " +
            "    cu.codigo, " +
            "    m.nombre, " +
            "    CONCAT(u.nombre, ' ', u.apellido), " +
            "    cu.periodo, " +
            "    COUNT(DISTINCT i.estudiante_id) as total, " +
            "    SUM(CASE WHEN prom.promedio >= 3.0 THEN 1 ELSE 0 END) as aprobados, " +
            "    SUM(CASE WHEN prom.promedio < 3.0 THEN 1 ELSE 0 END) as reprobados, " +
            "    AVG(prom.promedio) as promedio_general " +
            "FROM cursos cu " +
            "INNER JOIN materias m ON cu.materia_id = m.id " +
            "INNER JOIN profesores p ON cu.profesor_id = p.id " +
            "INNER JOIN usuarios u ON p.usuario_id = u.id " +
            "INNER JOIN inscripciones i ON i.curso_id = cu.id " +
            "INNER JOIN (" +
            "    SELECT c.inscripcion_id, AVG(c.nota) as promedio " +
            "    FROM calificaciones c " +
            "    GROUP BY c.inscripcion_id" +
            ") prom ON prom.inscripcion_id = i.id " +
            "WHERE i.estado = 'ACTIVO' AND cu.estado = 'ACTIVO' " +
            "GROUP BY cu.id, cu.codigo, m.nombre, u.nombre, u.apellido, cu.periodo " +
            "ORDER BY cu.periodo DESC, " +
            "(SUM(CASE WHEN prom.promedio >= 3.0 THEN 1 ELSE 0 END) * 100.0 / COUNT(DISTINCT i.estudiante_id)) DESC",
            nativeQuery = true)
    List<Object[]> obtenerTasaAprobacionPorCurso();

// =========================================================================
// TASA DE APROBACIÓN POR PERIODO
// =========================================================================

    @Query(value = "SELECT " +
            "    cu.periodo, " +
            "    COUNT(DISTINCT i.estudiante_id) as total, " +
            "    SUM(CASE WHEN prom.promedio >= 3.0 THEN 1 ELSE 0 END) as aprobados, " +
            "    SUM(CASE WHEN prom.promedio < 3.0 THEN 1 ELSE 0 END) as reprobados, " +
            "    AVG(prom.promedio) as promedio_general " +
            "FROM cursos cu " +
            "INNER JOIN inscripciones i ON i.curso_id = cu.id " +
            "INNER JOIN (" +
            "    SELECT c.inscripcion_id, AVG(c.nota) as promedio " +
            "    FROM calificaciones c " +
            "    GROUP BY c.inscripcion_id" +
            ") prom ON prom.inscripcion_id = i.id " +
            "WHERE i.estado = 'ACTIVO' AND cu.estado = 'ACTIVO' " +
            "GROUP BY cu.periodo " +
            "ORDER BY cu.periodo DESC",
            nativeQuery = true)
    List<Object[]> obtenerTasaAprobacionPorPeriodo();

// =========================================================================
// TASA DE APROBACIÓN POR DEPARTAMENTO
// =========================================================================

    @Query(value = "SELECT " +
            "    d.nombre, " +
            "    COUNT(DISTINCT i.estudiante_id) as total, " +
            "    SUM(CASE WHEN prom.promedio >= 3.0 THEN 1 ELSE 0 END) as aprobados, " +
            "    SUM(CASE WHEN prom.promedio < 3.0 THEN 1 ELSE 0 END) as reprobados, " +
            "    AVG(prom.promedio) as promedio_general, " +
            "    COUNT(DISTINCT m.id) as total_materias " +
            "FROM departamentos d " +
            "INNER JOIN materias m ON m.departamento_id = d.id " +
            "INNER JOIN cursos cu ON cu.materia_id = m.id " +
            "INNER JOIN inscripciones i ON i.curso_id = cu.id " +
            "INNER JOIN (" +
            "    SELECT c.inscripcion_id, AVG(c.nota) as promedio " +
            "    FROM calificaciones c " +
            "    GROUP BY c.inscripcion_id" +
            ") prom ON prom.inscripcion_id = i.id " +
            "WHERE i.estado = 'ACTIVO' AND cu.estado = 'ACTIVO' AND m.estado = 'ACTIVO' " +
            "GROUP BY d.id, d.nombre " +
            "ORDER BY (SUM(CASE WHEN prom.promedio >= 3.0 THEN 1 ELSE 0 END) * 100.0 / COUNT(DISTINCT i.estudiante_id)) DESC",
            nativeQuery = true)
    List<Object[]> obtenerTasaAprobacionPorDepartamento();
}