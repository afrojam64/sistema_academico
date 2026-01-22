package com.sistema.academico.aplicacion.mapper;

import com.sistema.academico.aplicacion.dto.request.HorarioRequestDTO;
import com.sistema.academico.aplicacion.dto.response.HorarioResponseDTO;
import com.sistema.academico.dominio.entidad.Horario;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entre entidad Horario y sus DTOs.
 *
 * UBICACIÓN EN ARQUITECTURA:
 * - Capa: APLICACIÓN
 * - Paquete: aplicacion.mapper
 */
@Component
public class HorarioMapper {

    public Horario toEntity(HorarioRequestDTO dto) {
        if (dto == null) return null;
        
        Horario horario = new Horario();
        horario.setDia(dto.getDia());
        horario.setHoraInicio(dto.getHoraInicio());
        horario.setHoraFin(dto.getHoraFin());
        horario.setAula(dto.getAula());
        
        return horario;
    }

    public HorarioResponseDTO toDTO(Horario entity) {
        if (entity == null) return null;

        return HorarioResponseDTO.builder()
                .id(entity.getId())
                .dia(entity.getDia())
                .diaNombre(entity.getDia().getNombreMostrar())
                .horaInicio(entity.getHoraInicio())
                .horaFin(entity.getHoraFin())
                .aula(entity.getAula())
                .build();
    }

    public List<HorarioResponseDTO> toDTOList(List<Horario> horarios) {
        if (horarios == null) return List.of();
        return horarios.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}