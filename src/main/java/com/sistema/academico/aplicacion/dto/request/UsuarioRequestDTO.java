package com.sistema.academico.aplicacion.dto.request;

import com.sistema.academico.dominio.enumeracion.Rol;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioRequestDTO {

    // ==========================================
    // CREDENCIALES
    // ==========================================

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 4, max = 50, message = "El nombre de usuario debe tener entre 4 y 50 caracteres")
    private String nombreUsuario;

    //@NotBlank(message = "La contraseña es obligatoria")
    //@Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String contrasena;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    private String email;

    @NotNull(message = "El rol es obligatorio")
    private Rol rol;

    // ==========================================
    // DATOS PERSONALES (NUEVOS)
    // ==========================================

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 100, message = "El apellido debe tener entre 2 y 100 caracteres")
    private String apellido;

    @NotBlank(message = "La cédula es obligatoria")
    @Size(min = 5, max = 20, message = "La cédula debe tener entre 5 y 20 caracteres")
    private String cedula;

    @Pattern(regexp = "^[0-9+\\-() ]*$", message = "El teléfono solo puede contener números y caracteres válidos")
    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    private String telefono;

    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    private LocalDate fechaNacimiento;

    @Size(max = 500, message = "La dirección no puede exceder 500 caracteres")
    private String direccion;
}