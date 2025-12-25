package com.sistema.academico.infraestructura.repositorio;

import com.sistema.academico.dominio.entidad.Departamento;
import com.sistema.academico.dominio.enumeracion.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {

    // Buscar por código
    Optional<Departamento> findByCodigo(String codigo);

    // Verificar si existe un código
    boolean existsByCodigo(String codigo);

    // Verificar si existe un nombre
    boolean existsByNombre(String nombre);

    // Buscar por estado
    List<Departamento> findByEstado(Estado estado);

    // Buscar departamentos activos
    default List<Departamento> findActivos() {
        return findByEstado(Estado.ACTIVO);
    }
}