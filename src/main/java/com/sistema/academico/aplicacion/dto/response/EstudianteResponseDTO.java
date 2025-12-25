package com.sistema.academico.aplicacion.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstudianteResponseDTO {

    private Long id;
    private String nombreCompleto;
    private String email;
    private String telefono;
    private String matricula;
    private String fechaIngreso;
    private String estado;
}