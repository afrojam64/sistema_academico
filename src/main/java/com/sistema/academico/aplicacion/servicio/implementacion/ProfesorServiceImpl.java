package com.sistema.academico.aplicacion.servicio.implementacion;

import com.sistema.academico.aplicacion.dto.request.ProfesorRequestDTO;
import com.sistema.academico.aplicacion.dto.response.ProfesorResponseDTO;
import com.sistema.academico.aplicacion.mapper.ProfesorMapper;
import com.sistema.academico.aplicacion.servicio.IProfesorService;
import com.sistema.academico.dominio.entidad.Departamento;
import com.sistema.academico.dominio.entidad.Profesor;
import com.sistema.academico.dominio.entidad.Usuario;
import com.sistema.academico.dominio.enumeracion.Estado;
import com.sistema.academico.dominio.enumeracion.Rol;
import com.sistema.academico.infraestructura.excepcion.DuplicadoException;
import com.sistema.academico.infraestructura.excepcion.PermisosDenegadosException;
import com.sistema.academico.infraestructura.excepcion.RecursoNoEncontradoException;
import com.sistema.academico.infraestructura.repositorio.DepartamentoRepository;
import com.sistema.academico.infraestructura.repositorio.ProfesorRepository;
import com.sistema.academico.infraestructura.repositorio.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfesorServiceImpl implements IProfesorService {

    private final ProfesorRepository profesorRepository;
    private final UsuarioRepository usuarioRepository;
    private final DepartamentoRepository departamentoRepository;
    private final ProfesorMapper profesorMapper;

    @Override
    @Transactional
    public ProfesorResponseDTO crear(ProfesorRequestDTO request) {
        // Validar que el usuario existe
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        // Validar que el departamento existe
        Departamento departamento = departamentoRepository.findById(request.getDepartamentoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Departamento no encontrado"));

        // Validar que el email no exista
        if (profesorRepository.existsByEmail(request.getEmail())) {
            throw new DuplicadoException("El email ya está registrado");
        }

        Profesor profesor = profesorMapper.toEntity(request, usuario, departamento);
        Profesor guardado = profesorRepository.save(profesor);

        return profesorMapper.toResponseDTO(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfesorResponseDTO obtenerPorId(Long id) {
        Profesor profesor = profesorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Profesor no encontrado con ID: " + id));

        return profesorMapper.toResponseDTO(profesor);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfesorResponseDTO> listarTodos() {
        return profesorRepository.findAll().stream()
                .map(profesorMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfesorResponseDTO> listarActivos() {
        return profesorRepository.findByEstado(Estado.ACTIVO).stream()
                .map(profesorMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProfesorResponseDTO actualizar(Long id, ProfesorRequestDTO request) {
        Profesor profesor = profesorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Profesor no encontrado con ID: " + id));

        // Validar email si cambió
        if (request.getEmail() != null && !request.getEmail().equals(profesor.getEmail())) {
            if (profesorRepository.existsByEmail(request.getEmail())) {
                throw new DuplicadoException("El email ya está registrado");
            }
        }

        // Obtener departamento si cambió
        Departamento departamento = null;
        if (request.getDepartamentoId() != null) {
            departamento = departamentoRepository.findById(request.getDepartamentoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Departamento no encontrado"));
        }

        profesorMapper.updateEntityFromDTO(profesor, request, departamento);
        Profesor actualizado = profesorRepository.save(profesor);

        return profesorMapper.toResponseDTO(actualizado);
    }

    @Override
    @Transactional
    public void desactivar(Long id, Rol rolUsuarioActual) {
        if (!rolUsuarioActual.puedeDesactivar()) {
            throw new PermisosDenegadosException("No tiene permisos para desactivar profesores");
        }

        Profesor profesor = profesorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Profesor no encontrado con ID: " + id));

        profesor.setEstado(Estado.INACTIVO);
        profesorRepository.save(profesor);
    }

    @Override
    @Transactional
    public void activar(Long id, Rol rolUsuarioActual) {
        if (!rolUsuarioActual.puedeDesactivar()) {
            throw new PermisosDenegadosException("No tiene permisos para activar profesores");
        }

        Profesor profesor = profesorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Profesor no encontrado con ID: " + id));

        profesor.setEstado(Estado.ACTIVO);
        profesorRepository.save(profesor);
    }

    @Override
    @Transactional
    public void eliminar(Long id, Rol rolUsuarioActual) {
        if (!rolUsuarioActual.puedeEliminarFisicamente()) {
            throw new PermisosDenegadosException("Solo SUPER_ADMIN puede eliminar profesores físicamente");
        }

        Profesor profesor = profesorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Profesor no encontrado con ID: " + id));

        profesorRepository.delete(profesor);
    }
}