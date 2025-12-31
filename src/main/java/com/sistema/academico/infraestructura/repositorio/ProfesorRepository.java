package com.sistema.academico.infraestructura.repositorio;

import com.sistema.academico.dominio.entidad.Profesor;
import com.sistema.academico.dominio.enumeracion.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfesorRepository extends JpaRepository<Profesor, Long> {

    /**
     * Buscar profesores por estado
     */
    List<Profesor> findByEstado(Estado estado);

    /**
     * Buscar profesor por ID de usuario
     */
    Optional<Profesor> findByUsuarioId(Long usuarioId);

    /**
     * Verificar si existe un profesor asociado a un usuario
     */
    boolean existsByUsuarioId(Long usuarioId);

    /**
     * Buscar profesores por ID de departamento
     */
    List<Profesor> findByDepartamentoId(Long departamentoId);

    /**
     * Buscar profesores activos por ID de departamento
     */
    List<Profesor> findByDepartamentoIdAndEstado(Long departamentoId, Estado estado);

    /**
     * Buscar profesor por email del usuario asociado
     * Usa JPQL para acceder a la relación con Usuario
     */
    @Query("SELECT p FROM Profesor p WHERE p.usuario.email = :email")
    Optional<Profesor> findByUsuarioEmail(@Param("email") String email);

    /**
     * Verificar si existe un profesor con un email específico en su usuario
     * Usa JPQL para acceder a la relación con Usuario
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Profesor p WHERE p.usuario.email = :email")
    boolean existsByUsuarioEmail(@Param("email") String email);
}