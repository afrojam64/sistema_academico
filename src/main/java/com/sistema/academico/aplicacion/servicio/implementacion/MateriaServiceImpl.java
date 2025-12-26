package com.sistema.academico.aplicacion.servicio.implementacion;

import com.sistema.academico.aplicacion.dto.request.MateriaRequestDTO;
import com.sistema.academico.aplicacion.dto.response.MateriaResponseDTO;
import com.sistema.academico.aplicacion.mapper.MateriaMapper;
import com.sistema.academico.aplicacion.servicio.IMateriaService;
import com.sistema.academico.dominio.entidad.Materia;
import com.sistema.academico.dominio.entidad.Profesor;
import com.sistema.academico.dominio.enumeracion.Estado;
import com.sistema.academico.dominio.enumeracion.Rol;
import com.sistema.academico.infraestructura.excepcion.DuplicadoException;
import com.sistema.academico.infraestructura.excepcion.PermisosDenegadosException;
import com.sistema.academico.infraestructura.excepcion.RecursoNoEncontradoException;
import com.sistema.academico.infraestructura.repositorio.MateriaRepository;
import com.sistema.academico.infraestructura.repositorio.ProfesorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MateriaServiceImpl implements IMateriaService {

    private final MateriaRepository materiaRepository;
    private final ProfesorRepository profesorRepository;
    private final MateriaMapper materiaMapper;

    @Override
    @Transactional
    public MateriaResponseDTO crear(MateriaRequestDTO request) {
        // Validar que el código no exista
        if (materiaRepository.existsByCodigo(request.getCodigo())) {
            throw new DuplicadoException("El código de materia ya existe");
        }

        // Validar que el profesor existe
        Profesor profesor = profesorRepository.findById(request.getProfesorId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Profesor no encontrado"));

        Materia materia = materiaMapper.toEntity(request, profesor);
        Materia guardada = materiaRepository.save(materia);

        return materiaMapper.toResponseDTO(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public MateriaResponseDTO obtenerPorId(Long id) {
        Materia materia = materiaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Materia no encontrada con ID: " + id));

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
        return materiaRepository.findActivas().stream()
                .map(materiaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MateriaResponseDTO actualizar(Long id, MateriaRequestDTO request) {
        Materia materia = materiaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Materia no encontrada con ID: " + id));

        // Validar código si cambió
        if (request.getCodigo() != null && !request.getCodigo().equals(materia.getCodigo())) {
            if (materiaRepository.existsByCodigo(request.getCodigo())) {
                throw new DuplicadoException("El código de materia ya existe");
            }
        }

        // Obtener profesor si cambió
        Profesor profesor = null;
        if (request.getProfesorId() != null) {
            profesor = profesorRepository.findById(request.getProfesorId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Profesor no encontrado"));
        }

        materiaMapper.updateEntityFromDTO(materia, request, profesor);
        Materia actualizada = materiaRepository.save(materia);

        return materiaMapper.toResponseDTO(actualizada);
    }

    @Override
    @Transactional
    public void desactivar(Long id, Rol rolUsuarioActual) {
        if (!rolUsuarioActual.puedeDesactivar()) {
            throw new PermisosDenegadosException("No tiene permisos para desactivar materias");
        }

        Materia materia = materiaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Materia no encontrada con ID: " + id));

        materia.setEstado(Estado.INACTIVO);
        materiaRepository.save(materia);
    }

    @Override
    @Transactional
    public void activar(Long id, Rol rolUsuarioActual) {
        if (!rolUsuarioActual.puedeDesactivar()) {
            throw new PermisosDenegadosException("No tiene permisos para activar materias");
        }

        Materia materia = materiaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Materia no encontrada con ID: " + id));

        materia.setEstado(Estado.ACTIVO);
        materiaRepository.save(materia);
    }

    @Override
    @Transactional
    public void eliminar(Long id, Rol rolUsuarioActual) {
        if (!rolUsuarioActual.puedeEliminarFisicamente()) {
            throw new PermisosDenegadosException("Solo SUPER_ADMIN puede eliminar materias físicamente");
        }

        Materia materia = materiaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Materia no encontrada con ID: " + id));

        materiaRepository.delete(materia);
    }
}