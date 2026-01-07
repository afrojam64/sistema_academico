package com.sistema.academico.aplicacion.servicio.implementacion;

import com.sistema.academico.aplicacion.dto.request.CursoRequestDTO;
import com.sistema.academico.aplicacion.dto.response.CursoResponseDTO;
import com.sistema.academico.aplicacion.mapper.CursoMapper;
import com.sistema.academico.aplicacion.servicio.ICursoService;
import com.sistema.academico.dominio.entidad.Curso;
import com.sistema.academico.dominio.entidad.Materia;
import com.sistema.academico.dominio.entidad.Profesor;
import com.sistema.academico.dominio.enumeracion.Estado;
import com.sistema.academico.dominio.enumeracion.Rol;
import com.sistema.academico.infraestructura.excepcion.RecursoDuplicadoException;
import com.sistema.academico.infraestructura.excepcion.OperacionNoPermitidaException;
import com.sistema.academico.infraestructura.excepcion.RecursoNoEncontradoException;
import com.sistema.academico.infraestructura.repositorio.CursoRepository;
import com.sistema.academico.infraestructura.repositorio.MateriaRepository;
import com.sistema.academico.infraestructura.repositorio.ProfesorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CursoServiceImpl implements ICursoService {

    private final CursoRepository cursoRepository;
    private final MateriaRepository materiaRepository;
    private final ProfesorRepository profesorRepository;
    private final CursoMapper cursoMapper;

    @Override
    @Transactional
    public CursoResponseDTO crear(CursoRequestDTO request) {
        // Validar que el código no exista
        if (cursoRepository.existsByCodigo(request.getCodigo())) {
            throw new RecursoDuplicadoException("El código de curso ya existe");
        }

        // Validar que la materia existe
        Materia materia = materiaRepository.findById(request.getMateriaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Materia no encontrada"));

        // Validar que el profesor existe
        Profesor profesor = profesorRepository.findById(request.getProfesorId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Profesor no encontrado"));

        Curso curso = cursoMapper.toEntity(request, materia, profesor);
        Curso guardado = cursoRepository.save(curso);

        return cursoMapper.toResponseDTO(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public CursoResponseDTO obtenerPorId(Long id) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Curso no encontrado con ID: " + id));

        return cursoMapper.toResponseDTO(curso);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CursoResponseDTO> listarTodos() {
        return cursoRepository.findAll().stream()
                .map(cursoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CursoResponseDTO> listarActivos() {
        return cursoRepository.findActivos().stream()
                .map(cursoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CursoResponseDTO> listarCursosConCupos() {
        return cursoRepository.findCursosConCuposDisponibles().stream()
                .map(cursoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CursoResponseDTO actualizar(Long id, CursoRequestDTO request) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Curso no encontrado con ID: " + id));

        // Validar código si cambió
        if (request.getCodigo() != null && !request.getCodigo().equals(curso.getCodigo())) {
            if (cursoRepository.existsByCodigo(request.getCodigo())) {
                throw new RecursoDuplicadoException("El código de curso ya existe");
            }
        }

        // Obtener materia si cambió
        Materia materia = null;
        if (request.getMateriaId() != null) {
            materia = materiaRepository.findById(request.getMateriaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Materia no encontrada"));
        }

        // Obtener profesor si cambió
        Profesor profesor = null;
        if (request.getProfesorId() != null) {
            profesor = profesorRepository.findById(request.getProfesorId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Profesor no encontrado"));
        }

        cursoMapper.updateEntityFromDTO(curso, request, materia, profesor);
        Curso actualizado = cursoRepository.save(curso);

        return cursoMapper.toResponseDTO(actualizado);
    }

    @Override
    @Transactional
    public void desactivar(Long id, Rol rolUsuarioActual) {
        if (!rolUsuarioActual.puedeDesactivar()) {
            throw new OperacionNoPermitidaException("No tiene permisos para desactivar cursos");
        }

        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Curso no encontrado con ID: " + id));

        curso.setEstado(Estado.INACTIVO);
        cursoRepository.save(curso);
    }

    @Override
    @Transactional
    public void activar(Long id, Rol rolUsuarioActual) {
        if (!rolUsuarioActual.puedeDesactivar()) {
            throw new OperacionNoPermitidaException("No tiene permisos para activar cursos");
        }

        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Curso no encontrado con ID: " + id));

        curso.setEstado(Estado.ACTIVO);
        cursoRepository.save(curso);
    }

    @Override
    @Transactional
    public void eliminar(Long id, Rol rolUsuarioActual) {
        if (!rolUsuarioActual.puedeEliminarFisicamente()) {
            throw new OperacionNoPermitidaException("Solo SUPER_ADMIN puede eliminar cursos físicamente");
        }

        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Curso no encontrado con ID: " + id));

        cursoRepository.delete(curso);
    }

    // ========================================
    // MÉTODOS PARA DASHBOARD PROFESOR
    // ========================================

    @Override
    @Transactional(readOnly = true)
    public List<CursoResponseDTO> listarCursosPorProfesor(Long profesorId) {
        Profesor profesor = profesorRepository.findById(profesorId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Profesor no encontrado con ID: " + profesorId));

        return cursoRepository.findByProfesor(profesor).stream()
                .map(cursoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CursoResponseDTO> listarCursosActivosPorProfesor(Long profesorId) {
        Profesor profesor = profesorRepository.findById(profesorId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Profesor no encontrado con ID: " + profesorId));

        return cursoRepository.findByProfesorAndEstado(profesor, Estado.ACTIVO).stream()
                .map(cursoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar cursos por código, nombre de materia o periodo
     */
    @Override
    @Transactional(readOnly = true)
    public List<CursoResponseDTO> buscarPorTermino(String termino) {
        // Convertir término a minúsculas para búsqueda insensible a mayúsculas
        String terminoBusqueda = termino.toLowerCase().trim();

        // Obtener todos los cursos activos
        List<Curso> todosCursos = cursoRepository.findAll();

        // Filtrar por código, nombre de materia o periodo
        List<Curso> resultados = todosCursos.stream()
                .filter(curso -> curso.getEstado() == Estado.ACTIVO)
                .filter(curso -> {
                    String codigo = curso.getCodigo().toLowerCase();
                    String nombreMateria = curso.getMateria().getNombre().toLowerCase();
                    String periodo = curso.getPeriodo().toLowerCase();
                    String nombreProfesor = (curso.getProfesor().getUsuario().getNombre() + " " +
                            curso.getProfesor().getUsuario().getApellido()).toLowerCase();

                    return codigo.contains(terminoBusqueda) ||
                            nombreMateria.contains(terminoBusqueda) ||
                            periodo.contains(terminoBusqueda) ||
                            nombreProfesor.contains(terminoBusqueda);
                })
                .collect(Collectors.toList());

        // Mapear a DTOs
        return resultados.stream()
                .map(cursoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}