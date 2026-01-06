package com.sistema.academico.config;

import com.sistema.academico.dominio.entidad.Usuario;
import com. sistema.academico.dominio. enumeracion.Estado;
import com.sistema.academico.dominio.enumeracion.Rol;
import com. sistema.academico.infraestructura.repositorio. UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework. context.annotation.Bean;
import org.springframework.context.annotation. Configuration;
import org.springframework. security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java. time.LocalDateTime;

/**
 * DataSeederConfig:  Crea automáticamente un usuario SUPER_ADMIN y ADMIN
 * al iniciar la aplicación si no existen.
 *
 * UBICACIÓN: com.sistema.academico.config
 * FUNCIONALIDAD: Se ejecuta una sola vez al arrancar la app
 * SEGURIDAD: Las contraseñas DEBEN cambiarse en producción
 *
 * Usuarios creados:
 * - SUPER_ADMIN: superadmin / superseguro123
 * - ADMIN: admin / adminseguro123
 */
@Configuration
@RequiredArgsConstructor
public class DataSeederConfig {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public ApplicationRunner seedAdminUsers() {
        return args -> {
            crearSuperAdmin();
            crearAdmin();
        };
    }

    /**
     * Crea el usuario SUPER_ADMIN si no existe
     */
    private void crearSuperAdmin() {
        if (!usuarioRepository. existsByNombreUsuario("superadmin")) {
            Usuario superAdmin = Usuario.builder()
                    .nombreUsuario("superadmin")
                    .contrasena(passwordEncoder.encode("superseguro123"))
                    .email("superadmin@sistema.com")
                    .rol(Rol.SUPER_ADMIN)
                    .estado(Estado.ACTIVO)
                    .nombre("Super")
                    .apellido("Administrador")
                    .cedula("0000000001")
                    .telefono("0999999999")
                    .fechaNacimiento(LocalDate.of(1980, 1, 1))
                    . direccion("Calle Principal 123, Sistema Académico")
                    .build();
            usuarioRepository.save(superAdmin);
            System.out.println("✅ Usuario SUPER_ADMIN creado correctamente");
        }
    }

    /**
     * Crea el usuario ADMIN si no existe
     */
    private void crearAdmin() {
        if (!usuarioRepository.existsByNombreUsuario("admin")) {
            Usuario admin = Usuario.builder()
                    .nombreUsuario("admin")
                    .contrasena(passwordEncoder.encode("adminseguro123"))
                    .email("admin@sistema.com")
                    .rol(Rol.ADMIN)
                    .estado(Estado.ACTIVO)
                    .nombre("Admin")
                    .apellido("Sistema")
                    .cedula("0000000002")
                    .telefono("0988888888")
                    .fechaNacimiento(LocalDate.of(1985, 6, 15))
                    .direccion("Avenida Administrativa 456, Sistema Académico")
                    .build();
            usuarioRepository.save(admin);
            System.out.println("✅ Usuario ADMIN creado correctamente");
        }
    }
}