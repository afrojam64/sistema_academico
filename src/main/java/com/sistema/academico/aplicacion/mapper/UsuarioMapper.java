package com.sistema.academico.aplicacion.mapper;

import com.sistema.academico.aplicacion.dto.request.UsuarioRequestDTO;
import com.sistema.academico.aplicacion.dto.response.UsuarioResponseDTO;
import com.sistema.academico.dominio.entidad.Usuario;
import com.sistema.academico.dominio.enumeracion.Estado;
import com.sistema.academico.dominio.enumeracion.Rol;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class UsuarioMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final PasswordEncoder passwordEncoder;

    public UsuarioMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Convierte UsuarioRequestDTO a Entidad Usuario
     * @param dto DTO con datos de entrada
     * @return Entidad Usuario
     */
    public Usuario toEntity(UsuarioRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        return Usuario.builder()
                .nombreUsuario(dto.getNombreUsuario())
                .contrasena(passwordEncoder.encode(dto.getContrasena()))
                .email(dto.getEmail())  // ← MAPEO DE EMAIL AGREGADO
                .rol(dto.getRol())
                .estado(Estado.ACTIVO)
                .fechaCreacion(LocalDateTime.now())
                .build();
    }

    /**
     * Convierte Entidad Usuario a UsuarioResponseDTO
     * @param usuario Entidad Usuario
     * @return DTO de respuesta
     */
    public UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        return UsuarioResponseDTO.builder()
                .id(usuario.getId())
                .nombreUsuario(usuario.getNombreUsuario())
                .email(usuario.getEmail())  // ← MAPEO DE EMAIL AGREGADO
                .rol(transformarRol(usuario.getRol()))
                .estado(transformarEstado(usuario.getEstado()))
                .fechaCreacion(usuario.getFechaCreacion().format(FORMATTER))
                .build();
    }

    /**
     * Actualiza una entidad Usuario existente con datos del DTO
     * @param usuario Entidad existente
     * @param dto DTO con datos nuevos
     */
    public void updateEntityFromDTO(Usuario usuario, UsuarioRequestDTO dto) {
        if (dto.getNombreUsuario() != null) {
            usuario.setNombreUsuario(dto.getNombreUsuario());
        }
        if (dto.getContrasena() != null && !dto.getContrasena().isEmpty()) {
            usuario.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        }
        if (dto.getEmail() != null) {  // ← ACTUALIZACIÓN DE EMAIL AGREGADA
            usuario.setEmail(dto.getEmail());
        }
        if (dto.getRol() != null) {
            usuario.setRol(dto.getRol());
        }
    }

    /**
     * Transforma el enum Rol a texto legible
     * @param rol Enum Rol
     * @return Texto legible
     */
    private String transformarRol(Rol rol) {
        switch (rol) {
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
     * Transforma el enum Estado a texto legible
     * @param estado Enum Estado
     * @return Texto legible
     */
    private String transformarEstado(Estado estado) {
        return estado == Estado.ACTIVO ? "Activo" : "Inactivo";
    }
}