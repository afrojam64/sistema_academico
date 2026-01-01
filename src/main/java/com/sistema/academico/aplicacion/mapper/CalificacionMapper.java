package com.sistema.academico.aplicacion.mapper;

import com.sistema.academico.aplicacion.dto.request.CalificacionRequestDTO;
import com.sistema.academico.aplicacion.dto.response.CalificacionResponseDTO;
import com.sistema.academico.dominio.entidad.Calificacion;
import com.sistema.academico.dominio.entidad.Curso;
import com.sistema.academico.dominio.entidad.Estudiante;
import com.sistema.academico.dominio.entidad.Inscripcion;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class CalificacionMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Convierte CalificacionRequestDTO a Entidad Calificacion
     * @param dto DTO con datos de entrada
     * @param inscripcion Inscripción asociada a la calificación
     * @return Entidad Calificacion
     */
    public Calificacion toEntity(CalificacionRequestDTO dto, Inscripcion inscripcion) {
        if (dto == null) {
            return null;
        }

        return Calificacion.builder()
                .inscripcion(inscripcion)
                .nombreEvaluacion(dto.getNombreEvaluacion())
                .nota(dto.getNota())
                .porcentaje(dto.getPorcentaje())
                .observaciones(dto.getObservaciones())
                .fechaCalificacion(LocalDate.now())
                .build();
    }

    /**
     * Convierte Entidad Calificacion a CalificacionResponseDTO
     * @param calificacion Entidad Calificacion
     * @return DTO de respuesta
     */
    public CalificacionResponseDTO toResponseDTO(Calificacion calificacion) {
        if (calificacion == null) {
            return null;
        }

        BigDecimal notaPonderada = calificacion.getNotaPonderada();

        // OBTENER DATOS DE LA INSCRIPCIÓN
        Inscripcion inscripcion = calificacion.getInscripcion();
        Estudiante estudiante = inscripcion.getEstudiante();
        Curso curso = inscripcion.getCurso();

        return CalificacionResponseDTO.builder()
                .id(calificacion.getId())
                .inscripcionId(calificacion.getInscripcion().getId())

                .estudiante(estudiante.getNombreCompleto())
                .curso(curso.getCodigo() + " - " + curso.getNombre())
                .materia(curso.getMateria().getNombre())

                .nombreEvaluacion(calificacion.getNombreEvaluacion())
                .nota(calificacion.getNota() != null ? calificacion.getNota().toString() : "")
                .porcentaje(calificacion.getPorcentaje())
                .notaPonderada(notaPonderada != null ? notaPonderada.toString() : "0.00")
                .fechaCalificacion(calificacion.getFechaCalificacion() != null ? calificacion.getFechaCalificacion().format(FORMATTER) : "")
                .observaciones(calificacion.getObservaciones())
                .esAprobada(calificacion.esAprobada())
                .build();
    }

    /**
     * Actualiza una entidad Calificacion existente con datos del DTO
     * @param calificacion Entidad existente
     * @param dto DTO con datos nuevos
     */
    public void updateEntityFromDTO(Calificacion calificacion, CalificacionRequestDTO dto) {
        if (dto.getNombreEvaluacion() != null) {
            calificacion.setNombreEvaluacion(dto.getNombreEvaluacion());
        }
        if (dto.getNota() != null) {
            calificacion.setNota(dto.getNota());
        }
        if (dto.getPorcentaje() != null) {
            calificacion.setPorcentaje(dto.getPorcentaje());
        }
        if (dto.getObservaciones() != null) {
            calificacion.setObservaciones(dto.getObservaciones());
        }
    }
}