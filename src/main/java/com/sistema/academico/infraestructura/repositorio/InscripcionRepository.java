package com.sistema.academico.infraestructura.repositorio;

import com.sistema.academico.dominio.entidad.Curso;
import com.sistema.academico.dominio.entidad.Estudiante;
import com.sistema.academico.dominio.entidad.Inscripcion;
import com.sistema.academico.dominio.enumeracion.EstadoInscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {

    // Buscar inscripciones de un estudiante
    List<Inscripcion> findByEstudiante(Estudiante estudiante);

    // Buscar inscripciones de un curso
    List<Inscripcion> findByCurso(Curso curso);

    // Buscar inscripciones por estado
    List<Inscripcion> findByEstado(EstadoInscripcion estado);

    // Buscar inscripciones activas de un estudiante
    List<Inscripcion> findByEstudianteAndEstado(Estudiante estudiante, EstadoInscripcion estado);

    // Buscar inscripciones activas de un curso
    List<Inscripcion> findByCursoAndEstado(Curso curso, EstadoInscripcion estado);

    // Verificar si un estudiante ya está inscrito en un curso
    boolean existsByEstudianteAndCurso(Estudiante estudiante, Curso curso);

    // Buscar inscripción específica
    Optional<Inscripcion> findByEstudianteAndCurso(Estudiante estudiante, Curso curso);

    // Contar inscripciones activas de un curso
    @Query("SELECT COUNT(i) FROM Inscripcion i WHERE i.curso = :curso AND i.estado = 'ACTIVO'")
    Long countInscripcionesActivasByCurso(@Param("curso") Curso curso);
}