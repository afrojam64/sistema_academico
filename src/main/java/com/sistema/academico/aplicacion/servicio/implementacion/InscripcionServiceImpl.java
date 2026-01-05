package com.sistema.academico.aplicacion.servicio.implementacion;

import com.sistema.academico.aplicacion.dto.request.InscripcionRequestDTO;
import com.sistema.academico.aplicacion.dto.response.InscripcionResponseDTO;
import com.sistema.academico.aplicacion.mapper.InscripcionMapper;
import com.sistema.academico.aplicacion.servicio.IInscripcionService;
import com.sistema.academico.dominio.entidad.Curso;
import com.sistema.academico.dominio.entidad.Estudiante;
import com.sistema.academico.dominio.entidad.Inscripcion;
import com.sistema.academico.dominio.entidad.Profesor;
import com.sistema.academico.dominio.enumeracion.EstadoInscripcion;
import com.sistema.academico.dominio.enumeracion.Rol;
import com.sistema.academico.infraestructura.excepcion.RecursoDuplicadoException;
import com.sistema.academico.infraestructura.excepcion.OperacionNoPermitidaException;
import com.sistema.academico.infraestructura.excepcion.RecursoNoEncontradoException;
import com.sistema.academico.infraestructura.excepcion.ValidacionNegocioException;
import com.sistema.academico.infraestructura.repositorio.CursoRepository;
import com.sistema.academico.infraestructura.repositorio.EstudianteRepository;
import com.sistema.academico.infraestructura.repositorio.InscripcionRepository;
import com.sistema.academico.infraestructura.repositorio.ProfesorRepository;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InscripcionServiceImpl implements IInscripcionService {

    private final InscripcionRepository inscripcionRepository;
    private final EstudianteRepository estudianteRepository;
    private final CursoRepository cursoRepository;
    private final InscripcionMapper inscripcionMapper;
    private final ProfesorRepository profesorRepository;

    @Override
    @Transactional
    public InscripcionResponseDTO crear(InscripcionRequestDTO request) {
        // Validar que el estudiante existe
        Estudiante estudiante = estudianteRepository.findById(request.getEstudianteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado"));

        // Validar que el curso existe
        Curso curso = cursoRepository.findById(request.getCursoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Curso no encontrado"));

        // Validar que el estudiante no esté ya inscrito
        if (inscripcionRepository.existsByEstudianteAndCurso(estudiante, curso)) {
            throw new RecursoDuplicadoException("El estudiante ya está inscrito en este curso");
        }

        // Validar que el curso tenga cupos disponibles
        if (!curso.tieneCuposDisponibles()) {
            throw new ValidacionNegocioException("El curso no tiene cupos disponibles");
        }

        Inscripcion inscripcion = inscripcionMapper.toEntity(request, estudiante, curso);
        Inscripcion guardada = inscripcionRepository.save(inscripcion);

        // Incrementar cupo actual del curso
        curso.incrementarCupo();
        cursoRepository.save(curso);

        return inscripcionMapper.toResponseDTO(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public InscripcionResponseDTO obtenerPorId(Long id) {
        Inscripcion inscripcion = inscripcionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Inscripción no encontrada con ID: " + id));

        return inscripcionMapper.toResponseDTO(inscripcion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InscripcionResponseDTO> listarTodas() {
        return inscripcionRepository.findAll().stream()
                .map(inscripcionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InscripcionResponseDTO> listarActivas() {
        return inscripcionRepository.findByEstado(EstadoInscripcion.ACTIVO).stream()
                .map(inscripcionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void retirar(Long id) {
        Inscripcion inscripcion = inscripcionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Inscripción no encontrada con ID: " + id));

        if (!inscripcion.puedeRetirar()) {
            throw new ValidacionNegocioException("Esta inscripción no puede ser retirada");
        }

        inscripcionMapper.updateEstado(inscripcion, EstadoInscripcion.RETIRADO);
        inscripcionRepository.save(inscripcion);

        // Decrementar cupo actual del curso
        Curso curso = inscripcion.getCurso();
        curso.decrementarCupo();
        cursoRepository.save(curso);
    }

    @Override
    @Transactional
    public void completar(Long id, Rol rolUsuarioActual) {
        if (!rolUsuarioActual.puedeDesactivar()) {
            throw new OperacionNoPermitidaException("No tiene permisos para completar inscripciones");
        }

        Inscripcion inscripcion = inscripcionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Inscripción no encontrada con ID: " + id));

        inscripcionMapper.updateEstado(inscripcion, EstadoInscripcion.COMPLETADO);
        inscripcionRepository.save(inscripcion);
    }

    @Override
    @Transactional
    public void eliminar(Long id, Rol rolUsuarioActual) {
        if (!rolUsuarioActual.puedeEliminarFisicamente()) {
            throw new OperacionNoPermitidaException("Solo SUPER_ADMIN puede eliminar inscripciones físicamente");
        }

        Inscripcion inscripcion = inscripcionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Inscripción no encontrada con ID: " + id));

        // Decrementar cupo si está activa
        if (inscripcion.estaActiva()) {
            Curso curso = inscripcion.getCurso();
            curso.decrementarCupo();
            cursoRepository.save(curso);
        }

        inscripcionRepository.delete(inscripcion);


    }

    // ========================================
    // MÉTODOS PARA DASHBOARDS
    // ========================================

    @Override
    @Transactional(readOnly = true)
    public List<InscripcionResponseDTO> listarPorEstudiante(Long estudianteId) {
        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado con ID: " + estudianteId));

        return inscripcionRepository.findByEstudiante(estudiante).stream()
                .map(inscripcionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InscripcionResponseDTO> listarActivasPorEstudiante(Long estudianteId) {
        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado con ID: " + estudianteId));

        return inscripcionRepository.findByEstudianteAndEstado(estudiante, EstadoInscripcion.ACTIVO).stream()
                .map(inscripcionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InscripcionResponseDTO> listarPorCurso(Long cursoId) {
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Curso no encontrado con ID: " + cursoId));

        return inscripcionRepository.findByCurso(curso).stream()
                .map(inscripcionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Listar inscripciones activas de todos los cursos de un profesor
     * @param profesorId ID del profesor
     * @return Lista de inscripciones activas de los cursos del profesor
     */
    @Override
    @Transactional(readOnly = true)
    public List<InscripcionResponseDTO> listarActivasPorProfesor(Long profesorId) {
        // Validar que el profesor existe
        Profesor profesor = profesorRepository.findById(profesorId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Profesor no encontrado con ID: " + profesorId));

        // Obtener todos los cursos del profesor
        List<Curso> cursos = cursoRepository.findByProfesor(profesor);

        // Obtener todas las inscripciones activas de esos cursos
        List<Inscripcion> inscripciones = new ArrayList<>();
        for (Curso curso : cursos) {
            List<Inscripcion> inscripcionesCurso = inscripcionRepository.findByCurso(curso);
            // Filtrar solo las activas
            inscripcionesCurso.stream()
                    .filter(i -> i.getEstado() == EstadoInscripcion.ACTIVO)
                    .forEach(inscripciones::add);
        }

        // Mapear a DTOs
        return inscripciones.stream()
                .map(inscripcionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}