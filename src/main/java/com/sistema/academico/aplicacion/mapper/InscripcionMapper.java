package com.sistema.academico.aplicacion.mapper;

import com.sistema.academico.aplicacion.dto.request.InscripcionRequestDTO;
import com.sistema.academico.aplicacion.dto.response.InscripcionResponseDTO;
import com.sistema.academico.dominio.entidad.Curso;
import com.sistema.academico.dominio.entidad.Estudiante;
import com.sistema.academico.dominio.entidad.Inscripcion;
import com.sistema.academico.dominio.enumeracion.EstadoInscripcion;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class InscripcionMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Convierte InscripcionRequestDTO a Entidad Inscripcion
     * @param dto DTO con datos de entrada
     * @param estudiante Estudiante asociado a la inscripción
     * @param curso Curso asociado a la inscripción
     * @return Entidad Inscripcion
     */
    public Inscripcion toEntity(InscripcionRequestDTO dto, Estudiante estudiante, Curso curso) {
        if (dto == null) {
            return null;
        }

        return Inscripcion.builder()
                .estudiante(estudiante)
                .curso(curso)
                .fechaInscripcion(LocalDate.now())
                .estado(EstadoInscripcion.ACTIVO)
                .build();
    }

    /**
     * Convierte Entidad Inscripcion a InscripcionResponseDTO
     * @param inscripcion Entidad Inscripcion
     * @return DTO de respuesta
     */
    public InscripcionResponseDTO toResponseDTO(Inscripcion inscripcion) {
        if (inscripcion == null) {
            return null;
        }

        return InscripcionResponseDTO.builder()
                .id(inscripcion.getId())
                .estudiante(inscripcion.getEstudiante().getNombreCompleto())
                .curso(inscripcion.getCurso().getCodigo() + " - " + inscripcion.getCurso().getNombre())
                .fechaInscripcion(inscripcion.getFechaInscripcion() != null ? inscripcion.getFechaInscripcion().format(FORMATTER) : "")
                .estado(inscripcion.getEstado().name())
                .build();
    }

    /**
     * Actualiza el estado de una inscripción
     * @param inscripcion Entidad existente
     * @param nuevoEstado Nuevo estado
     */
    public void updateEstado(Inscripcion inscripcion, EstadoInscripcion nuevoEstado) {
        inscripcion.setEstado(nuevoEstado);
    }
}