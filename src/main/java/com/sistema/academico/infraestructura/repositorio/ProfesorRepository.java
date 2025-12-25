package com.sistema.academico.infraestructura.repositorio;

import com.sistema.academico.dominio.entidad.Departamento;
import com.sistema.academico.dominio.entidad.Profesor;
import com.sistema.academico.dominio.enumeracion.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProfesorRepository extends JpaRepository<Profesor, Long> {

    // Buscar por email
    Optional<Profesor> findByEmail(String email);

    // Verificar si existe un email
    boolean existsByEmail(String email);

    // Buscar por departamento
    List<Profesor> findByDepartamento(Departamento departamento);

    // Buscar por estado
    List<Profesor> findByEstado(Estado estado);

    // Buscar profesores activos de un departamento
    List<Profesor> findByDepartamentoAndEstado(Departamento departamento, Estado estado);

    // Buscar por usuario
    Optional<Profesor> findByUsuarioId(Long usuarioId);
}