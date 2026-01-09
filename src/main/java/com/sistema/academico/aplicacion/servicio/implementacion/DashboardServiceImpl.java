package com.sistema.academico.aplicacion.servicio.implementacion;

import com.sistema.academico.aplicacion.dto.response.DashboardEjecutivoDTO;
import com.sistema.academico.aplicacion.servicio.IDashboardService;
import com.sistema.academico.dominio.enumeracion.Estado;
import com.sistema.academico.dominio.enumeracion.EstadoInscripcion;
import com.sistema.academico.infraestructura.repositorio.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementación del servicio de Dashboard Ejecutivo
 */
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements IDashboardService {

    private final EstudianteRepository estudianteRepository;
    private final ProfesorRepository profesorRepository;
    private final CursoRepository cursoRepository;
    private final MateriaRepository materiaRepository;
    private final DepartamentoRepository departamentoRepository;
    private final InscripcionRepository inscripcionRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardEjecutivoDTO generarDashboardEjecutivo() {

        // 1. Resumen General
        DashboardEjecutivoDTO.ResumenGeneral resumenGeneral = calcularResumenGeneral();

        // 2. Periodo Actual
        DashboardEjecutivoDTO.PeriodoActual periodoActual = calcularPeriodoActual();

        // 3. Ocupación de Cursos
        DashboardEjecutivoDTO.OcupacionCursos ocupacionCursos = calcularOcupacionCursos();

        // 4. Top 5 Departamentos
        List<DashboardEjecutivoDTO.DepartamentoTop> topDepartamentos = calcularTopDepartamentos();

        // 5. Alertas
        DashboardEjecutivoDTO.Alertas alertas = calcularAlertas();

        // Construir DTO final
        return DashboardEjecutivoDTO.builder()
                .resumenGeneral(resumenGeneral)
                .periodoActual(periodoActual)
                .ocupacionCursos(ocupacionCursos)
                .topDepartamentos(topDepartamentos)
                .alertas(alertas)
                .build();
    }

    /**
     * Calcula el resumen general del sistema
     */
    private DashboardEjecutivoDTO.ResumenGeneral calcularResumenGeneral() {
        long totalEstudiantes = estudianteRepository.countByEstado(Estado.ACTIVO);
        long totalProfesores = profesorRepository.countByEstado(Estado.ACTIVO);
        long totalCursos = cursoRepository.countByEstado(Estado.ACTIVO);
        long totalMaterias = materiaRepository.countByEstado(Estado.ACTIVO);
        long totalDepartamentos = departamentoRepository.count();
        long totalInscripciones = inscripcionRepository.countByEstado(EstadoInscripcion.ACTIVO);

        return DashboardEjecutivoDTO.ResumenGeneral.builder()
                .totalEstudiantes((int) totalEstudiantes)
                .totalProfesores((int) totalProfesores)
                .totalCursos((int) totalCursos)
                .totalMaterias((int) totalMaterias)
                .totalDepartamentos((int) totalDepartamentos)
                .totalInscripciones((int) totalInscripciones)
                .build();
    }

    /**
     * Calcula información del periodo actual
     */
    private DashboardEjecutivoDTO.PeriodoActual calcularPeriodoActual() {
        // Obtener el periodo más reciente
        List<String> periodos = cursoRepository.findPeriodosActivos();
        String periodoActual = periodos.isEmpty() ? "Sin periodo" : periodos.get(0);

        // Contar cursos ofertados en el periodo
        long cursosOfertados = cursoRepository.countCursosPorPeriodo(periodoActual);

        // Contar estudiantes únicos inscritos
        long estudiantesInscritos = inscripcionRepository.countEstudiantesUnicosPorPeriodo(periodoActual);

        // Contar inscripciones totales
        long inscripcionesTotales = inscripcionRepository.countInscripcionesActivasPorPeriodo(periodoActual);

        // Calcular cupos disponibles
        Integer totalCuposOfrecidos = cursoRepository.calcularTotalCuposOfrecidos();
        Integer totalCuposOcupados = cursoRepository.calcularTotalCuposOcupados();
        int cuposDisponibles = (totalCuposOfrecidos != null ? totalCuposOfrecidos : 0) -
                (totalCuposOcupados != null ? totalCuposOcupados : 0);

        // Calcular promedio de inscripciones por estudiante
        double promedioInscripciones = estudiantesInscritos > 0 ?
                (double) inscripcionesTotales / estudiantesInscritos : 0.0;

        return DashboardEjecutivoDTO.PeriodoActual.builder()
                .periodo(periodoActual)
                .cursosOfertados((int) cursosOfertados)
                .estudiantesInscritos((int) estudiantesInscritos)
                .inscripcionesTotales((int) inscripcionesTotales)
                .cuposDisponibles(cuposDisponibles)
                .promedioInscripcionesPorEstudiante(Math.round(promedioInscripciones * 100.0) / 100.0)
                .build();
    }

    /**
     * Calcula estadísticas de ocupación de cursos
     */
    private DashboardEjecutivoDTO.OcupacionCursos calcularOcupacionCursos() {
        long cursosCompletos = cursoRepository.countCursosCompletos();
        long cursosAltaOcupacion = cursoRepository.countCursosAltaOcupacion();
        long cursosMediaOcupacion = cursoRepository.countCursosMediaOcupacion();
        long cursosBajaOcupacion = cursoRepository.countCursosBajaOcupacion();

        Double ocupacionPromedio = cursoRepository.calcularPromedioOcupacion();
        Integer totalCuposOfrecidos = cursoRepository.calcularTotalCuposOfrecidos();
        Integer totalCuposOcupados = cursoRepository.calcularTotalCuposOcupados();

        int cuposDisponibles = (totalCuposOfrecidos != null ? totalCuposOfrecidos : 0) -
                (totalCuposOcupados != null ? totalCuposOcupados : 0);

        return DashboardEjecutivoDTO.OcupacionCursos.builder()
                .cursosCompletos((int) cursosCompletos)
                .cursosAltaOcupacion((int) cursosAltaOcupacion)
                .cursosMediaOcupacion((int) cursosMediaOcupacion)
                .cursosBajaOcupacion((int) cursosBajaOcupacion)
                .ocupacionPromedio(ocupacionPromedio != null ?
                        Math.round(ocupacionPromedio * 100.0) / 100.0 : 0.0)
                .totalCuposOfrecidos(totalCuposOfrecidos != null ? totalCuposOfrecidos : 0)
                .totalCuposOcupados(totalCuposOcupados != null ? totalCuposOcupados : 0)
                .cuposDisponibles(cuposDisponibles)
                .build();
    }

    /**
     * Calcula top 5 departamentos por inscripciones
     */
    private List<DashboardEjecutivoDTO.DepartamentoTop> calcularTopDepartamentos() {
        List<Object[]> inscripcionesPorDept = inscripcionRepository.countInscripcionesPorDepartamento();
        List<Object[]> estudiantesPorDept = inscripcionRepository.countEstudiantesUnicosPorDepartamento();
        List<Object[]> cursosPorDept = inscripcionRepository.countCursosActivosPorDepartamento();

        // Calcular total de inscripciones para porcentajes
        long totalInscripciones = inscripcionRepository.countByEstado(EstadoInscripcion.ACTIVO);

        List<DashboardEjecutivoDTO.DepartamentoTop> topDepartamentos = new ArrayList<>();

        // Limitar a top 5
        int limite = Math.min(5, inscripcionesPorDept.size());

        for (int i = 0; i < limite; i++) {
            Object[] inscripcionData = inscripcionesPorDept.get(i);
            String nombreDept = (String) inscripcionData[0];
            Long totalInscripDept = (Long) inscripcionData[1];

            // Buscar estudiantes y cursos del departamento
            Long totalEstudiantes = buscarEnResultado(estudiantesPorDept, nombreDept);
            Long totalCursos = buscarEnResultado(cursosPorDept, nombreDept);

            // Calcular porcentaje
            double porcentaje = totalInscripciones > 0 ?
                    (totalInscripDept * 100.0) / totalInscripciones : 0.0;

            DashboardEjecutivoDTO.DepartamentoTop deptTop =
                    DashboardEjecutivoDTO.DepartamentoTop.builder()
                            .nombreDepartamento(nombreDept)
                            .totalInscripciones(totalInscripDept.intValue())
                            .totalEstudiantes(totalEstudiantes.intValue())
                            .totalCursos(totalCursos.intValue())
                            .porcentaje(Math.round(porcentaje * 100.0) / 100.0)
                            .build();

            topDepartamentos.add(deptTop);
        }

        return topDepartamentos;
    }

    /**
     * Busca un valor en un resultado de query agrupado
     */
    private Long buscarEnResultado(List<Object[]> resultado, String clave) {
        for (Object[] fila : resultado) {
            if (fila[0].equals(clave)) {
                return (Long) fila[1];
            }
        }
        return 0L;
    }

    /**
     * Calcula alertas del sistema
     */
    private DashboardEjecutivoDTO.Alertas calcularAlertas() {
        // Estudiantes en riesgo (ya existe en tu sistema)
        // Aquí usamos un query aproximado, ajustar según tu lógica
        Long estudiantesSinInscripciones = estudianteRepository.countEstudiantesActivosSinInscripcionesActivas();

        // Cursos con baja ocupación
        long cursosBajaOcupacion = cursoRepository.countCursosBajaOcupacion();

        return DashboardEjecutivoDTO.Alertas.builder()
                .estudiantesEnRiesgo(0) // Implementar según tu lógica de riesgo
                .estudiantesSinInscripciones(estudiantesSinInscripciones != null ?
                        estudiantesSinInscripciones.intValue() : 0)
                .cursosConBajaOcupacion((int) cursosBajaOcupacion)
                .build();
    }
}