package com.sistema.academico.aplicacion.dto.response;

import lombok.*;

/**
 * DTO para devolver la respuesta del login exitoso.
 *
 * Contiene:
 * - Token JWT
 * - Tipo de token (Bearer)
 * - Información del usuario autenticado
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {

    /**
     * Token JWT para autenticación
     * El cliente debe enviarlo en el header: Authorization: Bearer {token}
     */
    private String token;

    /**
     * Tipo de token (siempre "Bearer" para JWT)
     */
    private String type = "Bearer";

    /**
     * ID del usuario autenticado
     */
    private Long id;

    /**
     * Nombre de usuario
     */
    private String nombreUsuario;

    /**
     * Email del usuario
     */
    private String email;

    /**
     * Rol del usuario (SUPER_ADMIN, ADMIN, PROFESOR, ESTUDIANTE)
     */
    private String rol;
}