package com.sistema.academico.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configuración de seguridad HTTP
     *
     * TEMPORAL: Permite todas las peticiones sin autenticación
     * Útil para probar los endpoints REST con Postman
     *
     * IMPORTANTE: En producción, esto debe configurarse con autenticación real
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Desactivar CSRF (Solo para APIs REST)
                .csrf(csrf -> csrf.disable())

                // Permitir todas las peticiones sin autenticación
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll());
        return  http.build();
    }
}