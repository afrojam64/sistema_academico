package com.sistema.academico.aplicacion.mapper;

import com.sistema.academico.aplicacion.dto.request.UsuarioRequestDTO;
import com.sistema.academico.aplicacion.dto.response.UsuarioResponseDTO;
import com.sistema.academico.dominio.entidad.Usuario;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

/**
 * Mapper para convertir entre Usuario y sus DTOs
 */
@Component
public class UsuarioMapper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Convierte UsuarioRequestDTO a Usuario
     */
    public Usuario toEntity(UsuarioRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        return Usuario.builder()
                .nombreUsuario(dto.getNombreUsuario())
                .contrasena(dto.getContrasena())
                .email(dto.getEmail())
                .rol(dto.getRol())
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .cedula(dto.getCedula())
                .telefono(dto.getTelefono())
                .fechaNacimiento(dto.getFechaNacimiento())
                .direccion(dto.getDireccion())
                .build();
    }

    /**
     * Convierte Usuario a UsuarioResponseDTO
     */
    public UsuarioResponseDTO toDTO(Usuario entity) {
        if (entity == null) {
            return null;
        }

        return UsuarioResponseDTO.builder()
                .id(entity.getId())
                .nombreUsuario(entity.getNombreUsuario())
                .email(entity.getEmail())
                .rol(formatRol(entity.getRol()))
                .estado(formatEstado(entity.getEstado()))
                .nombre(entity.getNombre())
                .apellido(entity.getApellido())
                .cedula(entity.getCedula())
                .telefono(entity.getTelefono())
                .fechaNacimiento(entity.getFechaNacimiento() != null ?
                        entity.getFechaNacimiento().format(DATE_FORMATTER) : null)
                .direccion(entity.getDireccion())
                .fechaCreacion(entity.getFechaCreacion() != null ?
                        entity.getFechaCreacion().format(DATETIME_FORMATTER) : null)
                .fechaActualizacion(entity.getFechaActualizacion() != null ?
                        entity.getFechaActualizacion().format(DATETIME_FORMATTER) : null)
                .build();
    }

    /**
     * Actualiza una entidad Usuario existente con datos del DTO
     * NO actualiza la contraseña (se maneja por separado)
     */
    public void updateEntity(UsuarioRequestDTO dto, Usuario entity) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setNombreUsuario(dto.getNombreUsuario());
        entity.setEmail(dto.getEmail());
        entity.setRol(dto.getRol());
        entity.setNombre(dto.getNombre());
        entity.setApellido(dto.getApellido());
        entity.setCedula(dto.getCedula());
        entity.setTelefono(dto.getTelefono());
        entity.setFechaNacimiento(dto.getFechaNacimiento());
        entity.setDireccion(dto.getDireccion());
        // NO actualizar contraseña aquí
    }

    /**
     * Formatea el rol para mostrar
     */
    private String formatRol(com.sistema.academico.dominio.enumeracion.Rol rol) {
        if (rol == null) {
            return null;
        }

        switch (rol) {
            case SUPER_ADMIN:
                return "SUPER_ADMIN";
            case ADMIN:
                return "Administrador";
            case PROFESOR:
                return "Profesor";
            case ESTUDIANTE:
                return "Estudiante";
            default:
                return rol.name();
        }
    }

    /**
     * Formatea el estado para mostrar
     */
    private String formatEstado(com.sistema.academico.dominio.enumeracion.Estado estado) {
        if (estado == null) {
            return null;
        }

        return estado == com.sistema.academico.dominio.enumeracion.Estado.ACTIVO ?
                "Activo" : "Inactivo";
    }
}