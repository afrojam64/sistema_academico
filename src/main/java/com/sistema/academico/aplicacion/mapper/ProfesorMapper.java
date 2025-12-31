package com.sistema.academico.aplicacion.mapper;

import com.sistema.academico.aplicacion.dto.request.ProfesorRequestDTO;
import com.sistema.academico.aplicacion.dto.response.ProfesorResponseDTO;
import com.sistema.academico.dominio.entidad.Departamento;
import com.sistema.academico.dominio.entidad.Profesor;
import com.sistema.academico.dominio.entidad.Usuario;
import com.sistema.academico.dominio.enumeracion.Estado;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class ProfesorMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Convierte ProfesorRequestDTO a Entidad Profesor
     * @param dto DTO con datos de entrada
     * @param usuario Usuario asociado al profesor (ya creado)
     * @param departamento Departamento asociado al profesor
     * @return Entidad Profesor
     */
    public Profesor toEntity(ProfesorRequestDTO dto, Usuario usuario, Departamento departamento) {
        if (dto == null) {
            return null;
        }

        return Profesor.builder()
                .usuario(usuario)
                .telefono(dto.getTelefono())
                .especialidad(dto.getEspecialidad())
                .departamento(departamento)
                .estado(Estado.ACTIVO)
                .fechaContratacion(dto.getFechaContratacion() != null ?
                        dto.getFechaContratacion() : LocalDate.now())
                .build();
    }

    /**
     * Convierte Entidad Profesor a ProfesorResponseDTO
     * Obtiene nombre, apellido y email del usuario asociado
     * @param profesor Entidad Profesor
     * @return DTO de respuesta
     */
    public ProfesorResponseDTO toResponseDTO(Profesor profesor) {
        if (profesor == null) {
            return null;
        }

        Usuario usuario = profesor.getUsuario();

        return ProfesorResponseDTO.builder()
                .id(profesor.getId())
                .usuarioId(usuario.getId())
                .nombreUsuario(usuario.getNombreUsuario())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .nombreCompleto(usuario.getNombre() + " " + usuario.getApellido())
                .email(usuario.getEmail())
                .cedula(usuario.getCedula())  // ← NUEVO: Obtiene cédula del usuario
                .telefono(profesor.getTelefono())
                .especialidad(profesor.getEspecialidad())
                .departamentoId(profesor.getDepartamento().getId())
                .departamentoNombre(profesor.getDepartamento().getNombre())
                .departamentoCodigo(profesor.getDepartamento().getCodigo())
                .fechaContratacion(profesor.getFechaContratacion() != null ?
                        profesor.getFechaContratacion().format(FORMATTER) : "")
                .estado(profesor.getEstado().name())
                .build();
    }

    /**
     * Actualiza una entidad Profesor existente con datos del DTO
     * @param profesor Entidad existente
     * @param dto DTO con datos nuevos
     * @param departamento Departamento nuevo (opcional)
     */
    public void updateEntityFromDTO(Profesor profesor, ProfesorRequestDTO dto, Departamento departamento) {
        if (dto.getTelefono() != null) {
            profesor.setTelefono(dto.getTelefono());
        }
        if (dto.getEspecialidad() != null) {
            profesor.setEspecialidad(dto.getEspecialidad());
        }
        if (dto.getFechaContratacion() != null) {
            profesor.setFechaContratacion(dto.getFechaContratacion());
        }
        if (departamento != null) {
            profesor.setDepartamento(departamento);
        }

        // Actualizar datos del usuario (nombre, apellido, email, cedula)
        Usuario usuario = profesor.getUsuario();
        if (dto.getNombre() != null) {
            usuario.setNombre(dto.getNombre());
        }
        if (dto.getApellido() != null) {
            usuario.setApellido(dto.getApellido());
        }
        if (dto.getEmail() != null) {
            usuario.setEmail(dto.getEmail());
        }
        if (dto.getCedula() != null) {  // ← NUEVO: Actualiza cédula del usuario
            usuario.setCedula(dto.getCedula());
        }
    }
}