package com.sistema.academico.aplicacion.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MateriaRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100)
    private String nombre;

    @NotBlank(message = "El código es obligatorio")
    @Size(min = 2, max = 20)
    private String codigo;

    @Size(max = 500)
    private String descripcion;

    @NotNull(message = "Los créditos son obligatorios")
    @Min(value = 1, message = "Debe tener al menos 1 crédito")
    @Max(value = 10, message = "No puede tener más de 10 créditos")
    private Integer creditos;

    @NotNull(message = "El departamento es obligatorio")
    private Long departamentoId;
}