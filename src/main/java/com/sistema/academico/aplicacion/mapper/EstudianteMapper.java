package com.sistema.academico.aplicacion.mapper;

import com.sistema.academico.aplicacion.dto.request.EstudianteRequestDTO;
import com.sistema.academico.aplicacion.dto.response.EstudianteResponseDTO;
import com.sistema.academico.dominio.entidad.Estudiante;
import com.sistema.academico.dominio.entidad.Usuario;
import com.sistema.academico.dominio.enumeracion.Estado;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class EstudianteMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Convierte EstudianteRequestDTO a Entidad Estudiante
     * @param dto DTO con datos de entrada
     * @param usuario Usuario asociado al estudiante
     * @return Entidad Estudiante
     */
    public Estudiante toEntity(EstudianteRequestDTO dto, Usuario usuario) {
        if (dto == null) {
            return null;
        }

        return Estudiante.builder()
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .email(dto.getEmail())
                .telefono(dto.getTelefono())
                .matricula(dto.getMatricula())
                .usuario(usuario)
                .estado(Estado.ACTIVO)
                .fechaIngreso(dto.getFechaIngreso() != null ? dto.getFechaIngreso() : LocalDate.now())
                .build();
    }

    /**
     * Convierte Entidad Estudiante a EstudianteResponseDTO
     * @param estudiante Entidad Estudiante
     * @return DTO de respuesta
     */
    public EstudianteResponseDTO toResponseDTO(Estudiante estudiante) {
        if (estudiante == null) {
            return null;
        }

        return EstudianteResponseDTO.builder()
                .id(estudiante.getId())
                .nombreCompleto(estudiante.getNombre() + " " + estudiante.getApellido())
                .email(estudiante.getEmail())
                .telefono(estudiante.getTelefono())
                .matricula(estudiante.getMatricula())
                .fechaIngreso(estudiante.getFechaIngreso() != null ? estudiante.getFechaIngreso().format(FORMATTER) : "")
                .estado(estudiante.getEstado().name())
                .build();
    }

    /**
     * Actualiza una entidad Estudiante existente con datos del DTO
     * @param estudiante Entidad existente
     * @param dto DTO con datos nuevos
     */
    public void updateEntityFromDTO(Estudiante estudiante, EstudianteRequestDTO dto) {
        if (dto.getNombre() != null) {
            estudiante.setNombre(dto.getNombre());
        }
        if (dto.getApellido() != null) {
            estudiante.setApellido(dto.getApellido());
        }
        if (dto.getEmail() != null) {
            estudiante.setEmail(dto.getEmail());
        }
        if (dto.getTelefono() != null) {
            estudiante.setTelefono(dto.getTelefono());
        }
        if (dto.getMatricula() != null) {
            estudiante.setMatricula(dto.getMatricula());
        }
        if (dto.getFechaIngreso() != null) {
            estudiante.setFechaIngreso(dto.getFechaIngreso());
        }
    }
}