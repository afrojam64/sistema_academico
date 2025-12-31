package com.sistema.academico.aplicacion.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfesorResponseDTO {

    private Long id;

    // Datos del usuario asociado
    private Long usuarioId;
    private String nombreUsuario;
    private String nombre;
    private String apellido;
    private String nombreCompleto;
    private String email;
    private String cedula;  // ← NUEVO: Viene del usuario

    // Datos específicos del profesor
    private String telefono;
    private String especialidad;

    // Datos del departamento
    private Long departamentoId;
    private String departamentoNombre;
    private String departamentoCodigo;

    private String fechaContratacion;
    private String estado;
}