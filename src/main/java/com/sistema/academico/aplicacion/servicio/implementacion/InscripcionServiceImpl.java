package com.sistema.academico.aplicacion.servicio.implementacion;

import com.sistema.academico.aplicacion.dto.request.InscripcionRequestDTO;
import com.sistema.academico.aplicacion.dto.response.InscripcionResponseDTO;
import com.sistema.academico.aplicacion.dto.response.InscripcionesPorCursoReporteDTO;
import com.sistema.academico.aplicacion.dto.response.InscripcionesPorPeriodoReporteDTO;
import com.sistema.academico.aplicacion.mapper.InscripcionMapper;
import com.sistema.academico.aplicacion.servicio.IInscripcionService;
import com.sistema.academico.dominio.entidad.*;
import com.sistema.academico.dominio.enumeracion.EstadoInscripcion;
import com.sistema.academico.dominio.enumeracion.Rol;
import com.sistema.academico.infraestructura.excepcion.RecursoDuplicadoException;
import com.sistema.academico.infraestructura.excepcion.OperacionNoPermitidaException;
import com.sistema.academico.infraestructura.excepcion.RecursoNoEncontradoException;
import com.sistema.academico.infraestructura.excepcion.ValidacionNegocioException;
import com.sistema.academico.infraestructura.repositorio.*;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final CalificacionRepository calificacionRepository;

    @Override
    @Transactional
    public InscripcionResponseDTO crear(InscripcionRequestDTO request) {

        // ✅ NUEVO: Obtener estudiante del token JWT si no viene en el request
        Estudiante estudiante;

        if (request.getEstudianteId() != null) {
            // Caso 1: Admin/Profesor creando inscripción (estudianteId viene en request)
            estudiante = estudianteRepository.findById(request.getEstudianteId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado"));
        } else {
            // Caso 2: Estudiante creando su propia inscripción (obtener del token JWT)
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String nombreUsuario = auth.getName();

            estudiante = estudianteRepository.findByUsuario_NombreUsuario(nombreUsuario)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado"));
        }

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

    /**
     * Generar reporte detallado de inscripciones por curso
     */
    @Override
    @Transactional(readOnly = true)
    public InscripcionesPorCursoReporteDTO generarReporteInscripcionesPorCurso(Long cursoId) {

        // Obtener el curso
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Curso no encontrado con ID: " + cursoId));

        // Obtener todas las inscripciones del curso
        List<Inscripcion> inscripciones = inscripcionRepository.findByCurso(curso);

        // Contadores por estado
        int inscripcionesActivas = 0;
        int inscripcionesRetiradas = 0;
        int inscripcionesCompletadas = 0;

        // Lista de estudiantes inscritos
        List<InscripcionesPorCursoReporteDTO.EstudianteInscrito> estudiantesData = new ArrayList<>();

        for (Inscripcion inscripcion : inscripciones) {
            // Contar por estado
            switch (inscripcion.getEstado()) {
                case ACTIVO -> inscripcionesActivas++;
                case RETIRADO -> inscripcionesRetiradas++;
                case COMPLETADO -> inscripcionesCompletadas++;
            }

            Estudiante estudiante = inscripcion.getEstudiante();

            // Obtener calificaciones del estudiante en este curso
            List<Calificacion> calificaciones = calificacionRepository.findByInscripcion(inscripcion);

            boolean tieneCalificaciones = !calificaciones.isEmpty();
            int numeroCalificaciones = calificaciones.size();
            double promedioActual = 0.0;
            int porcentajeEvaluado = 0;

            if (tieneCalificaciones) {
                // Calcular promedio simple de las notas
                double sumaNotas = calificaciones.stream()
                        .mapToDouble(c -> c.getNota().doubleValue())
                        .sum();
                promedioActual = sumaNotas / numeroCalificaciones;

                // Calcular porcentaje evaluado
                porcentajeEvaluado = calificaciones.stream()
                        .mapToInt(Calificacion::getPorcentaje)
                        .sum();
            }

            InscripcionesPorCursoReporteDTO.EstudianteInscrito estudianteData =
                    InscripcionesPorCursoReporteDTO.EstudianteInscrito.builder()
                            .estudianteId(estudiante.getId())
                            .codigoEstudiante(estudiante.getCodigoEstudiante())
                            .nombreCompleto(estudiante.getUsuario().getNombre() + " " +
                                    estudiante.getUsuario().getApellido())
                            .cedula(estudiante.getUsuario().getCedula())
                            .email(estudiante.getUsuario().getEmail())
                            .telefono(estudiante.getUsuario().getTelefono())
                            .inscripcionId(inscripcion.getId())
                            .fechaInscripcion(inscripcion.getFechaInscripcion().format(
                                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                            .estadoInscripcion(inscripcion.getEstado().name())
                            .fechaActualizacion(inscripcion.getFechaActualizacion() != null ?
                                    inscripcion.getFechaActualizacion().format(
                                            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "")
                            .tieneCalificaciones(tieneCalificaciones)
                            .numeroCalificaciones(numeroCalificaciones)
                            .promedioActual(Math.round(promedioActual * 100.0) / 100.0)
                            .porcentajeEvaluado(porcentajeEvaluado)
                            .build();

            estudiantesData.add(estudianteData);
        }

        // Ordenar estudiantes por nombre
        estudiantesData.sort((e1, e2) -> e1.getNombreCompleto().compareTo(e2.getNombreCompleto()));

        // Calcular estadísticas
        int totalInscripciones = inscripciones.size();
        int cupoMaximo = curso.getCupoMaximo();
        int cuposDisponibles = cupoMaximo - inscripcionesActivas;
        double porcentajeOcupacion = cupoMaximo > 0 ?
                (inscripcionesActivas * 100.0) / cupoMaximo : 0.0;

        // Construir DTO final
        return InscripcionesPorCursoReporteDTO.builder()
                .cursoId(curso.getId())
                .codigoCurso(curso.getCodigo())
                .nombreCurso(curso.getNombre())
                .nombreMateria(curso.getMateria().getNombre())
                .codigoMateria(curso.getMateria().getCodigo())
                .creditos(curso.getMateria().getCreditos())
                .periodo(curso.getPeriodo())
                .fechaInicio(curso.getFechaInicio().format(
                        java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .fechaFin(curso.getFechaFin().format(
                        java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .nombreProfesor(curso.getProfesor().getUsuario().getNombre() + " " +
                        curso.getProfesor().getUsuario().getApellido())
                .emailProfesor(curso.getProfesor().getUsuario().getEmail())
                .nombreDepartamento(curso.getMateria().getDepartamento().getNombre())
                .cupoMaximo(cupoMaximo)
                .totalInscripciones(totalInscripciones)
                .inscripcionesActivas(inscripcionesActivas)
                .inscripcionesRetiradas(inscripcionesRetiradas)
                .inscripcionesCompletadas(inscripcionesCompletadas)
                .cuposDisponibles(cuposDisponibles)
                .porcentajeOcupacion(Math.round(porcentajeOcupacion * 100.0) / 100.0)
                .estudiantes(estudiantesData)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public InscripcionesPorPeriodoReporteDTO generarReporteInscripcionesPorPeriodo(String periodo) {

        // 1. Obtener todos los cursos del periodo
        List<Curso> cursosDelPeriodo = cursoRepository.findByPeriodo(periodo);

        if (cursosDelPeriodo.isEmpty()) {
            throw new RecursoNoEncontradoException("No existen cursos para el periodo: " + periodo);
        }

        // 2. Obtener todas las inscripciones de esos cursos
        List<Inscripcion> inscripciones = new ArrayList<>();
        for (Curso curso : cursosDelPeriodo) {
            inscripciones.addAll(inscripcionRepository.findByCurso(curso));
        }

        // 3. Calcular estadísticas generales
        InscripcionesPorPeriodoReporteDTO.EstadisticasGenerales estadisticas = calcularEstadisticasPeriodo(inscripciones, cursosDelPeriodo);

        // 4. Construir detalles de inscripciones
        List<InscripcionesPorPeriodoReporteDTO.InscripcionDetalle> detalles = inscripciones.stream()
                .map(this::construirInscripcionDetallePeriodo)
                .sorted(Comparator
                        .comparing(InscripcionesPorPeriodoReporteDTO.InscripcionDetalle::getCarrera)
                        .thenComparing(InscripcionesPorPeriodoReporteDTO.InscripcionDetalle::getCodigoEstudiante))
                .collect(Collectors.toList());

        // 5. Construir y retornar el reporte completo
        return InscripcionesPorPeriodoReporteDTO.builder()
                .periodo(periodo)
                .estadisticas(estadisticas)
                .inscripciones(detalles)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> obtenerPeriodosDisponibles() {
        return cursoRepository.findDistinctPeriodos();
    }

    private InscripcionesPorPeriodoReporteDTO.EstadisticasGenerales calcularEstadisticasPeriodo(List<Inscripcion> inscripciones, List<Curso> cursos) {

        // Contar por estado
        long activas = inscripciones.stream()
                .filter(i -> i.getEstado() == EstadoInscripcion.ACTIVO)
                .count();

        long retiradas = inscripciones.stream()
                .filter(i -> i.getEstado() == EstadoInscripcion.RETIRADO)
                .count();

        long completadas = inscripciones.stream()
                .filter(i -> i.getEstado() == EstadoInscripcion.COMPLETADO)
                .count();

        // Estudiantes únicos
        long estudiantesUnicos = inscripciones.stream()
                .map(i -> i.getEstudiante().getId())
                .distinct()
                .count();

        // Cupos
        int cuposTotales = cursos.stream()
                .mapToInt(Curso::getCupoMaximo)
                .sum();

        int cuposOcupados = cursos.stream()
                .mapToInt(Curso::getCupoActual)
                .sum();

        int cuposDisponibles = cuposTotales - cuposOcupados;

        double porcentajeOcupacion = cuposTotales > 0
                ? (cuposOcupados * 100.0 / cuposTotales)
                : 0.0;

        // Distribución por carrera
        Map<String, List<Inscripcion>> porCarrera = inscripciones.stream()
                .collect(Collectors.groupingBy(i -> i.getEstudiante().getCarrera()));

        List<InscripcionesPorPeriodoReporteDTO.CarreraDistribucion> distribucionCarreras = porCarrera.entrySet().stream()
                .map(entry -> InscripcionesPorPeriodoReporteDTO.CarreraDistribucion.builder()
                        .carrera(entry.getKey())
                        .totalInscripciones(entry.getValue().size())
                        .estudiantesUnicos((int) entry.getValue().stream()
                                .map(i -> i.getEstudiante().getId())
                                .distinct()
                                .count())
                        .build())
                .sorted(Comparator.comparing(InscripcionesPorPeriodoReporteDTO.CarreraDistribucion::getTotalInscripciones).reversed())
                .collect(Collectors.toList());

        return InscripcionesPorPeriodoReporteDTO.EstadisticasGenerales.builder()
                .totalInscripciones(inscripciones.size())
                .estudiantesUnicos((int) estudiantesUnicos)
                .cursosOfertados(cursos.size())
                .cuposTotales(cuposTotales)
                .cuposOcupados(cuposOcupados)
                .cuposDisponibles(cuposDisponibles)
                .porcentajeOcupacion(Math.round(porcentajeOcupacion * 100.0) / 100.0)
                .inscripcionesActivas((int) activas)
                .inscripcionesRetiradas((int) retiradas)
                .inscripcionesCompletadas((int) completadas)
                .distribucionCarreras(distribucionCarreras)
                .build();
    }

    private InscripcionesPorPeriodoReporteDTO.InscripcionDetalle construirInscripcionDetallePeriodo(Inscripcion inscripcion) {

        Estudiante estudiante = inscripcion.getEstudiante();
        Curso curso = inscripcion.getCurso();

        // Obtener calificaciones de esta inscripción
        List<Calificacion> calificaciones = calificacionRepository.findByInscripcion(inscripcion);

        // Calcular datos de calificaciones
        int numeroEvaluaciones = calificaciones.size();

        double promedioActual = 0.0;
        double porcentajeEvaluado = 0.0;

        if (!calificaciones.isEmpty()) {
            double sumaNotas = 0.0;
            int sumaPorcentajes = 0;

            for (Calificacion cal : calificaciones) {
                sumaNotas += cal.getNota().doubleValue() * cal.getPorcentaje() / 100.0;
                sumaPorcentajes += cal.getPorcentaje();
            }

            promedioActual = Math.round(sumaNotas * 100.0) / 100.0;
            porcentajeEvaluado = sumaPorcentajes;
        }

        return InscripcionesPorPeriodoReporteDTO.InscripcionDetalle.builder()
                // Datos del estudiante
                .estudianteId(estudiante.getId())
                .codigoEstudiante(estudiante.getCodigoEstudiante())
                .nombreEstudiante(estudiante.getUsuario().getNombre())
                .apellidoEstudiante(estudiante.getUsuario().getApellido())
                .emailEstudiante(estudiante.getUsuario().getEmail())
                .carrera(estudiante.getCarrera())
                .semestre(estudiante.getSemestre())
                // Datos del curso
                .cursoId(curso.getId())
                .codigoCurso(curso.getCodigo())
                .nombreCurso(curso.getNombre())
                .nombreMateria(curso.getMateria().getNombre())
                .nombreProfesor(curso.getProfesor().getUsuario().getNombre() + " " +
                        curso.getProfesor().getUsuario().getApellido())
                .departamento(curso.getMateria().getDepartamento().getNombre())
                .creditos(curso.getMateria().getCreditos())
                // Datos de la inscripción
                .inscripcionId(inscripcion.getId())
                .fechaInscripcion(inscripcion.getFechaInscripcion())
                .estado(inscripcion.getEstado().name())
                // Datos de calificaciones
                .numeroEvaluaciones(numeroEvaluaciones)
                .promedioActual(numeroEvaluaciones > 0 ? promedioActual : null)
                .porcentajeEvaluado(numeroEvaluaciones > 0 ? porcentajeEvaluado : null)
                .build();
    }
}