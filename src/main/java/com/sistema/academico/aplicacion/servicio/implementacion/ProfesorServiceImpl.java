package com.sistema.academico.aplicacion.servicio.implementacion;

import com.sistema.academico.aplicacion.dto.request.ProfesorRequestDTO;
import com.sistema.academico.aplicacion.dto.response.CursosPorProfesorReporteDTO;
import com.sistema.academico.aplicacion.dto.response.ProfesorResponseDTO;
import com.sistema.academico.aplicacion.mapper.ProfesorMapper;
import com.sistema.academico.aplicacion.servicio.IProfesorService;
import com.sistema.academico.dominio.entidad.Curso;
import com.sistema.academico.dominio.entidad.Departamento;
import com.sistema.academico.dominio.entidad.Profesor;
import com.sistema.academico.dominio.entidad.Usuario;
import com.sistema.academico.dominio.enumeracion.Estado;
import com.sistema.academico.dominio.enumeracion.Rol;
import com.sistema.academico.infraestructura.excepcion.RecursoDuplicadoException;
import com.sistema.academico.infraestructura.excepcion.OperacionNoPermitidaException;
import com.sistema.academico.infraestructura.excepcion.RecursoNoEncontradoException;
import com.sistema.academico.infraestructura.repositorio.CursoRepository;
import com.sistema.academico.infraestructura.repositorio.DepartamentoRepository;
import com.sistema.academico.infraestructura.repositorio.ProfesorRepository;
import com.sistema.academico.infraestructura.repositorio.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfesorServiceImpl implements IProfesorService {

    private final ProfesorRepository profesorRepository;
    private final UsuarioRepository usuarioRepository;
    private final DepartamentoRepository departamentoRepository;
    private final ProfesorMapper profesorMapper;
    private final PasswordEncoder passwordEncoder;
    private final CursoRepository cursoRepository;

    @Override
    @Transactional
    public ProfesorResponseDTO crear(ProfesorRequestDTO request) {
        // Validar que el email no exista en usuarios
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RecursoDuplicadoException("Ya existe un usuario con ese email");
        }

        // Validar que la cédula no exista en usuarios
        if (usuarioRepository.existsByCedula(request.getCedula())) {
            throw new RecursoDuplicadoException("Ya existe un usuario con esa cédula");
        }

        // Validar que el departamento existe y está activo
        Departamento departamento = departamentoRepository.findById(request.getDepartamentoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Departamento no encontrado"));

        if (!departamento.estaActivo()) {
            throw new OperacionNoPermitidaException("El departamento debe estar activo");
        }

        // 1. GENERAR NOMBRE DE USUARIO ÚNICO
        String nombreUsuario = generarNombreUsuario(request.getNombre(), request.getApellido());

        // 2. CREAR USUARIO AUTOMÁTICAMENTE
        Usuario usuario = Usuario.builder()
                .nombreUsuario(nombreUsuario)
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .email(request.getEmail())
                .cedula(request.getCedula())  // Cédula del formulario
                .telefono(request.getTelefono())
                .contrasena(passwordEncoder.encode("Temporal123"))  // Contraseña temporal
                .rol(Rol.PROFESOR)
                .estado(Estado.ACTIVO)
                .build();

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        // 3. CREAR PROFESOR VINCULADO AL USUARIO
        Profesor profesor = profesorMapper.toEntity(request, usuarioGuardado, departamento);
        Profesor profesorGuardado = profesorRepository.save(profesor);

        return profesorMapper.toResponseDTO(profesorGuardado);
    }

    /**
     * Genera un nombre de usuario único basado en nombre y apellido
     * Formato: primera letra del nombre + apellido sin espacios
     * Si ya existe, agrega número incremental
     */
    private String generarNombreUsuario(String nombre, String apellido) {
        // Limpiar y generar base
        String base = (nombre.substring(0, 1) + apellido)
                .toLowerCase()
                .replaceAll("[^a-z0-9]", "");  // Solo letras y números

        String nombreUsuario = base;
        int contador = 1;

        // Si ya existe, agregar número
        while (usuarioRepository.existsByNombreUsuario(nombreUsuario)) {
            nombreUsuario = base + contador;
            contador++;
        }

        return nombreUsuario;
    }

    @Override
    @Transactional(readOnly = true)
    public ProfesorResponseDTO obtenerPorId(Long id) {
        Profesor profesor = profesorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Profesor no encontrado con ID: " + id));

        return profesorMapper.toResponseDTO(profesor);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfesorResponseDTO> listarTodos() {
        return profesorRepository.findAll().stream()
                .map(profesorMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfesorResponseDTO> listarActivos() {
        return profesorRepository.findByEstado(Estado.ACTIVO).stream()
                .map(profesorMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProfesorResponseDTO actualizar(Long id, ProfesorRequestDTO request) {
        Profesor profesor = profesorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Profesor no encontrado con ID: " + id));

        Usuario usuario = profesor.getUsuario();

        // Validar email si cambió
        if (!usuario.getEmail().equals(request.getEmail())) {
            if (usuarioRepository.existsByEmail(request.getEmail())) {
                throw new RecursoDuplicadoException("Ya existe un usuario con ese email");
            }
        }

        // Validar cédula si cambió
        if (!usuario.getCedula().equals(request.getCedula())) {
            if (usuarioRepository.existsByCedula(request.getCedula())) {
                throw new RecursoDuplicadoException("Ya existe un usuario con esa cédula");
            }
        }

        // Obtener departamento si cambió
        Departamento departamento = null;
        if (request.getDepartamentoId() != null) {
            departamento = departamentoRepository.findById(request.getDepartamentoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Departamento no encontrado"));
        }

        // Actualizar profesor Y usuario
        profesorMapper.updateEntityFromDTO(profesor, request, departamento);

        // Guardar usuario actualizado
        usuarioRepository.save(usuario);

        // Guardar profesor actualizado
        Profesor actualizado = profesorRepository.save(profesor);

        return profesorMapper.toResponseDTO(actualizado);
    }

    @Override
    @Transactional
    public void desactivar(Long id, Rol rolUsuarioActual) {
        if (!rolUsuarioActual.puedeDesactivar()) {
            throw new OperacionNoPermitidaException("No tiene permisos para desactivar profesores");
        }

        Profesor profesor = profesorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Profesor no encontrado con ID: " + id));

        profesor.setEstado(Estado.INACTIVO);
        profesorRepository.save(profesor);
    }

    @Override
    @Transactional
    public void activar(Long id, Rol rolUsuarioActual) {
        if (!rolUsuarioActual.puedeDesactivar()) {
            throw new OperacionNoPermitidaException("No tiene permisos para activar profesores");
        }

        Profesor profesor = profesorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Profesor no encontrado con ID: " + id));

        profesor.setEstado(Estado.ACTIVO);
        profesorRepository.save(profesor);
    }

    @Override
    @Transactional
    public void eliminar(Long id, Rol rolUsuarioActual) {
        if (!rolUsuarioActual.puedeEliminarFisicamente()) {
            throw new OperacionNoPermitidaException(
                    "Solo SUPER_ADMIN puede eliminar profesores físicamente");
        }

        Profesor profesor = profesorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Profesor no encontrado con ID: " + id));

        // Eliminar profesor (el usuario se mantiene)
        profesorRepository.delete(profesor);
    }

    /**
     * Cambiar la contraseña de un profesor
     * @param id ID del profesor
     * @param nuevaContrasena Nueva contraseña (se encriptará automáticamente)
     */
    @Override
    @Transactional
    public void cambiarContrasena(Long id, String nuevaContrasena) {
        Profesor profesor = profesorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Profesor no encontrado con ID: " + id));

        Usuario usuario = profesor.getUsuario();

        // Encriptar la nueva contraseña
        usuario.setContrasena(passwordEncoder.encode(nuevaContrasena));

        // Guardar el usuario actualizado
        usuarioRepository.save(usuario);
    }

    /**
     * Generar reporte de carga académica por profesor
     */
    @Override
    @Transactional(readOnly = true)
    public CursosPorProfesorReporteDTO generarReporteCursosPorProfesor(Long profesorId) {

        List<Profesor> profesoresAEvaluar;

        // Determinar qué profesores evaluar
        if (profesorId != null) {
            // Buscar profesor específico
            Profesor profesor = profesorRepository.findById(profesorId)
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Profesor no encontrado con ID: " + profesorId));
            profesoresAEvaluar = List.of(profesor);
        } else {
            // Buscar todos los profesores activos
            profesoresAEvaluar = profesorRepository.findByEstado(Estado.ACTIVO);
        }

        // Variables para estadísticas generales
        int totalProfesores = profesoresAEvaluar.size();
        int totalCursosAsignados = 0;
        int totalEstudiantes = 0;
        double sumaOcupaciones = 0.0;
        int contadorOcupaciones = 0;

        // Lista de profesores con sus cursos
        List<CursosPorProfesorReporteDTO.ProfesorConCursos> profesoresData = new ArrayList<>();

        // Procesar cada profesor
        for (Profesor profesor : profesoresAEvaluar) {
            // Obtener cursos activos del profesor
            List<Curso> cursosProfesor = cursoRepository.findByProfesorAndEstado(
                    profesor, Estado.ACTIVO);

            // Variables para estadísticas del profesor
            int totalCursos = cursosProfesor.size();
            int estudiantesProfesor = 0;
            double sumaOcupacionesProfesor = 0.0;

            // Lista de cursos asignados
            List<CursosPorProfesorReporteDTO.CursoAsignado> cursosData = new ArrayList<>();

            for (Curso curso : cursosProfesor) {
                int cupoMaximo = curso.getCupoMaximo();
                int cupoActual = curso.getCupoActual();
                int cuposDisponibles = cupoMaximo - cupoActual;
                double porcentajeOcupacion = cupoMaximo > 0 ?
                        (cupoActual * 100.0) / cupoMaximo : 0.0;

                estudiantesProfesor += cupoActual;
                sumaOcupacionesProfesor += porcentajeOcupacion;

                CursosPorProfesorReporteDTO.CursoAsignado cursoData =
                        CursosPorProfesorReporteDTO.CursoAsignado.builder()
                                .cursoId(curso.getId())
                                .nombreCurso(curso.getNombre())
                                .codigoCurso(curso.getCodigo())
                                .nombreMateria(curso.getMateria().getNombre())
                                .creditos(curso.getMateria().getCreditos())
                                .periodo(curso.getPeriodo())
                                .fechaInicio(curso.getFechaInicio().format(
                                        java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                                .fechaFin(curso.getFechaFin().format(
                                        java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                                .cupoMaximo(cupoMaximo)
                                .cupoActual(cupoActual)
                                .cuposDisponibles(cuposDisponibles)
                                .porcentajeOcupacion(Math.round(porcentajeOcupacion * 100.0) / 100.0)
                                .estadoCurso(curso.getEstado().name())
                                .build();

                cursosData.add(cursoData);
            }

            // Calcular promedio de ocupación del profesor
            double promedioOcupacionProfesor = totalCursos > 0 ?
                    sumaOcupacionesProfesor / totalCursos : 0.0;

            // Determinar carga académica
            String cargaAcademica;
            if (totalCursos >= 5) {
                cargaAcademica = "ALTA";
            } else if (totalCursos >= 3) {
                cargaAcademica = "MEDIA";
            } else if (totalCursos > 0) {
                cargaAcademica = "BAJA";
            } else {
                cargaAcademica = "SIN CARGA";
            }

            // Acumular estadísticas generales
            totalCursosAsignados += totalCursos;
            totalEstudiantes += estudiantesProfesor;
            sumaOcupaciones += promedioOcupacionProfesor;
            if (totalCursos > 0) {
                contadorOcupaciones++;
            }

            // Crear DTO del profesor
            CursosPorProfesorReporteDTO.ProfesorConCursos profesorData =
                    CursosPorProfesorReporteDTO.ProfesorConCursos.builder()
                            .profesorId(profesor.getId())
                            .nombreCompleto(profesor.getUsuario().getNombre() + " " +
                                    profesor.getUsuario().getApellido())
                            .email(profesor.getUsuario().getEmail())
                            .telefono(profesor.getTelefono())
                            .especialidad(profesor.getEspecialidad())
                            .nombreDepartamento(profesor.getDepartamento().getNombre())
                            .fechaContratacion(profesor.getFechaContratacion() != null ?
                                    profesor.getFechaContratacion().format(
                                            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "")
                            .estado(profesor.getEstado().name())
                            .totalCursosActivos(totalCursos)
                            .totalEstudiantes(estudiantesProfesor)
                            .promedioOcupacion(Math.round(promedioOcupacionProfesor * 100.0) / 100.0)
                            .cargaAcademica(cargaAcademica)
                            .cursos(cursosData)
                            .build();

            profesoresData.add(profesorData);
        }

        // Calcular promedio de ocupación general
        double promedioOcupacion = contadorOcupaciones > 0 ?
                sumaOcupaciones / contadorOcupaciones : 0.0;

        // Ordenar profesores por cantidad de cursos (mayor a menor)
        profesoresData.sort((p1, p2) ->
                Integer.compare(p2.getTotalCursosActivos(), p1.getTotalCursosActivos()));

        // Construir DTO final
        return CursosPorProfesorReporteDTO.builder()
                .totalProfesores(totalProfesores)
                .totalCursosAsignados(totalCursosAsignados)
                .totalEstudiantes(totalEstudiantes)
                .promedioOcupacion(Math.round(promedioOcupacion * 100.0) / 100.0)
                .profesores(profesoresData)
                .build();
    }

    /**
     * Buscar profesores por término de búsqueda
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProfesorResponseDTO> buscarPorTermino(String termino) {
        String terminoBusqueda = termino.toLowerCase().trim();
        List<Profesor> todosProfesores = profesorRepository.findAll();

        List<Profesor> resultados = todosProfesores.stream()
                .filter(profesor -> profesor.getEstado() == Estado.ACTIVO)
                .filter(profesor -> {
                    String nombreCompleto = (profesor.getUsuario().getNombre() + " " +
                            profesor.getUsuario().getApellido()).toLowerCase();
                    String email = profesor.getUsuario().getEmail().toLowerCase();

                    return nombreCompleto.contains(terminoBusqueda) ||
                            email.contains(terminoBusqueda);
                })
                .collect(Collectors.toList());

        return resultados.stream()
                .map(profesorMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfesorResponseDTO> listarProfesoresConCursosActivos() {
        // Obtener todos los profesores activos
        List<Profesor> profesores = profesorRepository.findByEstado(Estado.ACTIVO);

        // Filtrar solo los que tienen cursos activos
        List<Profesor> profesoresConCursos = profesores.stream()
                .filter(profesor -> {
                    List<Curso> cursosActivos = cursoRepository
                            .findByProfesorAndEstado(profesor, Estado.ACTIVO);
                    return !cursosActivos.isEmpty();
                })
                .collect(Collectors.toList());

        // Mapear a DTO
        return profesoresConCursos.stream()
                .map(profesorMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}