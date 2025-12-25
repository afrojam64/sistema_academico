package com.sistema.academico.aplicacion.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CursoResponseDTO {

    private Long id;
    private String nombre;
    private String codigo;
    private String materia;
    private String profesor;
    private Integer cupoMaximo;
    private Integer cupoActual;
    private Integer cuposDisponibles;
    private String periodo;
    private String fechaInicio;
    private String fechaFin;
    private String estado;
}