package com.sistema.academico.infraestructura.repositorio;

import com.sistema.academico.dominio.entidad.Estudiante;
import com.sistema.academico.dominio.enumeracion.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    /**
     * Buscar estudiantes por estado
     */
    List<Estudiante> findByEstado(Estado estado);

    /**
     * Buscar estudiante por ID de usuario
     */
    Optional<Estudiante> findByUsuarioId(Long usuarioId);

    /**
     * Verificar si existe un estudiante asociado a un usuario
     */
    boolean existsByUsuarioId(Long usuarioId);

    /**
     * Verificar si existe un estudiante con un código específico
     */
    boolean existsByCodigoEstudiante(String codigoEstudiante);

    /**
     * Buscar estudiante por código
     */
    Optional<Estudiante> findByCodigoEstudiante(String codigoEstudiante);

    /**
     * Encuentra estudiantes activos sin inscripciones activas
     * (No tienen ninguna inscripción ACTIVA en el sistema)
     */
    /**
     * Encuentra estudiantes activos sin inscripciones activas
     * (No tienen ninguna inscripción ACTIVA en el sistema)
     * Usa SQL NATIVO para compatibilidad con PostgreSQL
     */
    @Query(value = "SELECT DISTINCT e.* FROM estudiantes e " +
            "LEFT JOIN inscripciones i ON i.estudiante_id = e.id AND i.estado = 'ACTIVO' " +
            "WHERE e.estado = 'ACTIVO' AND i.id IS NULL " +
            "ORDER BY e.fecha_ingreso DESC",
            nativeQuery = true)
    List<Estudiante> findEstudiantesActivosSinInscripcionesActivas();

    /**
     * Cuenta estudiantes activos sin inscripciones activas
     * Usa SQL NATIVO para compatibilidad con PostgreSQL
     */
    @Query(value = "SELECT COUNT(DISTINCT e.id) FROM estudiantes e " +
            "LEFT JOIN inscripciones i ON i.estudiante_id = e.id AND i.estado = 'ACTIVO' " +
            "WHERE e.estado = 'ACTIVO' AND i.id IS NULL",
            nativeQuery = true)
    Long countEstudiantesActivosSinInscripcionesActivas();

    // Contar estudiantes por estado
    Long countByEstado(Estado estado);

    /**
     * Buscar estudiante por nombre de usuario
     * Útil para obtener el estudiante del usuario autenticado
     */
    Optional<Estudiante> findByUsuario_NombreUsuario(String nombreUsuario);
}