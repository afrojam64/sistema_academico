package com.sistema.academico.aplicacion.servicio.implementacion;

import com.sistema.academico.aplicacion.dto.response.TasaAprobacionDTO;
import com.sistema.academico.aplicacion.servicio.ITasaAprobacionService;
import com.sistema.academico.infraestructura.repositorio.CalificacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación del servicio de Tasa de Aprobación
 */
@Service
@RequiredArgsConstructor
public class TasaAprobacionServiceImpl implements ITasaAprobacionService {

    private final CalificacionRepository calificacionRepository;

    @Override
    @Transactional(readOnly = true)
    public TasaAprobacionDTO generarReporteTasaAprobacion() {

        // 1. Resumen General
        TasaAprobacionDTO.ResumenGeneral resumenGeneral = calcularResumenGeneral();

        // 2. Tasa por Materia
        List<TasaAprobacionDTO.TasaAprobacionMateria> tasaPorMateria =
                calcularTasaPorMateria();

        // 3. Tasa por Curso
        List<TasaAprobacionDTO.TasaAprobacionCurso> tasaPorCurso =
                calcularTasaPorCurso();

        // 4. Tasa por Periodo
        List<TasaAprobacionDTO.TasaAprobacionPeriodo> tasaPorPeriodo =
                calcularTasaPorPeriodo();

        // 5. Tasa por Departamento
        List<TasaAprobacionDTO.TasaAprobacionDepartamento> tasaPorDepartamento =
                calcularTasaPorDepartamento();

        // Construir DTO final
        return TasaAprobacionDTO.builder()
                .resumenGeneral(resumenGeneral)
                .tasaPorMateria(tasaPorMateria)
                .tasaPorCurso(tasaPorCurso)
                .tasaPorPeriodo(tasaPorPeriodo)
                .tasaPorDepartamento(tasaPorDepartamento)
                .build();
    }

    /**
     * Calcula el resumen general de aprobación
     */
    private TasaAprobacionDTO.ResumenGeneral calcularResumenGeneral() {
        Long totalEvaluados = calificacionRepository.contarEstudiantesEvaluados();
        Long aprobados = calificacionRepository.contarEstudiantesAprobados();
        Long reprobados = calificacionRepository.contarEstudiantesReprobados();
        Double promedioGeneral = calificacionRepository.calcularPromedioGeneralSistema();

        // Calcular tasa de aprobación general
        Double tasaAprobacion = totalEvaluados > 0 ?
                (aprobados * 100.0) / totalEvaluados : 0.0;

        return TasaAprobacionDTO.ResumenGeneral.builder()
                .totalEstudiantesEvaluados(totalEvaluados != null ? totalEvaluados : 0L)
                .estudiantesAprobados(aprobados != null ? aprobados : 0L)
                .estudiantesReprobados(reprobados != null ? reprobados : 0L)
                .tasaAprobacionGeneral(Math.round(tasaAprobacion * 100.0) / 100.0)
                .promedioGeneralSistema(promedioGeneral != null ?
                        Math.round(promedioGeneral * 100.0) / 100.0 : 0.0)
                .build();
    }

    /**
     * Calcula tasa de aprobación por materia
     */
    private List<TasaAprobacionDTO.TasaAprobacionMateria> calcularTasaPorMateria() {
        List<Object[]> resultados = calificacionRepository.obtenerTasaAprobacionPorMateria();
        List<TasaAprobacionDTO.TasaAprobacionMateria> tasas = new ArrayList<>();

        int ranking = 1;
        for (Object[] fila : resultados) {
            String nombreMateria = (String) fila[0];
            String codigoMateria = (String) fila[1];
            String nombreDepartamento = (String) fila[2];
            Long totalEstudiantes = ((Number) fila[3]).longValue();
            Long aprobados = ((Number) fila[4]).longValue();
            Long reprobados = ((Number) fila[5]).longValue();
            Double promedioGeneral = fila[6] != null ? ((Number) fila[6]).doubleValue() : 0.0;

            // Calcular tasa de aprobación
            Double tasaAprobacion = totalEstudiantes > 0 ?
                    (aprobados * 100.0) / totalEstudiantes : 0.0;

            TasaAprobacionDTO.TasaAprobacionMateria tasa =
                    TasaAprobacionDTO.TasaAprobacionMateria.builder()
                            .nombreMateria(nombreMateria)
                            .codigoMateria(codigoMateria)
                            .nombreDepartamento(nombreDepartamento)
                            .totalEstudiantes(totalEstudiantes)
                            .aprobados(aprobados)
                            .reprobados(reprobados)
                            .tasaAprobacion(Math.round(tasaAprobacion * 100.0) / 100.0)
                            .promedioGeneral(Math.round(promedioGeneral * 100.0) / 100.0)
                            .ranking(ranking++)
                            .build();

            tasas.add(tasa);
        }

        return tasas;
    }

    /**
     * Calcula tasa de aprobación por curso
     */
    private List<TasaAprobacionDTO.TasaAprobacionCurso> calcularTasaPorCurso() {
        List<Object[]> resultados = calificacionRepository.obtenerTasaAprobacionPorCurso();
        List<TasaAprobacionDTO.TasaAprobacionCurso> tasas = new ArrayList<>();

        for (Object[] fila : resultados) {
            String codigoCurso = (String) fila[0];
            String nombreMateria = (String) fila[1];
            String nombreProfesor = (String) fila[2];
            String periodo = (String) fila[3];
            Long totalEstudiantes = ((Number) fila[4]).longValue();
            Long aprobados = ((Number) fila[5]).longValue();
            Long reprobados = ((Number) fila[6]).longValue();
            Double promedioGeneral = fila[7] != null ? ((Number) fila[7]).doubleValue() : 0.0;

            // Calcular tasa de aprobación
            Double tasaAprobacion = totalEstudiantes > 0 ?
                    (aprobados * 100.0) / totalEstudiantes : 0.0;

            TasaAprobacionDTO.TasaAprobacionCurso tasa =
                    TasaAprobacionDTO.TasaAprobacionCurso.builder()
                            .codigoCurso(codigoCurso)
                            .nombreMateria(nombreMateria)
                            .nombreProfesor(nombreProfesor)
                            .periodo(periodo)
                            .totalEstudiantes(totalEstudiantes)
                            .aprobados(aprobados)
                            .reprobados(reprobados)
                            .tasaAprobacion(Math.round(tasaAprobacion * 100.0) / 100.0)
                            .promedioGeneral(Math.round(promedioGeneral * 100.0) / 100.0)
                            .build();

            tasas.add(tasa);
        }

        return tasas;
    }

    /**
     * Calcula tasa de aprobación por periodo con tendencias
     */
    private List<TasaAprobacionDTO.TasaAprobacionPeriodo> calcularTasaPorPeriodo() {
        List<Object[]> resultados = calificacionRepository.obtenerTasaAprobacionPorPeriodo();
        List<TasaAprobacionDTO.TasaAprobacionPeriodo> tasas = new ArrayList<>();

        Double tasaAnterior = null;

        for (Object[] fila : resultados) {
            String periodo = (String) fila[0];
            Long totalEstudiantes = ((Number) fila[1]).longValue();
            Long aprobados = ((Number) fila[2]).longValue();
            Long reprobados = ((Number) fila[3]).longValue();
            Double promedioGeneral = fila[4] != null ? ((Number) fila[4]).doubleValue() : 0.0;

            // Calcular tasa de aprobación
            Double tasaAprobacion = totalEstudiantes > 0 ?
                    (aprobados * 100.0) / totalEstudiantes : 0.0;

            // Calcular tendencia (comparado con periodo anterior)
            Double tendencia = 0.0;
            String tendenciaIndicador = "ESTABLE";

            if (tasaAnterior != null) {
                tendencia = tasaAprobacion - tasaAnterior;
                if (tendencia > 2.0) {
                    tendenciaIndicador = "MEJORA";
                } else if (tendencia < -2.0) {
                    tendenciaIndicador = "DISMINUYE";
                }
            }

            TasaAprobacionDTO.TasaAprobacionPeriodo tasa =
                    TasaAprobacionDTO.TasaAprobacionPeriodo.builder()
                            .periodo(periodo)
                            .totalEstudiantes(totalEstudiantes)
                            .aprobados(aprobados)
                            .reprobados(reprobados)
                            .tasaAprobacion(Math.round(tasaAprobacion * 100.0) / 100.0)
                            .promedioGeneral(Math.round(promedioGeneral * 100.0) / 100.0)
                            .tendencia(Math.round(tendencia * 100.0) / 100.0)
                            .tendenciaIndicador(tendenciaIndicador)
                            .build();

            tasas.add(tasa);
            tasaAnterior = tasaAprobacion;
        }

        return tasas;
    }

    /**
     * Calcula tasa de aprobación por departamento
     */
    private List<TasaAprobacionDTO.TasaAprobacionDepartamento> calcularTasaPorDepartamento() {
        List<Object[]> resultados = calificacionRepository.obtenerTasaAprobacionPorDepartamento();
        List<TasaAprobacionDTO.TasaAprobacionDepartamento> tasas = new ArrayList<>();

        int ranking = 1;
        for (Object[] fila : resultados) {
            String nombreDepartamento = (String) fila[0];
            Long totalEstudiantes = ((Number) fila[1]).longValue();
            Long aprobados = ((Number) fila[2]).longValue();
            Long reprobados = ((Number) fila[3]).longValue();
            Double promedioGeneral = fila[4] != null ? ((Number) fila[4]).doubleValue() : 0.0;
            Long totalMaterias = ((Number) fila[5]).longValue();

            // Calcular tasa de aprobación
            Double tasaAprobacion = totalEstudiantes > 0 ?
                    (aprobados * 100.0) / totalEstudiantes : 0.0;

            TasaAprobacionDTO.TasaAprobacionDepartamento tasa =
                    TasaAprobacionDTO.TasaAprobacionDepartamento.builder()
                            .nombreDepartamento(nombreDepartamento)
                            .totalEstudiantes(totalEstudiantes)
                            .aprobados(aprobados)
                            .reprobados(reprobados)
                            .tasaAprobacion(Math.round(tasaAprobacion * 100.0) / 100.0)
                            .promedioGeneral(Math.round(promedioGeneral * 100.0) / 100.0)
                            .ranking(ranking++)
                            .totalMaterias(totalMaterias)
                            .build();

            tasas.add(tasa);
        }

        return tasas;
    }
}