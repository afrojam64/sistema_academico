package com.sistema.academico.aplicacion.servicio.implementacion;

import com.sistema.academico.aplicacion.dto.request.CalificacionRequestDTO;
import com.sistema.academico.aplicacion.dto.response.CalificacionResponseDTO;
import com.sistema.academico.aplicacion.dto.response.CalificacionesCursoReporteDTO;
import com.sistema.academico.aplicacion.dto.response.CalificacionesEstudianteReporteDTO;
import com.sistema.academico.aplicacion.dto.response.EstudiantesEnRiesgoReporteDTO;
import com.sistema.academico.aplicacion.mapper.CalificacionMapper;
import com.sistema.academico.aplicacion.servicio.ICalificacionService;
import com.sistema.academico.dominio.entidad.*;
import com.sistema.academico.dominio.enumeracion.Estado;
import com.sistema.academico.dominio.enumeracion.EstadoInscripcion;
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
import com.sistema.academico.aplicacion.dto.request.CalificacionBatchRequestDTO;

import java.math.BigDecimal;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import java.time.LocalDate;
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

    @Override
    public CalificacionesCursoReporteDTO generarReporteCurso(Long cursoId, LocalDate fechaInicio, LocalDate fechaFin) {

        // Validar que el curso existe
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Curso no encontrado con ID: " + cursoId));

        // Obtener todas las inscripciones del curso
        List<Inscripcion> inscripciones = inscripcionRepository.findByCurso(curso);

        // Lista de estudiantes con sus calificaciones
        List<CalificacionesCursoReporteDTO.EstudianteCalificaciones> estudiantesData = new ArrayList<>();

        // Variables para estadísticas
        int totalEstudiantes = inscripciones.size();
        int estudiantesActivos = 0;
        int estudiantesRetirados = 0;
        int estudiantesCompletados = 0;
        int aprobados = 0;
        int reprobados = 0;
        int enCurso = 0;
        int estudiantesRiesgo = 0;

        double sumaPromedios = 0.0;
        double mejorPromedio = 0.0;
        double peorPromedio = 5.0;

        int rango0a2 = 0;
        int rango3a3 = 0;
        int rango4a5 = 0;

        int estudiantesConCalificaciones = 0;

        // Procesar cada inscripción
        for (Inscripcion inscripcion : inscripciones) {
            // Contar estados de inscripción usando el enum correcto
            String estadoInsc = inscripcion.getEstado().name();
            if ("ACTIVO".equals(estadoInsc)) {
                estudiantesActivos++;
            } else if ("RETIRADO".equals(estadoInsc)) {
                estudiantesRetirados++;
            } else if ("COMPLETADO".equals(estadoInsc)) {
                estudiantesCompletados++;
            }

            // Obtener calificaciones de la inscripción
            List<Calificacion> calificaciones = calificacionRepository.findByInscripcion(inscripcion);

            // Filtrar por rango de fechas si se especificó (SIN usar getFechaRegistro)
            // Las fechas se omiten si no existe el campo
            List<Calificacion> calificacionesFiltradas = calificaciones;

            // Convertir calificaciones a DTOs
            List<CalificacionesCursoReporteDTO.DetalleCalificacion> detalles = new ArrayList<>();
            double sumaNotasPonderadas = 0.0;

            for (Calificacion calif : calificacionesFiltradas) {
                CalificacionesCursoReporteDTO.DetalleCalificacion detalle =
                        CalificacionesCursoReporteDTO.DetalleCalificacion.builder()
                                .calificacionId(calif.getId())
                                .nombreEvaluacion(calif.getNombreEvaluacion())
                                .nota(calif.getNota().doubleValue())
                                .porcentaje(calif.getPorcentaje())
                                .notaPonderada(calif.getNotaPonderada().doubleValue())
                                .fechaRegistro("") // Sin fecha por ahora
                                .build();

                detalles.add(detalle);
                sumaNotasPonderadas += calif.getNotaPonderada().doubleValue();
            }

            // Calcular promedio del estudiante
            double promedioFinal = sumaNotasPonderadas;
            String estadoEstudiante = "EN_CURSO";

            if (!calificacionesFiltradas.isEmpty()) {
                if (promedioFinal >= 3.0) {
                    estadoEstudiante = "APROBADO";
                    aprobados++;
                } else {
                    estadoEstudiante = "REPROBADO";
                    reprobados++;
                }

                // Estudiantes en riesgo
                if (promedioFinal < 3.0) {
                    estudiantesRiesgo++;
                }

                // Distribución de notas
                if (promedioFinal < 3.0) {
                    rango0a2++;
                } else if (promedioFinal < 4.0) {
                    rango3a3++;
                } else {
                    rango4a5++;
                }

                // Estadísticas de promedios
                sumaPromedios += promedioFinal;
                estudiantesConCalificaciones++;

                if (promedioFinal > mejorPromedio) {
                    mejorPromedio = promedioFinal;
                }
                if (promedioFinal < peorPromedio) {
                    peorPromedio = promedioFinal;
                }
            } else {
                enCurso++;
            }

            // Crear DTO del estudiante
            Estudiante estudiante = inscripcion.getEstudiante();
            CalificacionesCursoReporteDTO.EstudianteCalificaciones estudianteData =
                    CalificacionesCursoReporteDTO.EstudianteCalificaciones.builder()
                            .estudianteId(estudiante.getId())
                            .codigoEstudiante(estudiante.getCodigoEstudiante())
                            .nombreCompleto(estudiante.getUsuario().getNombre() + " " +
                                    estudiante.getUsuario().getApellido())
                            .cedula(estudiante.getUsuario().getCedula())
                            .estadoInscripcion(inscripcion.getEstado().name())
                            .calificaciones(detalles)
                            .promedioFinal(promedioFinal)
                            .estado(estadoEstudiante)
                            .build();

            estudiantesData.add(estudianteData);
        }

        // Calcular estadísticas generales
        double promedioGeneral = estudiantesConCalificaciones > 0 ?
                sumaPromedios / estudiantesConCalificaciones : 0.0;

        double tasaAprobacion = (aprobados + reprobados) > 0 ?
                (aprobados * 100.0) / (aprobados + reprobados) : 0.0;

        // Crear objeto de distribución
        CalificacionesCursoReporteDTO.DistribucionNotas distribucion =
                CalificacionesCursoReporteDTO.DistribucionNotas.builder()
                        .rango0a2(rango0a2)
                        .rango3a3(rango3a3)
                        .rango4a5(rango4a5)
                        .build();

        // Crear objeto de estadísticas
        CalificacionesCursoReporteDTO.Estadisticas estadisticas =
                CalificacionesCursoReporteDTO.Estadisticas.builder()
                        .totalEstudiantes(totalEstudiantes)
                        .estudiantesActivos(estudiantesActivos)
                        .estudiantesRetirados(estudiantesRetirados)
                        .estudiantesCompletados(estudiantesCompletados)
                        .promedioGeneral(Math.round(promedioGeneral * 100.0) / 100.0)
                        .mejorPromedio(Math.round(mejorPromedio * 100.0) / 100.0)
                        .peorPromedio(estudiantesConCalificaciones > 0 ?
                                Math.round(peorPromedio * 100.0) / 100.0 : 0.0)
                        .aprobados(aprobados)
                        .reprobados(reprobados)
                        .enCurso(enCurso)
                        .tasaAprobacion(Math.round(tasaAprobacion * 100.0) / 100.0)
                        .estudiantesRiesgo(estudiantesRiesgo)
                        .distribucion(distribucion)
                        .build();

        // Construir DTO final
        return CalificacionesCursoReporteDTO.builder()
                .cursoId(curso.getId())
                .codigoCurso(curso.getCodigo())
                .nombreMateria(curso.getMateria().getNombre())
                .periodo(curso.getPeriodo())
                .nombreProfesor(curso.getProfesor().getUsuario().getNombre() + " " +
                        curso.getProfesor().getUsuario().getApellido())
                .cupoMaximo(curso.getCupoMaximo())
                .estudiantesInscritos(totalEstudiantes)
                .estudiantes(estudiantesData)
                .estadisticas(estadisticas)
                .build();
    }

    /**
     * Generar reporte de estudiantes en riesgo académico
     */
    @Override
    @Transactional(readOnly = true)
    public EstudiantesEnRiesgoReporteDTO generarReporteEstudiantesEnRiesgo(Long estudianteId) {

        List<Estudiante> estudiantesAEvaluar;

        // Determinar qué estudiantes evaluar
        if (estudianteId != null) {
            // Buscar estudiante específico
            Estudiante estudiante = estudianteRepository.findById(estudianteId)
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Estudiante no encontrado con ID: " + estudianteId));
            estudiantesAEvaluar = List.of(estudiante);
        } else {
            // Buscar todos los estudiantes activos
            estudiantesAEvaluar = estudianteRepository.findAll().stream()
                    .filter(e -> e.getEstado() == Estado.ACTIVO)
                    .collect(Collectors.toList());
        }

        // Lista de estudiantes en riesgo
        List<EstudiantesEnRiesgoReporteDTO.EstudianteEnRiesgo> estudiantesEnRiesgo = new ArrayList<>();
        double sumaPromediosRiesgo = 0.0;
        int contadorEstudiantesRiesgo = 0;

        // Evaluar cada estudiante
        for (Estudiante estudiante : estudiantesAEvaluar) {
            // Obtener inscripciones activas del estudiante
            List<Inscripcion> inscripciones = inscripcionRepository.findByEstudianteAndEstado(
                    estudiante, EstadoInscripcion.ACTIVO);

            if (inscripciones.isEmpty()) {
                continue; // Estudiante sin inscripciones activas
            }

            // Analizar cada inscripción
            List<EstudiantesEnRiesgoReporteDTO.CursoEnRiesgo> cursosConRiesgo = new ArrayList<>();
            double sumaPromediosCursos = 0.0;
            int totalCursos = inscripciones.size();
            int cursosEnRiesgo = 0;

            for (Inscripcion inscripcion : inscripciones) {
                // Obtener calificaciones de esta inscripción
                List<Calificacion> calificaciones = calificacionRepository.findByInscripcion(inscripcion);

                if (calificaciones.isEmpty()) {
                    continue; // Sin calificaciones aún
                }

                // Calcular estadísticas del curso
                double sumaNotas = 0.0;
                double notaAcumulada = 0.0;
                int porcentajeEvaluado = 0;
                List<EstudiantesEnRiesgoReporteDTO.CalificacionDetalle> detalles = new ArrayList<>();

                for (Calificacion calif : calificaciones) {
                    double nota = calif.getNota().doubleValue();
                    double notaPonderada = calif.getNotaPonderada().doubleValue();

                    sumaNotas += nota;
                    notaAcumulada += notaPonderada;
                    porcentajeEvaluado += calif.getPorcentaje();

                    EstudiantesEnRiesgoReporteDTO.CalificacionDetalle detalle =
                            EstudiantesEnRiesgoReporteDTO.CalificacionDetalle.builder()
                                    .nombreEvaluacion(calif.getNombreEvaluacion())
                                    .nota(nota)
                                    .porcentaje(calif.getPorcentaje())
                                    .notaPonderada(notaPonderada)
                                    .fechaCalificacion(calif.getFechaCalificacion() != null ?
                                            calif.getFechaCalificacion().format(
                                                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "")
                                    .build();

                    detalles.add(detalle);
                }

                // Calcular promedio de notas (proyección)
                double promedioNotas = sumaNotas / calificaciones.size();
                int porcentajePendiente = 100 - porcentajeEvaluado;

                // Calcular nota necesaria para aprobar
                double notaNecesaria = 0.0;
                if (porcentajePendiente > 0) {
                    // Necesita: (3.0 - notaAcumulada) / (porcentajePendiente/100)
                    double puntosNecesarios = 3.0 - notaAcumulada;
                    notaNecesaria = (puntosNecesarios * 100.0) / porcentajePendiente;
                    notaNecesaria = Math.max(0, Math.min(5.0, notaNecesaria)); // Entre 0 y 5
                }

                // Determinar nivel de riesgo
                String nivelRiesgo;
                if (promedioNotas < 2.0) {
                    nivelRiesgo = "ALTO";
                } else if (promedioNotas < 3.0) {
                    nivelRiesgo = "MEDIO";
                } else {
                    nivelRiesgo = "BAJO";
                }

                sumaPromediosCursos += promedioNotas;

                // Solo agregar si está en riesgo (proyección < 3.0)
                if (promedioNotas < 3.0) {
                    cursosEnRiesgo++;

                    Curso curso = inscripcion.getCurso();
                    EstudiantesEnRiesgoReporteDTO.CursoEnRiesgo cursoRiesgo =
                            EstudiantesEnRiesgoReporteDTO.CursoEnRiesgo.builder()
                                    .cursoId(curso.getId())
                                    .codigoCurso(curso.getCodigo())
                                    .nombreMateria(curso.getMateria().getNombre())
                                    .periodo(curso.getPeriodo())
                                    .nombreProfesor(curso.getProfesor().getUsuario().getNombre() + " " +
                                            curso.getProfesor().getUsuario().getApellido())
                                    .calificaciones(detalles)
                                    .promedioNotas(Math.round(promedioNotas * 100.0) / 100.0)
                                    .notaAcumulada(Math.round(notaAcumulada * 100.0) / 100.0)
                                    .porcentajeEvaluado(porcentajeEvaluado)
                                    .porcentajePendiente(porcentajePendiente)
                                    .notaNecesaria(Math.round(notaNecesaria * 100.0) / 100.0)
                                    .nivelRiesgo(nivelRiesgo)
                                    .build();

                    cursosConRiesgo.add(cursoRiesgo);
                }
            }

            // Si el estudiante tiene al menos un curso en riesgo, agregarlo al reporte
            if (!cursosConRiesgo.isEmpty()) {
                double promedioGeneral = sumaPromediosCursos / totalCursos;

                EstudiantesEnRiesgoReporteDTO.EstudianteEnRiesgo estudianteRiesgo =
                        EstudiantesEnRiesgoReporteDTO.EstudianteEnRiesgo.builder()
                                .estudianteId(estudiante.getId())
                                .codigoEstudiante(estudiante.getCodigoEstudiante())
                                .nombreCompleto(estudiante.getUsuario().getNombre() + " " +
                                        estudiante.getUsuario().getApellido())
                                .cedula(estudiante.getUsuario().getCedula())
                                .email(estudiante.getUsuario().getEmail())
                                .promedioGeneral(Math.round(promedioGeneral * 100.0) / 100.0)
                                .totalCursosInscritos(totalCursos)
                                .cursosEnRiesgo(cursosEnRiesgo)
                                .cursosConRiesgo(cursosConRiesgo)
                                .build();

                estudiantesEnRiesgo.add(estudianteRiesgo);
                sumaPromediosRiesgo += promedioGeneral;
                contadorEstudiantesRiesgo++;
            }
        }

        // Calcular estadísticas generales
        double promedioGeneralRiesgo = contadorEstudiantesRiesgo > 0 ?
                sumaPromediosRiesgo / contadorEstudiantesRiesgo : 0.0;

        // Ordenar por promedio general (de menor a mayor - los más críticos primero)
        estudiantesEnRiesgo.sort((e1, e2) ->
                Double.compare(e1.getPromedioGeneral(), e2.getPromedioGeneral()));

        // Construir DTO final
        return EstudiantesEnRiesgoReporteDTO.builder()
                .totalEstudiantesEnRiesgo(contadorEstudiantesRiesgo)
                .promedioGeneralRiesgo(Math.round(promedioGeneralRiesgo * 100.0) / 100.0)
                .estudiantes(estudiantesEnRiesgo)
                .build();
    }

    @Override
    @Transactional
    public List<CalificacionResponseDTO> registrarBatch(CalificacionBatchRequestDTO request) {
        // Validar que el curso existe
        Curso curso = cursoRepository.findById(request.getCursoId())
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        List<CalificacionResponseDTO> calificacionesCreadas = new ArrayList<>();

        // Registrar cada calificación
        for (CalificacionBatchRequestDTO.CalificacionIndividualDTO calificacionDTO : request.getCalificaciones()) {
            // Validar inscripción
            Inscripcion inscripcion = inscripcionRepository.findById(calificacionDTO.getInscripcionId())
                    .orElseThrow(() -> new RuntimeException("Inscripción no encontrada"));

            // CORRECCIÓN 1: Usar EstadoInscripcion en lugar de Estado
            if (inscripcion.getEstado() != EstadoInscripcion.ACTIVO) {
                throw new RuntimeException("La inscripción no está activa");
            }

            if (!inscripcion.getCurso().getId().equals(request.getCursoId())) {
                throw new RuntimeException("La inscripción no pertenece al curso seleccionado");
            }

            // CORRECCIÓN 2 y 3: Convertir Double a BigDecimal
            BigDecimal notaBigDecimal = BigDecimal.valueOf(calificacionDTO.getNota());

            // Crear calificación
            Calificacion calificacion = Calificacion.builder()
                    .inscripcion(inscripcion)
                    .nombreEvaluacion(request.getNombreEvaluacion())
                    .nota(notaBigDecimal)  // Usar BigDecimal
                    .porcentaje(request.getPorcentaje())
                    .observaciones(request.getObservaciones())
                    .fechaCalificacion(LocalDate.now())
                    .build();

            Calificacion calificacionGuardada = calificacionRepository.save(calificacion);

            // Mapear a DTO - Convertir BigDecimal a Double
            CalificacionResponseDTO responseDTO = CalificacionResponseDTO.builder()
                    .id(calificacionGuardada.getId())
                    .inscripcionId(calificacionGuardada.getInscripcion().getId())
                    .nombreEvaluacion(calificacionGuardada.getNombreEvaluacion())
                    .nota(calificacionGuardada.getNota().doubleValue())  // Convertir a Double
                    .porcentaje(calificacionGuardada.getPorcentaje())
                    .observaciones(calificacionGuardada.getObservaciones())
                    .fechaCalificacion(calificacionGuardada.getFechaCalificacion())
                    .build();

            calificacionesCreadas.add(responseDTO);
        }

        return calificacionesCreadas;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> obtenerInscripcionesPorCurso(Long cursoId) {
        // Validar curso
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        // CORRECCIÓN 4: Usar EstadoInscripcion.ACTIVO
        List<Inscripcion> inscripciones = inscripcionRepository
                .findByCursoAndEstado(curso, EstadoInscripcion.ACTIVO);

        List<Map<String, Object>> resultado = new ArrayList<>();

        for (Inscripcion inscripcion : inscripciones) {
            Estudiante estudiante = inscripcion.getEstudiante();
            Usuario usuario = estudiante.getUsuario();

            Map<String, Object> estudianteInfo = new HashMap<>();
            estudianteInfo.put("inscripcionId", inscripcion.getId());
            estudianteInfo.put("estudianteId", estudiante.getId());
            estudianteInfo.put("codigoEstudiante", estudiante.getCodigoEstudiante());
            estudianteInfo.put("nombreCompleto", usuario.getNombre() + " " + usuario.getApellido());
            estudianteInfo.put("email", usuario.getEmail());

            resultado.add(estudianteInfo);
        }

        return resultado;
    }
}