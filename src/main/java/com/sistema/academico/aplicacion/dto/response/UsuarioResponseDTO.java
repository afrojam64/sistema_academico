package com.sistema.academico.aplicacion.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponseDTO {

    private Long id;

    // Credenciales
    private String nombreUsuario;
    private String email;
    private String rol;
    private String estado;

    // Datos personales
    private String nombre;
    private String apellido;
    private String cedula;
    private String telefono;
    private String fechaNacimiento;
    private String direccion;

    // Auditoría
    private String fechaCreacion;
    private String fechaActualizacion;

    /**
     * Retorna el nombre completo
     */
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
}