package com.sistema.academico.aplicacion.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfesorResponseDTO {

    private Long id;
    private String nombreCompleto;
    private String email;
    private String telefono;
    private String especialidad;
    private String departamento;
    private String fechaContratacion;
    private String estado;
}