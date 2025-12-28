package com.sistema.academico.aplicacion.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * DTO para recibir las credenciales de login del usuario.
 *
 * Se usa en el endpoint POST /auth/login
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDTO {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    private String nombreUsuario;

    @NotBlank(message = "La contraseña es obligatoria")
    private String contrasena;
}