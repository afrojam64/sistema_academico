package com.sistema.academico.infraestructura.seguridad;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Componente responsable de generar y validar tokens JWT.
 *
 * Funciones principales:
 * 1. Generar token JWT cuando el usuario hace login
 * 2. Validar token JWT en cada petición
 * 3. Extraer información del token (nombreUsuario, expiración)
 */
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    /**
     * Genera la clave secreta para firmar los tokens JWT
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Genera un token JWT para el usuario autenticado
     *
     * @param authentication Objeto de Spring Security con info del usuario
     * @return Token JWT como String
     */
    public String generateToken(Authentication authentication) {
        String nombreUsuario = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(nombreUsuario)           // Usuario
                .issuedAt(now)                    // Fecha de creación
                .expiration(expiryDate)           // Fecha de expiración
                .signWith(getSigningKey())        // Firma con clave secreta
                .compact();
    }

    /**
     * Extrae el nombreUsuario del token JWT
     *
     * @param token Token JWT
     * @return Nombre de usuario
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    /**
     * Valida si el token JWT es válido
     *
     * Verifica:
     * - Firma válida
     * - No expirado
     * - Formato correcto
     *
     * @param token Token JWT
     * @return true si es válido, false si no
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException ex) {
            System.err.println("Firma JWT inválida: " + ex.getMessage());
        } catch (MalformedJwtException ex) {
            System.err.println("Token JWT malformado: " + ex.getMessage());
        } catch (ExpiredJwtException ex) {
            System.err.println("Token JWT expirado: " + ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            System.err.println("Token JWT no soportado: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            System.err.println("JWT claims string vacío: " + ex.getMessage());
        }
        return false;
    }
}