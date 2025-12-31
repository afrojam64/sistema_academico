package com.sistema.academico.aplicacion.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstudianteResponseDTO {

    private Long id;

    // Datos del usuario asociado
    private Long usuarioId;
    private String nombreUsuario;
    private String nombre;
    private String apellido;
    private String nombreCompleto;
    private String email;
    private String cedula;
    private String telefono;

    // Datos específicos del estudiante
    private String codigoEstudiante;
    private String carrera;
    private Integer semestre;
    private String fechaIngreso;
    private String estado;
}