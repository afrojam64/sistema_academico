package com.sistema.academico.infraestructura.repositorio;

import com.sistema.academico.dominio.entidad.Estudiante;
import com.sistema.academico.dominio.enumeracion.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    // Buscar por matrícula
    Optional<Estudiante> findByMatricula(String matricula);

    // Buscar por email
    Optional<Estudiante> findByEmail(String email);

    // Verificar si existe una matrícula
    boolean existsByMatricula(String matricula);

    // Verificar si existe un email
    boolean existsByEmail(String email);

    // Buscar por estado
    List<Estudiante> findByEstado(Estado estado);

    // Buscar estudiantes activos
    default List<Estudiante> findActivos() {
        return findByEstado(Estado.ACTIVO);
    }

    // Buscar por usuario
    Optional<Estudiante> findByUsuarioId(Long usuarioId);
}