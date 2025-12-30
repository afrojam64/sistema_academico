package com.sistema.academico.aplicacion.servicio.implementacion;

import com.sistema.academico.aplicacion.dto.request.MateriaRequestDTO;
import com.sistema.academico.aplicacion.dto.response.MateriaResponseDTO;
import com.sistema.academico.aplicacion.mapper.MateriaMapper;
import com.sistema.academico.aplicacion.servicio.IMateriaService;
import com.sistema.academico.dominio.entidad.Departamento;
import com.sistema.academico.dominio.entidad.Materia;
import com.sistema.academico.dominio.enumeracion.Estado;
import com.sistema.academico.dominio.enumeracion.Rol;
import com.sistema.academico.infraestructura.excepcion.RecursoNoEncontradoException;
import com.sistema.academico.infraestructura.excepcion.OperacionNoPermitidaException;
import com.sistema.academico.infraestructura.repositorio.DepartamentoRepository;
import com.sistema.academico.infraestructura.repositorio.MateriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MateriaServiceImpl implements IMateriaService {

    private final MateriaRepository materiaRepository;
    private final DepartamentoRepository departamentoRepository;
    private final MateriaMapper materiaMapper;

    @Override
    @Transactional
    public MateriaResponseDTO crear(MateriaRequestDTO request) {
        // Validar que el código no exista
        if (materiaRepository.existsByCodigo(request.getCodigo())) {
            throw new IllegalArgumentException("Ya existe una materia con el código: " + request.getCodigo());
        }

        // Buscar el departamento
        Departamento departamento = departamentoRepository.findById(request.getDepartamentoId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el departamento con ID: " + request.getDepartamentoId()));

        // Validar que el departamento esté activo
        if (!departamento.estaActivo()) {
            throw new OperacionNoPermitidaException("El departamento debe estar activo para crear materias");
        }

        // Crear la materia
        Materia materia = materiaMapper.toEntity(request, departamento);
        Materia materiaGuardada = materiaRepository.save(materia);

        return materiaMapper.toResponseDTO(materiaGuardada);
    }

    @Override
    @Transactional(readOnly = true)
    public MateriaResponseDTO obtenerPorId(Long id) {
        Materia materia = materiaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la materia con ID: " + id));
        return materiaMapper.toResponseDTO(materia);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MateriaResponseDTO> listarTodas() {
        return materiaRepository.findAll().stream()
                .map(materiaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MateriaResponseDTO> listarActivas() {
        return materiaRepository.findByEstado(Estado.ACTIVO).stream()
                .map(materiaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MateriaResponseDTO actualizar(Long id, MateriaRequestDTO request) {
        Materia materia = materiaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la materia con ID: " + id));

        // Validar código único si cambió
        if (!materia.getCodigo().equals(request.getCodigo()) &&
                materiaRepository.existsByCodigo(request.getCodigo())) {
            throw new IllegalArgumentException("Ya existe una materia con el código: " + request.getCodigo());
        }

        // Buscar departamento si cambió
        Departamento departamento = null;
        if (!materia.getDepartamento().getId().equals(request.getDepartamentoId())) {
            departamento = departamentoRepository.findById(request.getDepartamentoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "No se encontró el departamento con ID: " + request.getDepartamentoId()));

            if (!departamento.estaActivo()) {
                throw new OperacionNoPermitidaException("El departamento debe estar activo");
            }
        }

        // Actualizar
        materiaMapper.updateEntityFromDTO(materia, request, departamento);
        Materia materiaActualizada = materiaRepository.save(materia);

        return materiaMapper.toResponseDTO(materiaActualizada);
    }

    @Override
    @Transactional
    public void desactivar(Long id, Rol rolUsuario) {
        validarPermisos(rolUsuario);

        Materia materia = materiaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la materia con ID: " + id));

        if (!materia.estaActiva()) {
            throw new OperacionNoPermitidaException("La materia ya está inactiva");
        }

        materia.setEstado(Estado.INACTIVO);
        materiaRepository.save(materia);
    }

    @Override
    @Transactional
    public void activar(Long id, Rol rolUsuario) {
        validarPermisos(rolUsuario);

        Materia materia = materiaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la materia con ID: " + id));

        if (materia.estaActiva()) {
            throw new OperacionNoPermitidaException("La materia ya está activa");
        }

        materia.setEstado(Estado.ACTIVO);
        materiaRepository.save(materia);
    }

    @Override
    @Transactional
    public void eliminar(Long id, Rol rolUsuario) {
        if (rolUsuario != Rol.SUPER_ADMIN) {
            throw new OperacionNoPermitidaException("Solo SUPER_ADMIN puede eliminar materias");
        }

        Materia materia = materiaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la materia con ID: " + id));

        // Aquí podrías validar si tiene cursos asociados antes de eliminar
        materiaRepository.delete(materia);
    }

    private void validarPermisos(Rol rolUsuario) {
        if (rolUsuario != Rol.SUPER_ADMIN && rolUsuario != Rol.ADMIN) {
            throw new OperacionNoPermitidaException(
                    "No tienes permisos para realizar esta operación. Requiere rol ADMIN o SUPER_ADMIN");
        }
    }
}