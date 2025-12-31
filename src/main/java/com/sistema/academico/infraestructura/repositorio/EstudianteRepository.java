package com.sistema.academico.infraestructura.repositorio;

import com.sistema.academico.dominio.entidad.Estudiante;
import com.sistema.academico.dominio.enumeracion.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
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
}