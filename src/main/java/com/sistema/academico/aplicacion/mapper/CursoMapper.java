package com.sistema.academico.aplicacion.mapper;

import com.sistema.academico.aplicacion.dto.request.CursoRequestDTO;
import com.sistema.academico.aplicacion.dto.response.CursoResponseDTO;
import com.sistema.academico.dominio.entidad.Curso;
import com.sistema.academico.dominio.entidad.Materia;
import com.sistema.academico.dominio.entidad.Profesor;
import com.sistema.academico.dominio.enumeracion.Estado;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class CursoMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Convierte CursoRequestDTO a Entidad Curso
     * @param dto DTO con datos de entrada
     * @param materia Materia asociada al curso
     * @param profesor Profesor asociado al curso
     * @return Entidad Curso
     */
    public Curso toEntity(CursoRequestDTO dto, Materia materia, Profesor profesor) {
        if (dto == null) {
            return null;
        }

        return Curso.builder()
                .nombre(dto.getNombre())
                .codigo(dto.getCodigo())
                .materia(materia)
                .profesor(profesor)
                .cupoMaximo(dto.getCupoMaximo())
                .cupoActual(0)
                .periodo(dto.getPeriodo())
                .fechaInicio(dto.getFechaInicio())
                .fechaFin(dto.getFechaFin())
                .estado(Estado.ACTIVO)
                .build();
    }

    /**
     * Convierte Entidad Curso a CursoResponseDTO
     * @param curso Entidad Curso
     * @return DTO de respuesta
     */
    public CursoResponseDTO toResponseDTO(Curso curso) {
        if (curso == null) {
            return null;
        }

        return CursoResponseDTO.builder()
                .id(curso.getId())
                .nombre(curso.getNombre())
                .codigo(curso.getCodigo())
                .materia(curso.getMateria().getNombre())
                .profesor(curso.getProfesor().getNombre() + " " + curso.getProfesor().getApellido())
                .cupoMaximo(curso.getCupoMaximo())
                .cupoActual(curso.getCupoActual())
                .cuposDisponibles(curso.getCupoMaximo() - curso.getCupoActual())
                .periodo(curso.getPeriodo())
                .fechaInicio(curso.getFechaInicio() != null ? curso.getFechaInicio().format(FORMATTER) : "")
                .fechaFin(curso.getFechaFin() != null ? curso.getFechaFin().format(FORMATTER) : "")
                .estado(curso.getEstado().name())
                .build();
    }

    /**
     * Actualiza una entidad Curso existente con datos del DTO
     * @param curso Entidad existente
     * @param dto DTO con datos nuevos
     * @param materia Materia nueva (opcional)
     * @param profesor Profesor nuevo (opcional)
     */
    public void updateEntityFromDTO(Curso curso, CursoRequestDTO dto, Materia materia, Profesor profesor) {
        if (dto.getNombre() != null) {
            curso.setNombre(dto.getNombre());
        }
        if (dto.getCodigo() != null) {
            curso.setCodigo(dto.getCodigo());
        }
        if (dto.getCupoMaximo() != null) {
            curso.setCupoMaximo(dto.getCupoMaximo());
        }
        if (dto.getPeriodo() != null) {
            curso.setPeriodo(dto.getPeriodo());
        }
        if (dto.getFechaInicio() != null) {
            curso.setFechaInicio(dto.getFechaInicio());
        }
        if (dto.getFechaFin() != null) {
            curso.setFechaFin(dto.getFechaFin());
        }
        if (materia != null) {
            curso.setMateria(materia);
        }
        if (profesor != null) {
            curso.setProfesor(profesor);
        }
    }
}