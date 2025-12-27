package com.sistema.academico.aplicacion.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponseDTO {

    private Long id;
    private String nombreUsuario;
    private String email;
    private String rol;
    private String estado;
    private String fechaCreacion;
}