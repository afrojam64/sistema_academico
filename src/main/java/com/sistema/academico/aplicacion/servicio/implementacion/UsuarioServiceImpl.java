package com.sistema.academico.aplicacion.servicio.implementacion;

import com.sistema.academico.aplicacion.dto.request.UsuarioRequestDTO;
import com.sistema.academico.aplicacion.dto.response.UsuarioResponseDTO;
import com.sistema.academico.aplicacion.mapper.UsuarioMapper;
import com.sistema.academico.aplicacion.servicio.IUsuarioService;
import com.sistema.academico.dominio.entidad.Usuario;
import com.sistema.academico.dominio.enumeracion.Estado;
import com.sistema.academico.dominio.enumeracion.Rol;
import com.sistema.academico.infraestructura.excepcion.DuplicadoException;
import com.sistema.academico.infraestructura.excepcion.PermisosDenegadosException;
import com.sistema.academico.infraestructura.excepcion.RecursoNoEncontradoException;
import com.sistema.academico.infraestructura.repositorio.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements IUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    @Override
    @Transactional
    public UsuarioResponseDTO crear(UsuarioRequestDTO request) {
        // Validar que el nombre de usuario no exista
        if (usuarioRepository.existsByNombreUsuario(request.getNombreUsuario())) {
            throw new DuplicadoException("El nombre de usuario ya existe");
        }

        // Convertir DTO a Entity
        Usuario usuario = usuarioMapper.toEntity(request);

        // Guardar
        Usuario guardado = usuarioRepository.save(usuario);

        // Convertir Entity a DTO Response
        return usuarioMapper.toResponseDTO(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id));

        return usuarioMapper.toResponseDTO(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarActivos() {
        return usuarioRepository.findActivos().stream()
                .map(usuarioMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UsuarioResponseDTO actualizar(Long id, UsuarioRequestDTO request) {
        // Buscar usuario
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id));

        // Validar nombre de usuario si cambió
        if (request.getNombreUsuario() != null &&
                !request.getNombreUsuario().equals(usuario.getNombreUsuario())) {
            if (usuarioRepository.existsByNombreUsuario(request.getNombreUsuario())) {
                throw new DuplicadoException("El nombre de usuario ya existe");
            }
        }

        // Actualizar campos
        usuarioMapper.updateEntityFromDTO(usuario, request);

        // Guardar
        Usuario actualizado = usuarioRepository.save(usuario);

        return usuarioMapper.toResponseDTO(actualizado);
    }

    @Override
    @Transactional
    public void desactivar(Long id, Rol rolUsuarioActual) {
        // Validar permisos
        if (!rolUsuarioActual.puedeDesactivar()) {
            throw new PermisosDenegadosException("No tiene permisos para desactivar usuarios");
        }

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id));

        usuario.setEstado(Estado.INACTIVO);
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void activar(Long id, Rol rolUsuarioActual) {
        // Validar permisos
        if (!rolUsuarioActual.puedeDesactivar()) {
            throw new PermisosDenegadosException("No tiene permisos para activar usuarios");
        }

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id));

        usuario.setEstado(Estado.ACTIVO);
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void eliminar(Long id, Rol rolUsuarioActual) {
        // Validar que sea SUPER_ADMIN
        if (!rolUsuarioActual.puedeEliminarFisicamente()) {
            throw new PermisosDenegadosException("Solo SUPER_ADMIN puede eliminar usuarios físicamente");
        }

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id));

        usuarioRepository.delete(usuario);
    }
}