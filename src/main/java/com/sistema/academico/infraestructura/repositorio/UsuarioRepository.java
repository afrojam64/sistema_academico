package com.sistema.academico.infraestructura.repositorio;

import com.sistema.academico.dominio.entidad.Usuario;
import com.sistema.academico.dominio.enumeracion.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Buscar por nombre de usuario (login)
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);

    // Verificar si existe un nombre de usuario
    boolean existsByNombreUsuario(String nombreUsuario);

    // Buscar usuarios por estado
    List<Usuario> findByEstado(Estado estado);

    /**
     * Verifica si existe un usuario con el email dado
     */
    boolean existsByEmail(String email);

    // Buscar usuarios activos
    default List<Usuario> findActivos() {
        return findByEstado(Estado.ACTIVO);
    }
}