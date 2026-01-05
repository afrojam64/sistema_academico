package com.sistema.academico.aplicacion.servicio.implementacion;

import com.sistema.academico.aplicacion.dto.request.CalificacionRequestDTO;
import com.sistema.academico.aplicacion.dto.response.CalificacionResponseDTO;
import com.sistema.academico.aplicacion.dto.response.CalificacionesEstudianteReporteDTO;
import com.sistema.academico.aplicacion.mapper.CalificacionMapper;
import com.sistema.academico.aplicacion.servicio.ICalificacionService;
import com.sistema.academico.dominio.entidad.Calificacion;
import com.sistema.academico.dominio.entidad.Curso;
import com.sistema.academico.dominio.entidad.Estudiante;
import com.sistema.academico.dominio.entidad.Inscripcion;
import com.sistema.academico.dominio.enumeracion.Rol;
import com.sistema.academico.infraestructura.excepcion.OperacionNoPermitidaException;
import com.sistema.academico.infraestructura.excepcion.RecursoNoEncontradoException;
import com.sistema.academico.infraestructura.excepcion.ValidacionNegocioException;
import com.sistema.academico.infraestructura.repositorio.CalificacionRepository;
import com.sistema.academico.infraestructura.repositorio.CursoRepository;
import com.sistema.academico.infraestructura.repositorio.EstudianteRepository;
import com.sistema.academico.infraestructura.repositorio.InscripcionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalificacionServiceImpl implements ICalificacionService {

    private final CalificacionRepository calificacionRepository;
    private final InscripcionRepository inscripcionRepository;
    private final EstudianteRepository estudianteRepository;
    private final CursoRepository cursoRepository;
    private final CalificacionMapper calificacionMapper;

    @Override
    @Transactional
    public CalificacionResponseDTO registrar(CalificacionRequestDTO request) {
        // Validar que la inscripción existe
        Inscripcion inscripcion = inscripcionRepository.findById(request.getInscripcionId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Inscripción no encontrada"));

        // Validar que la inscripción esté activa
        if (!inscripcion.estaActiva()) {
            throw new ValidacionNegocioException("Solo se pueden registrar calificaciones en inscripciones activas");
        }

        // Validar que la suma de porcentajes no exceda 100%
        List<Calificacion> calificacionesExistentes =
                calificacionRepository.findByInscripcion(inscripcion);

        int sumaActual = calificacionesExistentes.stream()
                .mapToInt(Calificacion::getPorcentaje)
                .sum();

        if (sumaActual + request.getPorcentaje() > 100) {
            throw new ValidacionNegocioException(
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
            throw new ValidacionNegocioException("No se pueden actualizar calificaciones de inscripciones no activas");
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
                throw new ValidacionNegocioException(
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
            throw new OperacionNoPermitidaException("Solo SUPER_ADMIN puede eliminar calificaciones físicamente");
        }

        Calificacion calificacion = calificacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Calificación no encontrada con ID: " + id));

        calificacionRepository.delete(calificacion);
    }

    // ========================================
    // MÉTODOS PARA DASHBOARDS
    // ========================================

    @Override
    @Transactional(readOnly = true)
    public List<CalificacionResponseDTO> listarPorEstudiante(Long estudianteId) {
        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado con ID: " + estudianteId));

        // Obtener todas las inscripciones del estudiante
        List<Inscripcion> inscripciones = inscripcionRepository.findByEstudiante(estudiante);

        // Obtener todas las calificaciones de esas inscripciones
        return inscripciones.stream()
                .flatMap(inscripcion -> calificacionRepository.findByInscripcion(inscripcion).stream())
                .map(calificacionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CalificacionResponseDTO> listarPorCurso(Long cursoId) {
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Curso no encontrado con ID: " + cursoId));

        // Obtener todas las inscripciones del curso
        List<Inscripcion> inscripciones = inscripcionRepository.findByCurso(curso);

        // Obtener todas las calificaciones de esas inscripciones
        return inscripciones.stream()
                .flatMap(inscripcion -> calificacionRepository.findByInscripcion(inscripcion).stream())
                .map(calificacionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Generar reporte completo de calificaciones de un estudiante
     */
    /**
     * Generar reporte completo de calificaciones de un estudiante
     */
    @Override
    @Transactional(readOnly = true)
    public CalificacionesEstudianteReporteDTO generarReporteEstudiante(Long estudianteId) {
        // Validar que el estudiante existe
        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Estudiante no encontrado con ID: " + estudianteId));

        // Obtener todas las inscripciones del estudiante
        List<Inscripcion> inscripciones = inscripcionRepository.findByEstudiante(estudiante);

        // Agrupar calificaciones por curso
        List<CalificacionesEstudianteReporteDTO.CursoCalificaciones> cursosCalificaciones = new ArrayList<>();
        double sumaPromedios = 0.0;
        int cursosConCalificaciones = 0;
        int cursosAprobados = 0;
        int cursosReprobados = 0;

        for (Inscripcion inscripcion : inscripciones) {
            // Obtener calificaciones de esta inscripción
            List<Calificacion> calificaciones = calificacionRepository.findByInscripcion(inscripcion);

            if (calificaciones.isEmpty()) {
                continue; // Saltar inscripciones sin calificaciones
            }

            // Convertir calificaciones a DTOs
            List<CalificacionesEstudianteReporteDTO.DetalleCalificacion> detalles = new ArrayList<>();
            double sumaNotasPonderadas = 0.0;

            for (Calificacion calif : calificaciones) {
                // Convertir BigDecimal a Double
                Double nota = calif.getNota().doubleValue();
                Double notaPonderada = calif.getNotaPonderada().doubleValue();

                // Calcular estado de la evaluación
                String estadoEval = nota >= 3.0 ? "APROBADA" : "REPROBADA";

                CalificacionesEstudianteReporteDTO.DetalleCalificacion detalle =
                        CalificacionesEstudianteReporteDTO.DetalleCalificacion.builder()
                                .nombreEvaluacion(calif.getNombreEvaluacion())
                                .nota(nota)
                                .porcentaje(calif.getPorcentaje())
                                .notaPonderada(notaPonderada)
                                .estado(estadoEval)
                                .fechaRegistro("") // Sin fecha por ahora
                                .build();

                detalles.add(detalle);
                sumaNotasPonderadas += notaPonderada;
            }

            // Calcular promedio del curso
            double promedioFinal = sumaNotasPonderadas;
            String estadoCurso;

            if (promedioFinal >= 3.0) {
                estadoCurso = "APROBADO";
                cursosAprobados++;
            } else {
                estadoCurso = "REPROBADO";
                cursosReprobados++;
            }

            // Crear DTO del curso
            CalificacionesEstudianteReporteDTO.CursoCalificaciones cursoCalif =
                    CalificacionesEstudianteReporteDTO.CursoCalificaciones.builder()
                            .nombreCurso(inscripcion.getCurso().getMateria().getNombre())
                            .codigoCurso(inscripcion.getCurso().getCodigo())
                            .periodo(inscripcion.getCurso().getPeriodo())
                            .calificaciones(detalles)
                            .promedioFinal(promedioFinal)
                            .estado(estadoCurso)
                            .build();

            cursosCalificaciones.add(cursoCalif);
            sumaPromedios += promedioFinal;
            cursosConCalificaciones++;
        }

        // Calcular promedio general
        double promedioGeneral = cursosConCalificaciones > 0 ?
                sumaPromedios / cursosConCalificaciones : 0.0;

        // Construir DTO final
        return CalificacionesEstudianteReporteDTO.builder()
                .estudianteId(estudiante.getId())
                .nombreCompleto(estudiante.getUsuario().getNombre() + " " +
                        estudiante.getUsuario().getApellido())
                .codigo("EST-" + estudiante.getId())
                .email(estudiante.getUsuario().getEmail())
                .cursos(cursosCalificaciones)
                .promedioGeneral(Math.round(promedioGeneral * 100.0) / 100.0)
                .totalCursos(cursosConCalificaciones)
                .cursosAprobados(cursosAprobados)
                .cursosReprobados(cursosReprobados)
                .build();
    }
}