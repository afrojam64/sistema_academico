package com.sistema.academico.aplicacion.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InscripcionResponseDTO {

    private Long id;
    private String estudiante;
    private String curso;
    private String fechaInscripcion;
    private String estado;
}