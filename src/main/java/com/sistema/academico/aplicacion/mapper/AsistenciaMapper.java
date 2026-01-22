package com.sistema.academico.aplicacion.mapper;

import com.sistema.academico.aplicacion.dto.request.AsistenciaRequestDTO;
import com.sistema.academico.aplicacion.dto.response.AsistenciaResponseDTO;
import com.sistema.academico.dominio.entidad.Asistencia;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entre entidad Asistencia y sus DTOs.
 *
 * UBICACIÓN EN ARQUITECTURA:
 * - Capa: APLICACIÓN
 * - Paquete: aplicacion.mapper
 */
@Component
public class AsistenciaMapper {

    public Asistencia toEntity(AsistenciaRequestDTO dto) {
        if (dto == null) return null;
        
        Asistencia asistencia = new Asistencia();
        asistencia.setFecha(dto.getFecha());
        asistencia.setPresente(dto.getPresente());
        asistencia.setObservaciones(dto.getObservaciones());
        
        return asistencia;
    }

    public AsistenciaResponseDTO toDTO(Asistencia entity) {
        if (entity == null) return null;

        return AsistenciaResponseDTO.builder()
                .id(entity.getId())
                .inscripcionId(entity.getInscripcion().getId())
                .nombreEstudiante(entity.getInscripcion().getEstudiante().getUsuario().getNombreCompleto())
                .fecha(entity.getFecha())
                .presente(entity.isPresente())
                .observaciones(entity.getObservaciones())
                .build();
    }

    public List<AsistenciaResponseDTO> toDTOList(List<Asistencia> asistencias) {
        if (asistencias == null) return List.of();
        return asistencias.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}