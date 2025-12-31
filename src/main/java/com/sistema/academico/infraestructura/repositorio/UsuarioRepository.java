package com.sistema.academico.infraestructura.repositorio;

import com.sistema.academico.dominio.entidad.Usuario;
import com.sistema.academico.dominio.enumeracion.Estado;
import com.sistema.academico.dominio.enumeracion.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Buscar usuario por nombre de usuario
     */
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);

    /**
     * Verificar si existe un usuario con un nombre de usuario específico
     */
    boolean existsByNombreUsuario(String nombreUsuario);

    /**
     * Buscar usuario por email
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Verificar si existe un usuario con un email específico
     */
    boolean existsByEmail(String email);

    /**
     * Verificar si existe un usuario con una cédula específica
     */
    boolean existsByCedula(String cedula);

    /**
     * Buscar usuarios por rol
     */
    List<Usuario> findByRol(Rol rol);

    /**
     * Buscar usuarios por estado
     */
    List<Usuario> findByEstado(Estado estado);

    /**
     * Buscar usuarios activos (método de conveniencia)
     */
    default List<Usuario> findActivos() {
        return findByEstado(Estado.ACTIVO);
    }

    /**
     * Buscar usuarios por rol y estado
     */
    List<Usuario> findByRolAndEstado(Rol rol, Estado estado);
}