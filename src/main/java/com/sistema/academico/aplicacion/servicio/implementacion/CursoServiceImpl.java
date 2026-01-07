package com.sistema.academico.aplicacion.servicio.implementacion;

import com.sistema.academico.aplicacion.dto.request.CursoRequestDTO;
import com.sistema.academico.aplicacion.dto.response.CursoResponseDTO;
import com.sistema.academico.aplicacion.dto.response.CursosActivosReporteDTO;
import com.sistema.academico.aplicacion.dto.response.OcupacionCursosReporteDTO;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    /**
     * Generar reporte de cursos activos
     */
    @Override
    @Transactional(readOnly = true)
    public CursosActivosReporteDTO generarReporteCursosActivos() {

        // Obtener todos los cursos activos
        List<Curso> cursosActivos = cursoRepository.findByEstado(Estado.ACTIVO);

        // Variables para estadísticas
        int totalCursosActivos = cursosActivos.size();
        int cursosConCupoCompleto = 0;
        int cursosBajaOcupacion = 0;
        int totalEstudiantesInscritos = 0;
        int totalCuposDisponibles = 0;
        double sumaOcupaciones = 0.0;

        // Lista de cursos
        List<CursosActivosReporteDTO.CursoActivo> cursosData = new ArrayList<>();

        // Procesar cada curso
        for (Curso curso : cursosActivos) {
            // Calcular estadísticas de ocupación
            int cupoMaximo = curso.getCupoMaximo();
            int cupoActual = curso.getCupoActual();
            int cuposDisponibles = cupoMaximo - cupoActual;
            double porcentajeOcupacion = cupoMaximo > 0 ?
                    (cupoActual * 100.0) / cupoMaximo : 0.0;

            // Determinar estado de ocupación
            String estadoOcupacion;
            if (cupoActual >= cupoMaximo) {
                estadoOcupacion = "COMPLETO";
                cursosConCupoCompleto++;
            } else if (porcentajeOcupacion >= 80) {
                estadoOcupacion = "ALTO";
            } else if (porcentajeOcupacion >= 50) {
                estadoOcupacion = "MEDIO";
            } else {
                estadoOcupacion = "BAJO";
                cursosBajaOcupacion++;
            }

            // Acumular estadísticas
            totalEstudiantesInscritos += cupoActual;
            totalCuposDisponibles += cuposDisponibles;
            sumaOcupaciones += porcentajeOcupacion;

            // Crear DTO del curso
            CursosActivosReporteDTO.CursoActivo cursoData =
                    CursosActivosReporteDTO.CursoActivo.builder()
                            .cursoId(curso.getId())
                            .nombreCurso(curso.getNombre())
                            .codigoCurso(curso.getCodigo())
                            .periodo(curso.getPeriodo())
                            .fechaInicio(curso.getFechaInicio().format(
                                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                            .fechaFin(curso.getFechaFin().format(
                                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                            .nombreMateria(curso.getMateria().getNombre())
                            .codigoMateria(curso.getMateria().getCodigo())
                            .creditos(curso.getMateria().getCreditos())
                            .nombreDepartamento(curso.getMateria().getDepartamento().getNombre())
                            .nombreProfesor(curso.getProfesor().getUsuario().getNombre() + " " +
                                    curso.getProfesor().getUsuario().getApellido())
                            .emailProfesor(curso.getProfesor().getUsuario().getEmail())
                            .cupoMaximo(cupoMaximo)
                            .cupoActual(cupoActual)
                            .cuposDisponibles(cuposDisponibles)
                            .porcentajeOcupacion(Math.round(porcentajeOcupacion * 100.0) / 100.0)
                            .estadoOcupacion(estadoOcupacion)
                            .estado(curso.getEstado().name())
                            .build();

            cursosData.add(cursoData);
        }

        // Calcular promedio de ocupación
        double promedioOcupacion = totalCursosActivos > 0 ?
                sumaOcupaciones / totalCursosActivos : 0.0;

        // Ordenar cursos por porcentaje de ocupación (mayor a menor)
        cursosData.sort((c1, c2) ->
                Double.compare(c2.getPorcentajeOcupacion(), c1.getPorcentajeOcupacion()));

        // Construir DTO final
        return CursosActivosReporteDTO.builder()
                .totalCursosActivos(totalCursosActivos)
                .promedioOcupacion(Math.round(promedioOcupacion * 100.0) / 100.0)
                .cursosConCupoCompleto(cursosConCupoCompleto)
                .cursosBajaOcupacion(cursosBajaOcupacion)
                .totalEstudiantesInscritos(totalEstudiantesInscritos)
                .totalCuposDisponibles(totalCuposDisponibles)
                .cursos(cursosData)
                .build();
    }

    /**
     * Generar reporte de análisis de ocupación de cursos
     */
    @Override
    @Transactional(readOnly = true)
    public OcupacionCursosReporteDTO generarReporteOcupacionCursos() {

        // Obtener todos los cursos activos
        List<Curso> cursosActivos = cursoRepository.findByEstado(Estado.ACTIVO);

        // Variables para estadísticas generales
        int totalCursos = cursosActivos.size();
        int totalCuposOfrecidos = 0;
        int totalCuposOcupados = 0;
        double sumaOcupaciones = 0.0;

        // Contadores para distribución
        int rango0a25 = 0;
        int rango25a50 = 0;
        int rango50a75 = 0;
        int rango75a100 = 0;
        int rango100 = 0;

        // Listas para tops
        List<OcupacionCursosReporteDTO.CursoDetalle> cursosCompletos = new ArrayList<>();
        List<OcupacionCursosReporteDTO.CursoDetalle> cursosMasDemandados = new ArrayList<>();
        List<OcupacionCursosReporteDTO.CursoDetalle> cursosMenosDemandados = new ArrayList<>();

        // Map para agrupar por departamento
        Map<String, List<Curso>> cursosPorDepartamento = new HashMap<>();

        // Procesar cada curso
        for (Curso curso : cursosActivos) {
            int cupoMaximo = curso.getCupoMaximo();
            int cupoActual = curso.getCupoActual();
            double porcentajeOcupacion = cupoMaximo > 0 ?
                    (cupoActual * 100.0) / cupoMaximo : 0.0;

            // Acumular estadísticas
            totalCuposOfrecidos += cupoMaximo;
            totalCuposOcupados += cupoActual;
            sumaOcupaciones += porcentajeOcupacion;

            // Clasificar por rango
            if (porcentajeOcupacion == 100.0) {
                rango100++;
            } else if (porcentajeOcupacion >= 75) {
                rango75a100++;
            } else if (porcentajeOcupacion >= 50) {
                rango50a75++;
            } else if (porcentajeOcupacion >= 25) {
                rango25a50++;
            } else {
                rango0a25++;
            }

            // Agrupar por departamento
            String nombreDepartamento = curso.getMateria().getDepartamento().getNombre();
            cursosPorDepartamento.computeIfAbsent(nombreDepartamento, k -> new ArrayList<>()).add(curso);

            // Crear detalle del curso
            OcupacionCursosReporteDTO.CursoDetalle detalle =
                    OcupacionCursosReporteDTO.CursoDetalle.builder()
                            .cursoId(curso.getId())
                            .nombreCurso(curso.getNombre())
                            .codigoCurso(curso.getCodigo())
                            .nombreMateria(curso.getMateria().getNombre())
                            .nombreDepartamento(nombreDepartamento)
                            .nombreProfesor(curso.getProfesor().getUsuario().getNombre() + " " +
                                    curso.getProfesor().getUsuario().getApellido())
                            .periodo(curso.getPeriodo())
                            .cupoMaximo(cupoMaximo)
                            .cupoActual(cupoActual)
                            .porcentajeOcupacion(Math.round(porcentajeOcupacion * 100.0) / 100.0)
                            .build();

            // Clasificar en tops
            if (porcentajeOcupacion == 100.0) {
                cursosCompletos.add(detalle);
            } else if (porcentajeOcupacion >= 90) {
                cursosMasDemandados.add(detalle);
            } else if (porcentajeOcupacion < 25) {
                cursosMenosDemandados.add(detalle);
            }
        }

        // Calcular estadísticas generales
        double promedioOcupacionGlobal = totalCursos > 0 ?
                sumaOcupaciones / totalCursos : 0.0;
        int totalCuposDisponibles = totalCuposOfrecidos - totalCuposOcupados;

        // Crear distribución
        OcupacionCursosReporteDTO.DistribucionOcupacion distribucion =
                OcupacionCursosReporteDTO.DistribucionOcupacion.builder()
                        .rango0a25(rango0a25)
                        .rango25a50(rango25a50)
                        .rango50a75(rango50a75)
                        .rango75a100(rango75a100)
                        .rango100(rango100)
                        .porcentaje0a25(totalCursos > 0 ? (rango0a25 * 100.0) / totalCursos : 0.0)
                        .porcentaje25a50(totalCursos > 0 ? (rango25a50 * 100.0) / totalCursos : 0.0)
                        .porcentaje50a75(totalCursos > 0 ? (rango50a75 * 100.0) / totalCursos : 0.0)
                        .porcentaje75a100(totalCursos > 0 ? (rango75a100 * 100.0) / totalCursos : 0.0)
                        .porcentaje100(totalCursos > 0 ? (rango100 * 100.0) / totalCursos : 0.0)
                        .build();

        // Procesar departamentos
        List<OcupacionCursosReporteDTO.OcupacionPorDepartamento> departamentos = new ArrayList<>();

        for (Map.Entry<String, List<Curso>> entry : cursosPorDepartamento.entrySet()) {
            String nombreDepartamento = entry.getKey();
            List<Curso> cursosDepartamento = entry.getValue();

            int totalCursosDept = cursosDepartamento.size();
            int cursosCompletosDept = 0;
            int totalEstudiantesDept = 0;
            int totalCuposDept = 0;
            double sumaOcupacionesDept = 0.0;

            for (Curso curso : cursosDepartamento) {
                int cupoActual = curso.getCupoActual();
                int cupoMaximo = curso.getCupoMaximo();
                double porcentaje = cupoMaximo > 0 ? (cupoActual * 100.0) / cupoMaximo : 0.0;

                totalEstudiantesDept += cupoActual;
                totalCuposDept += cupoMaximo;
                sumaOcupacionesDept += porcentaje;

                if (cupoActual >= cupoMaximo) {
                    cursosCompletosDept++;
                }
            }

            double promedioOcupacionDept = totalCursosDept > 0 ?
                    sumaOcupacionesDept / totalCursosDept : 0.0;
            double porcentajeOcupacionDept = totalCuposDept > 0 ?
                    (totalEstudiantesDept * 100.0) / totalCuposDept : 0.0;

            OcupacionCursosReporteDTO.OcupacionPorDepartamento deptData =
                    OcupacionCursosReporteDTO.OcupacionPorDepartamento.builder()
                            .nombreDepartamento(nombreDepartamento)
                            .totalCursos(totalCursosDept)
                            .promedioOcupacion(Math.round(promedioOcupacionDept * 100.0) / 100.0)
                            .cursosCompletos(cursosCompletosDept)
                            .totalEstudiantes(totalEstudiantesDept)
                            .totalCupos(totalCuposDept)
                            .porcentajeOcupacion(Math.round(porcentajeOcupacionDept * 100.0) / 100.0)
                            .build();

            departamentos.add(deptData);
        }

        // Ordenar departamentos por promedio de ocupación (mayor a menor)
        departamentos.sort((d1, d2) ->
                Double.compare(d2.getPromedioOcupacion(), d1.getPromedioOcupacion()));

        // Ordenar tops
        cursosCompletos.sort((c1, c2) -> c1.getNombreCurso().compareTo(c2.getNombreCurso()));
        cursosMasDemandados.sort((c1, c2) ->
                Double.compare(c2.getPorcentajeOcupacion(), c1.getPorcentajeOcupacion()));
        cursosMenosDemandados.sort((c1, c2) ->
                Double.compare(c1.getPorcentajeOcupacion(), c2.getPorcentajeOcupacion()));

        // Limitar tops a 10
        if (cursosCompletos.size() > 10) {
            cursosCompletos = cursosCompletos.subList(0, 10);
        }
        if (cursosMasDemandados.size() > 10) {
            cursosMasDemandados = cursosMasDemandados.subList(0, 10);
        }
        if (cursosMenosDemandados.size() > 10) {
            cursosMenosDemandados = cursosMenosDemandados.subList(0, 10);
        }

        // Construir DTO final
        return OcupacionCursosReporteDTO.builder()
                .totalCursos(totalCursos)
                .promedioOcupacionGlobal(Math.round(promedioOcupacionGlobal * 100.0) / 100.0)
                .totalCuposOfrecidos(totalCuposOfrecidos)
                .totalCuposOcupados(totalCuposOcupados)
                .totalCuposDisponibles(totalCuposDisponibles)
                .distribucion(distribucion)
                .departamentos(departamentos)
                .cursosCompletos(cursosCompletos)
                .cursosMasDemandados(cursosMasDemandados)
                .cursosMenosDemandados(cursosMenosDemandados)
                .build();
    }
}