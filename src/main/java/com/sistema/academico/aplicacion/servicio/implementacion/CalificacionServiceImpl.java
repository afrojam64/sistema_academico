package com.sistema.academico.aplicacion.servicio.implementacion;

import com.sistema.academico.aplicacion.dto.request.CalificacionRequestDTO;
import com.sistema.academico.aplicacion.dto.response.CalificacionResponseDTO;
import com.sistema.academico.aplicacion.mapper.CalificacionMapper;
import com.sistema.academico.aplicacion.servicio.ICalificacionService;
import com.sistema.academico.dominio.entidad.Calificacion;
import com.sistema.academico.dominio.entidad.Inscripcion;
import com.sistema.academico.dominio.enumeracion.Rol;
import com.sistema.academico.infraestructura.excepcion.PermisosDenegadosException;
import com.sistema.academico.infraestructura.excepcion.RecursoNoEncontradoException;
import com.sistema.academico.infraestructura.excepcion.ValidacionException;
import com.sistema.academico.infraestructura.repositorio.CalificacionRepository;
import com.sistema.academico.infraestructura.repositorio.InscripcionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalificacionServiceImpl implements ICalificacionService {

    private final CalificacionRepository calificacionRepository;
    private final InscripcionRepository inscripcionRepository;
    private final CalificacionMapper calificacionMapper;

    @Override
    @Transactional
    public CalificacionResponseDTO registrar(CalificacionRequestDTO request) {
        // Validar que la inscripción existe
        Inscripcion inscripcion = inscripcionRepository.findById(request.getInscripcionId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Inscripción no encontrada"));

        // Validar que la inscripción esté activa
        if (!inscripcion.estaActiva()) {
            throw new ValidacionException("Solo se pueden registrar calificaciones en inscripciones activas");
        }

        // Validar que la suma de porcentajes no exceda 100%
        List<Calificacion> calificacionesExistentes =
                calificacionRepository.findByInscripcion(inscripcion);

        int sumaActual = calificacionesExistentes.stream()
                .mapToInt(Calificacion::getPorcentaje)
                .sum();

        if (sumaActual + request.getPorcentaje() > 100) {
            throw new ValidacionException(
                    "La suma de porcentajes excede 100%. Actual: " + sumaActual + "%"
            );
        }

        Calificacion calificacion = calificacionMapper.toEntity(request, inscripcion);
        Calificacion guardada = calificacionRepository.save(calificacion);

        return calificacionMapper.toResponseDTO(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public CalificacionResponseDTO obtenerPorId(Long id) {
        Calificacion calificacion = calificacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Calificación no encontrada con ID: " + id));

        return calificacionMapper.toResponseDTO(calificacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CalificacionResponseDTO> listarTodas() {
        return calificacionRepository.findAll().stream()
                .map(calificacionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CalificacionResponseDTO> listarPorInscripcion(Long inscripcionId) {
        Inscripcion inscripcion = inscripcionRepository.findById(inscripcionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Inscripción no encontrada"));

        return calificacionRepository.findByInscripcionOrderByFechaCalificacionAsc(inscripcion).stream()
                .map(calificacionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CalificacionResponseDTO actualizar(Long id, CalificacionRequestDTO request) {
        Calificacion calificacion = calificacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Calificación no encontrada con ID: " + id));

        // Validar que la inscripción esté activa
        if (!calificacion.getInscripcion().estaActiva()) {
            throw new ValidacionException("No se pueden actualizar calificaciones de inscripciones no activas");
        }

        // Validar porcentaje si cambió
        if (request.getPorcentaje() != null &&
                !request.getPorcentaje().equals(calificacion.getPorcentaje())) {

            List<Calificacion> calificacionesExistentes =
                    calificacionRepository.findByInscripcion(calificacion.getInscripcion());

            int sumaActual = calificacionesExistentes.stream()
                    .filter(c -> !c.getId().equals(id))
                    .mapToInt(Calificacion::getPorcentaje)
                    .sum();

            if (sumaActual + request.getPorcentaje() > 100) {
                throw new ValidacionException(
                        "La suma de porcentajes excede 100%. Actual: " + sumaActual + "%"
                );
            }
        }

        calificacionMapper.updateEntityFromDTO(calificacion, request);
        Calificacion actualizada = calificacionRepository.save(calificacion);

        return calificacionMapper.toResponseDTO(actualizada);
    }

    @Override
    @Transactional
    public void eliminar(Long id, Rol rolUsuarioActual) {
        if (!rolUsuarioActual.puedeEliminarFisicamente()) {
            throw new PermisosDenegadosException("Solo SUPER_ADMIN puede eliminar calificaciones físicamente");
        }

        Calificacion calificacion = calificacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Calificación no encontrada con ID: " + id));

        calificacionRepository.delete(calificacion);
    }
}