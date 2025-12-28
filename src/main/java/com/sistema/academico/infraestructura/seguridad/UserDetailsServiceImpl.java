package com.sistema.academico.infraestructura.seguridad;

import com.sistema.academico.dominio.entidad.Usuario;
import com.sistema.academico.infraestructura.repositorio.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Implementación de UserDetailsService de Spring Security.
 *
 * Spring Security usa este servicio para:
 * 1. Cargar el usuario durante el login
 * 2. Cargar el usuario cuando se valida un token JWT
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Carga un usuario por su nombre de usuario.
     *
     * Este método es llamado por Spring Security durante:
     * - El proceso de login (AuthenticationManager)
     * - La validación de tokens JWT (JwtAuthenticationFilter)
     *
     * @param nombreUsuario Nombre de usuario
     * @return UserDetails con la información del usuario
     * @throws UsernameNotFoundException Si el usuario no existe
     */
    @Override
    public UserDetails loadUserByUsername(String nombreUsuario) throws UsernameNotFoundException {
        // 1. Buscar el usuario en la base de datos
        Usuario usuario = usuarioRepository.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado con nombreUsuario: " + nombreUsuario
                ));

        // 2. Convertir el rol del usuario a GrantedAuthority
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name())
        );

        // 3. Crear y retornar el objeto UserDetails
        return new User(
                usuario.getNombreUsuario(),    // username
                usuario.getContrasena(),       // password (encriptada)
                authorities                    // roles/authorities
        );
    }
}