package com.sistema.academico.aplicacion.servicio.implementacion;

import com.sistema.academico.aplicacion.dto.request.EstudianteRequestDTO;
import com.sistema.academico.aplicacion.dto.response.EstudianteResponseDTO;
import com.sistema.academico.aplicacion.mapper.EstudianteMapper;
import com.sistema.academico.aplicacion.servicio.IEstudianteService;
import com.sistema.academico.dominio.entidad.Estudiante;
import com.sistema.academico.dominio.entidad.Usuario;
import com.sistema.academico.dominio.enumeracion.Estado;
import com.sistema.academico.dominio.enumeracion.Rol;
import com.sistema.academico.infraestructura.excepcion.RecursoDuplicadoException;
import com.sistema.academico.infraestructura.excepcion.OperacionNoPermitidaException;
import com.sistema.academico.infraestructura.excepcion.RecursoNoEncontradoException;
import com.sistema.academico.infraestructura.repositorio.EstudianteRepository;
import com.sistema.academico.infraestructura.repositorio.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EstudianteServiceImpl implements IEstudianteService {

    private final EstudianteRepository estudianteRepository;
    private final UsuarioRepository usuarioRepository;
    private final EstudianteMapper estudianteMapper;

    @Override
    @Transactional
    public EstudianteResponseDTO crear(EstudianteRequestDTO request) {
        // Validar que el usuario existe
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        // Validar que el email no exista
        if (estudianteRepository.existsByEmail(request.getEmail())) {
            throw new RecursoDuplicadoException("El email ya está registrado");
        }

        // Validar que la matrícula no exista
        if (estudianteRepository.existsByMatricula(request.getMatricula())) {
            throw new RecursoDuplicadoException("La matrícula ya está registrada");
        }

        Estudiante estudiante = estudianteMapper.toEntity(request, usuario);
        Estudiante guardado = estudianteRepository.save(estudiante);

        return estudianteMapper.toResponseDTO(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public EstudianteResponseDTO obtenerPorId(Long id) {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado con ID: " + id));

        return estudianteMapper.toResponseDTO(estudiante);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EstudianteResponseDTO> listarTodos() {
        return estudianteRepository.findAll().stream()
                .map(estudianteMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EstudianteResponseDTO> listarActivos() {
        return estudianteRepository.findActivos().stream()
                .map(estudianteMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EstudianteResponseDTO actualizar(Long id, EstudianteRequestDTO request) {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado con ID: " + id));

        // Validar email si cambió
        if (request.getEmail() != null && !request.getEmail().equals(estudiante.getEmail())) {
            if (estudianteRepository.existsByEmail(request.getEmail())) {
                throw new RecursoDuplicadoException("El email ya está registrado");
            }
        }

        // Validar matrícula si cambió
        if (request.getMatricula() != null && !request.getMatricula().equals(estudiante.getMatricula())) {
            if (estudianteRepository.existsByMatricula(request.getMatricula())) {
                throw new RecursoDuplicadoException("La matrícula ya está registrada");
            }
        }

        estudianteMapper.updateEntityFromDTO(estudiante, request);
        Estudiante actualizado = estudianteRepository.save(estudiante);

        return estudianteMapper.toResponseDTO(actualizado);
    }

    @Override
    @Transactional
    public void desactivar(Long id, Rol rolUsuarioActual) {
        if (!rolUsuarioActual.puedeDesactivar()) {
            throw new OperacionNoPermitidaException("No tiene permisos para desactivar estudiantes");
        }

        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado con ID: " + id));

        estudiante.setEstado(Estado.INACTIVO);
        estudianteRepository.save(estudiante);
    }

    @Override
    @Transactional
    public void activar(Long id, Rol rolUsuarioActual) {
        if (!rolUsuarioActual.puedeDesactivar()) {
            throw new OperacionNoPermitidaException("No tiene permisos para activar estudiantes");
        }

        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado con ID: " + id));

        estudiante.setEstado(Estado.ACTIVO);
        estudianteRepository.save(estudiante);
    }

    @Override
    @Transactional
    public void eliminar(Long id, Rol rolUsuarioActual) {
        if (!rolUsuarioActual.puedeEliminarFisicamente()) {
            throw new OperacionNoPermitidaException("Solo SUPER_ADMIN puede eliminar estudiantes físicamente");
        }

        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado con ID: " + id));

        estudianteRepository.delete(estudiante);
    }
}