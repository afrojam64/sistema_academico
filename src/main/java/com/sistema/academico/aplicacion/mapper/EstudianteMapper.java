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

    public Estudiante toEntity(EstudianteRequestDTO dto, Usuario usuario) {
        if (dto == null) {
            return null;
        }

        return Estudiante.builder()
                .usuario(usuario)
                .codigoEstudiante(dto.getCodigoEstudiante())
                .carrera(dto.getCarrera())
                .semestre(dto.getSemestre())
                .estado(Estado.ACTIVO)
                .fechaIngreso(dto.getFechaIngreso() != null ?
                        dto.getFechaIngreso() : LocalDate.now())
                .build();
    }

    public EstudianteResponseDTO toResponseDTO(Estudiante estudiante) {
        if (estudiante == null) {
            return null;
        }

        Usuario usuario = estudiante.getUsuario();

        return EstudianteResponseDTO.builder()
                .id(estudiante.getId())
                .usuarioId(usuario.getId())
                .nombreUsuario(usuario.getNombreUsuario())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .nombreCompleto(usuario.getNombre() + " " + usuario.getApellido())
                .email(usuario.getEmail())
                .cedula(usuario.getCedula())
                .telefono(usuario.getTelefono())
                .codigoEstudiante(estudiante.getCodigoEstudiante())
                .carrera(estudiante.getCarrera())
                .semestre(estudiante.getSemestre())
                .fechaIngreso(estudiante.getFechaIngreso() != null ?
                        estudiante.getFechaIngreso().format(FORMATTER) : "")
                .estado(estudiante.getEstado().name())
                .build();
    }

    public void updateEntityFromDTO(Estudiante estudiante, EstudianteRequestDTO dto) {
        if (dto.getCodigoEstudiante() != null) {
            estudiante.setCodigoEstudiante(dto.getCodigoEstudiante());
        }
        if (dto.getCarrera() != null) {
            estudiante.setCarrera(dto.getCarrera());
        }
        if (dto.getSemestre() != null) {
            estudiante.setSemestre(dto.getSemestre());
        }
        if (dto.getFechaIngreso() != null) {
            estudiante.setFechaIngreso(dto.getFechaIngreso());
        }

        // Actualizar datos del usuario
        Usuario usuario = estudiante.getUsuario();
        if (dto.getNombre() != null) {
            usuario.setNombre(dto.getNombre());
        }
        if (dto.getApellido() != null) {
            usuario.setApellido(dto.getApellido());
        }
        if (dto.getEmail() != null) {
            usuario.setEmail(dto.getEmail());
        }
        if (dto.getCedula() != null) {
            usuario.setCedula(dto.getCedula());
        }
        if (dto.getTelefono() != null) {
            usuario.setTelefono(dto.getTelefono());
        }
    }
}