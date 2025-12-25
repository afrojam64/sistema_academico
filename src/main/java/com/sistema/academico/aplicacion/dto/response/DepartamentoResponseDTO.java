package com.sistema.academico.aplicacion.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartamentoResponseDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private String codigo;
    private String estado;
}