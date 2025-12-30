package com.sistema.academico.aplicacion.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MateriaResponseDTO {

    private Long id;
    private String nombre;
    private String codigo;
    private String descripcion;
    private Integer creditos;
    private Long departamentoId;
    private String departamentoNombre;
    private String departamentoCodigo;
    private String estado;
}