package com.sistema.academico.aplicacion.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfesorResponseDTO {

    private Long id;
    private String nombre;
    private String apellido;
    private String nombreCompleto;
    private String email;
    private String telefono;
    private String especialidad;
    private Long departamentoId;
    private String departamentoNombre;
    private String departamentoCodigo;
    private String fechaContratacion;
    private String estado;
}