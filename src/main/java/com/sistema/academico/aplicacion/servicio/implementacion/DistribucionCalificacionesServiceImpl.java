package com.sistema.academico.aplicacion.servicio.implementacion;

import com.sistema.academico.aplicacion.dto.response.DistribucionCalificacionesDTO;
import com.sistema.academico.aplicacion.servicio.IDistribucionCalificacionesService;
import com.sistema.academico.infraestructura.repositorio.CalificacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación del servicio de Distribución de Calificaciones
 */
@Service
@RequiredArgsConstructor
public class DistribucionCalificacionesServiceImpl implements IDistribucionCalificacionesService {

    private final CalificacionRepository calificacionRepository;

    @Override
    @Transactional(readOnly = true)
    public DistribucionCalificacionesDTO generarReporteDistribucion() {

        // 1. Estadísticas Generales
        DistribucionCalificacionesDTO.EstadisticasGenerales estadisticas = calcularEstadisticasGenerales();

        // 2. Distribución por Rangos
        DistribucionCalificacionesDTO.DistribucionRangos distribucionRangos = calcularDistribucionRangos();

        // 3. Distribución por Departamento
        List<DistribucionCalificacionesDTO.DistribucionDepartamento> porDepartamento =
                calcularDistribucionPorDepartamento();

        // 4. Distribución por Materia (Top 10)
        List<DistribucionCalificacionesDTO.DistribucionMateria> topMaterias =
                calcularDistribucionPorMateria();

        // 5. Distribución por Periodo
        List<DistribucionCalificacionesDTO.DistribucionPeriodo> porPeriodo =
                calcularDistribucionPorPeriodo();

        // Construir DTO final
        return DistribucionCalificacionesDTO.builder()
                .estadisticasGenerales(estadisticas)
                .distribucionRangos(distribucionRangos)
                .distribucionPorDepartamento(porDepartamento)
                .topMaterias(topMaterias)
                .distribucionPorPeriodo(porPeriodo)
                .build();
    }

    /**
     * Calcula estadísticas generales del sistema
     */
    private DistribucionCalificacionesDTO.EstadisticasGenerales calcularEstadisticasGenerales() {
        Long totalCalificaciones = calificacionRepository.contarTotalCalificaciones();
        Double promedioGeneral = calificacionRepository.calcularPromedioGeneralSistema();
        Double notaMasAlta = calificacionRepository.obtenerNotaMasAlta();
        Double notaMasBaja = calificacionRepository.obtenerNotaMasBaja();

        return DistribucionCalificacionesDTO.EstadisticasGenerales.builder()
                .totalCalificaciones(totalCalificaciones != null ? totalCalificaciones : 0L)
                .promedioGeneral(promedioGeneral != null ?
                        Math.round(promedioGeneral * 100.0) / 100.0 : 0.0)
                .notaMasAlta(notaMasAlta != null ?
                        Math.round(notaMasAlta * 10.0) / 10.0 : 0.0)
                .notaMasBaja(notaMasBaja != null ?
                        Math.round(notaMasBaja * 10.0) / 10.0 : 0.0)
                .desviacionEstandar(0.0) // Opcional: implementar cálculo
                .build();
    }

    /**
     * Calcula distribución por rangos de notas
     */
    private DistribucionCalificacionesDTO.DistribucionRangos calcularDistribucionRangos() {
        Long totalCalificaciones = calificacionRepository.contarTotalCalificaciones();
        Long rango0a2 = calificacionRepository.contarCalificacionesRango0a2();
        Long rango3a3 = calificacionRepository.contarCalificacionesRango3a3();
        Long rango4a5 = calificacionRepository.contarCalificacionesRango4a5();

        // Calcular porcentajes
        double porcentaje0a2 = totalCalificaciones > 0 ?
                (rango0a2 * 100.0) / totalCalificaciones : 0.0;
        double porcentaje3a3 = totalCalificaciones > 0 ?
                (rango3a3 * 100.0) / totalCalificaciones : 0.0;
        double porcentaje4a5 = totalCalificaciones > 0 ?
                (rango4a5 * 100.0) / totalCalificaciones : 0.0;

        // Crear rangos
        DistribucionCalificacionesDTO.RangoNota rangoReprobado =
                DistribucionCalificacionesDTO.RangoNota.builder()
                        .rangoLabel("0.0 - 2.9")
                        .categoria("Reprobado")
                        .cantidad(rango0a2 != null ? rango0a2 : 0L)
                        .porcentaje(Math.round(porcentaje0a2 * 100.0) / 100.0)
                        .color("#ef4444")
                        .build();

        DistribucionCalificacionesDTO.RangoNota rangoAceptable =
                DistribucionCalificacionesDTO.RangoNota.builder()
                        .rangoLabel("3.0 - 3.9")
                        .categoria("Aceptable")
                        .cantidad(rango3a3 != null ? rango3a3 : 0L)
                        .porcentaje(Math.round(porcentaje3a3 * 100.0) / 100.0)
                        .color("#f59e0b")
                        .build();

        DistribucionCalificacionesDTO.RangoNota rangoSobresaliente =
                DistribucionCalificacionesDTO.RangoNota.builder()
                        .rangoLabel("4.0 - 5.0")
                        .categoria("Sobresaliente")
                        .cantidad(rango4a5 != null ? rango4a5 : 0L)
                        .porcentaje(Math.round(porcentaje4a5 * 100.0) / 100.0)
                        .color("#10b981")
                        .build();

        return DistribucionCalificacionesDTO.DistribucionRangos.builder()
                .rango0a2(rangoReprobado)
                .rango3a3(rangoAceptable)
                .rango4a5(rangoSobresaliente)
                .build();
    }

    /**
     * Calcula distribución por departamento
     */
    private List<DistribucionCalificacionesDTO.DistribucionDepartamento> calcularDistribucionPorDepartamento() {
        List<Object[]> resultados = calificacionRepository.obtenerDistribucionPorDepartamento();
        List<DistribucionCalificacionesDTO.DistribucionDepartamento> distribucion = new ArrayList<>();

        for (Object[] fila : resultados) {
            String nombreDepartamento = (String) fila[0];
            Long totalCalificaciones = ((Number) fila[1]).longValue();
            Double promedioNotas = fila[2] != null ? ((Number) fila[2]).doubleValue() : 0.0;
            Long cantidadRango0a2 = ((Number) fila[3]).longValue();
            Long cantidadRango3a3 = ((Number) fila[4]).longValue();
            Long cantidadRango4a5 = ((Number) fila[5]).longValue();

            // Calcular porcentaje de aprobación
            Long aprobados = cantidadRango3a3 + cantidadRango4a5;
            Double porcentajeAprobacion = totalCalificaciones > 0 ?
                    (aprobados * 100.0) / totalCalificaciones : 0.0;

            DistribucionCalificacionesDTO.DistribucionDepartamento dept =
                    DistribucionCalificacionesDTO.DistribucionDepartamento.builder()
                            .nombreDepartamento(nombreDepartamento)
                            .totalCalificaciones(totalCalificaciones)
                            .promedioNotas(Math.round(promedioNotas * 100.0) / 100.0)
                            .cantidadRango0a2(cantidadRango0a2)
                            .cantidadRango3a3(cantidadRango3a3)
                            .cantidadRango4a5(cantidadRango4a5)
                            .porcentajeAprobacion(Math.round(porcentajeAprobacion * 100.0) / 100.0)
                            .build();

            distribucion.add(dept);
        }

        return distribucion;
    }

    /**
     * Calcula distribución por materia (Top 10)
     */
    private List<DistribucionCalificacionesDTO.DistribucionMateria> calcularDistribucionPorMateria() {
        List<Object[]> resultados = calificacionRepository.obtenerDistribucionPorMateria();
        List<DistribucionCalificacionesDTO.DistribucionMateria> distribucion = new ArrayList<>();

        // Limitar a top 10
        int limite = Math.min(10, resultados.size());

        for (int i = 0; i < limite; i++) {
            Object[] fila = resultados.get(i);
            String nombreMateria = (String) fila[0];
            String nombreDepartamento = (String) fila[1];
            Long totalCalificaciones = ((Number) fila[2]).longValue();
            Double promedioNotas = fila[3] != null ? ((Number) fila[3]).doubleValue() : 0.0;
            Long cantidadRango0a2 = ((Number) fila[4]).longValue();
            Long cantidadRango3a3 = ((Number) fila[5]).longValue();
            Long cantidadRango4a5 = ((Number) fila[6]).longValue();

            // Calcular porcentaje de aprobación
            Long aprobados = cantidadRango3a3 + cantidadRango4a5;
            Double porcentajeAprobacion = totalCalificaciones > 0 ?
                    (aprobados * 100.0) / totalCalificaciones : 0.0;

            DistribucionCalificacionesDTO.DistribucionMateria materia =
                    DistribucionCalificacionesDTO.DistribucionMateria.builder()
                            .nombreMateria(nombreMateria)
                            .nombreDepartamento(nombreDepartamento)
                            .totalCalificaciones(totalCalificaciones)
                            .promedioNotas(Math.round(promedioNotas * 100.0) / 100.0)
                            .cantidadRango0a2(cantidadRango0a2)
                            .cantidadRango3a3(cantidadRango3a3)
                            .cantidadRango4a5(cantidadRango4a5)
                            .porcentajeAprobacion(Math.round(porcentajeAprobacion * 100.0) / 100.0)
                            .build();

            distribucion.add(materia);
        }

        return distribucion;
    }

    /**
     * Calcula distribución por periodo académico
     */
    private List<DistribucionCalificacionesDTO.DistribucionPeriodo> calcularDistribucionPorPeriodo() {
        List<Object[]> resultados = calificacionRepository.obtenerDistribucionPorPeriodo();
        List<DistribucionCalificacionesDTO.DistribucionPeriodo> distribucion = new ArrayList<>();

        for (Object[] fila : resultados) {
            String periodo = (String) fila[0];
            Long totalCalificaciones = ((Number) fila[1]).longValue();
            Double promedioNotas = fila[2] != null ? ((Number) fila[2]).doubleValue() : 0.0;
            Long cantidadRango0a2 = ((Number) fila[3]).longValue();
            Long cantidadRango3a3 = ((Number) fila[4]).longValue();
            Long cantidadRango4a5 = ((Number) fila[5]).longValue();

            // Calcular porcentaje de aprobación
            Long aprobados = cantidadRango3a3 + cantidadRango4a5;
            Double porcentajeAprobacion = totalCalificaciones > 0 ?
                    (aprobados * 100.0) / totalCalificaciones : 0.0;

            DistribucionCalificacionesDTO.DistribucionPeriodo per =
                    DistribucionCalificacionesDTO.DistribucionPeriodo.builder()
                            .periodo(periodo)
                            .totalCalificaciones(totalCalificaciones)
                            .promedioNotas(Math.round(promedioNotas * 100.0) / 100.0)
                            .cantidadRango0a2(cantidadRango0a2)
                            .cantidadRango3a3(cantidadRango3a3)
                            .cantidadRango4a5(cantidadRango4a5)
                            .porcentajeAprobacion(Math.round(porcentajeAprobacion * 100.0) / 100.0)
                            .build();

            distribucion.add(per);
        }

        return distribucion;
    }
}