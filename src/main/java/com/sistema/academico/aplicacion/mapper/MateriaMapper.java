package com.sistema.academico.aplicacion.mapper;

import com.sistema.academico.aplicacion.dto.request.MateriaRequestDTO;
import com.sistema.academico.aplicacion.dto.response.MateriaResponseDTO;
import com.sistema.academico.dominio.entidad.Materia;
import com.sistema.academico.dominio.entidad.Profesor;
import com.sistema.academico.dominio.enumeracion.Estado;
import org.springframework.stereotype.Component;

@Component
public class MateriaMapper {

    /**
     * Convierte MateriaRequestDTO a Entidad Materia
     * @param dto DTO con datos de entrada
     * @param profesor Profesor responsable de la materia
     * @return Entidad Materia
     */
    public Materia toEntity(MateriaRequestDTO dto, Profesor profesor) {
        if (dto == null) {
            return null;
        }

        return Materia.builder()
                .nombre(dto.getNombre())
                .codigo(dto.getCodigo())
                .descripcion(dto.getDescripcion())
                .creditos(dto.getCreditos())
                .profesor(profesor)
                .estado(Estado.ACTIVO)
                .build();
    }

    /**
     * Convierte Entidad Materia a MateriaResponseDTO
     * @param materia Entidad Materia
     * @return DTO de respuesta
     */
    public MateriaResponseDTO toResponseDTO(Materia materia) {
        if (materia == null) {
            return null;
        }

        return MateriaResponseDTO.builder()
                .id(materia.getId())
                .nombre(materia.getNombre())
                .codigo(materia.getCodigo())
                .descripcion(materia.getDescripcion())
                .creditos(materia.getCreditos())
                .profesor(materia.getProfesor().getNombre() + " " + materia.getProfesor().getApellido())
                .estado(materia.getEstado().name())
                .build();
    }

    /**
     * Actualiza una entidad Materia existente con datos del DTO
     * @param materia Entidad existente
     * @param dto DTO con datos nuevos
     * @param profesor Profesor nuevo (opcional)
     */
    public void updateEntityFromDTO(Materia materia, MateriaRequestDTO dto, Profesor profesor) {
        if (dto.getNombre() != null) {
            materia.setNombre(dto.getNombre());
        }
        if (dto.getCodigo() != null) {
            materia.setCodigo(dto.getCodigo());
        }
        if (dto.getDescripcion() != null) {
            materia.setDescripcion(dto.getDescripcion());
        }
        if (dto.getCreditos() != null) {
            materia.setCreditos(dto.getCreditos());
        }
        if (profesor != null) {
            materia.setProfesor(profesor);
        }
    }
}