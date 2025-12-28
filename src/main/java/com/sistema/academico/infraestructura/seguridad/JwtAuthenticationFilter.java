package com.sistema.academico.infraestructura.seguridad;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro que intercepta cada petición HTTP y valida el token JWT.
 *
 * Flujo:
 * 1. Extrae el token del header Authorization
 * 2. Valida que el token sea válido
 * 3. Carga el usuario desde la base de datos
 * 4. Establece la autenticación en el contexto de Spring Security
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Método que se ejecuta en cada petición HTTP
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 1. Extraer el token JWT del header Authorization
            String jwt = getJwtFromRequest(request);

            // 2. Validar que el token exista y sea válido
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {

                // 3. Extraer el nombreUsuario del token
                String nombreUsuario = tokenProvider.getUsernameFromToken(jwt);

                // 4. Cargar los detalles del usuario desde la base de datos
                UserDetails userDetails = userDetailsService.loadUserByUsername(nombreUsuario);

                // 5. Crear el objeto de autenticación
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 6. Establecer la autenticación en el contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            System.err.println("No se pudo establecer la autenticación del usuario: " + ex.getMessage());
        }

        // 7. Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }

    /**
     * Extrae el token JWT del header Authorization
     *
     * Header esperado: Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
     *
     * @param request Petición HTTP
     * @return Token JWT (sin el prefijo "Bearer ")
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        // Verificar que el header exista y empiece con "Bearer "
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            // Retornar el token sin el prefijo "Bearer "
            return bearerToken.substring(7);
        }

        return null;
    }
}