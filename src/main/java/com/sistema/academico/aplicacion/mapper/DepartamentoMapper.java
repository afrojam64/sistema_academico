package com.sistema.academico.aplicacion.mapper;

import com.sistema.academico.aplicacion.dto.request.DepartamentoRequestDTO;
import com.sistema.academico.aplicacion.dto.response.DepartamentoResponseDTO;
import com.sistema.academico.dominio.entidad.Departamento;
import com.sistema.academico.dominio.enumeracion.Estado;
import org.springframework.stereotype.Component;

@Component
public class DepartamentoMapper {

    /**
     * Convierte DepartamentoRequestDTO a Entidad Departamento
     * @param dto DTO con datos de entrada
     * @return Entidad Departamento
     */
    public Departamento toEntity(DepartamentoRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        return Departamento.builder()
                .nombre(dto.getNombre())
                .codigo(dto.getCodigo())
                .descripcion(dto.getDescripcion())
                .estado(Estado.ACTIVO)
                .build();
    }

    /**
     * Convierte Entidad Departamento a DepartamentoResponseDTO
     * @param departamento Entidad Departamento
     * @return DTO de respuesta
     */
    public DepartamentoResponseDTO toResponseDTO(Departamento departamento) {
        if (departamento == null) {
            return null;
        }

        return DepartamentoResponseDTO.builder()
                .id(departamento.getId())
                .nombre(departamento.getNombre())
                .codigo(departamento.getCodigo())
                .descripcion(departamento.getDescripcion())
                .estado(departamento.getEstado().name())
                .build();
    }

    /**
     * Actualiza una entidad Departamento existente con datos del DTO
     * @param departamento Entidad existente
     * @param dto DTO con datos nuevos
     */
    public void updateEntityFromDTO(Departamento departamento, DepartamentoRequestDTO dto) {
        if (dto.getNombre() != null) {
            departamento.setNombre(dto.getNombre());
        }
        if (dto.getCodigo() != null) {
            departamento.setCodigo(dto.getCodigo());
        }
        if (dto.getDescripcion() != null) {
            departamento.setDescripcion(dto.getDescripcion());
        }
    }
}