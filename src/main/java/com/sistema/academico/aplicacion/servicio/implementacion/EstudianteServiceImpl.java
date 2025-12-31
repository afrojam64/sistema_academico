package com.sistema.academico.aplicacion.servicio.implementacion;

import com.sistema.academico.aplicacion.dto.request.EstudianteRequestDTO;
import com.sistema.academico.aplicacion.dto.response.EstudianteResponseDTO;
import com.sistema.academico.aplicacion.mapper.EstudianteMapper;
import com.sistema.academico.aplicacion.servicio.IEstudianteService;
import com.sistema.academico.dominio.entidad.Estudiante;
import com.sistema.academico.dominio.entidad.Usuario;
import com.sistema.academico.dominio.enumeracion.Estado;
import com.sistema.academico.dominio.enumeracion.Rol;
import com.sistema.academico.infraestructura.excepcion.RecursoDuplicadoException;
import com.sistema.academico.infraestructura.excepcion.OperacionNoPermitidaException;
import com.sistema.academico.infraestructura.excepcion.RecursoNoEncontradoException;
import com.sistema.academico.infraestructura.repositorio.EstudianteRepository;
import com.sistema.academico.infraestructura.repositorio.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EstudianteServiceImpl implements IEstudianteService {

    private final EstudianteRepository estudianteRepository;
    private final UsuarioRepository usuarioRepository;
    private final EstudianteMapper estudianteMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public EstudianteResponseDTO crear(EstudianteRequestDTO request) {
        // Validar que el email no exista en usuarios
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RecursoDuplicadoException("Ya existe un usuario con ese email");
        }

        // Validar que la cédula no exista en usuarios
        if (usuarioRepository.existsByCedula(request.getCedula())) {
            throw new RecursoDuplicadoException("Ya existe un usuario con esa cédula");
        }

        // Validar que el código de estudiante no exista
        if (estudianteRepository.existsByCodigoEstudiante(request.getCodigoEstudiante())) {
            throw new RecursoDuplicadoException("Ya existe un estudiante con ese código");
        }

        // 1. GENERAR NOMBRE DE USUARIO ÚNICO
        String nombreUsuario = generarNombreUsuario(request.getNombre(), request.getApellido());

        // 2. CREAR USUARIO AUTOMÁTICAMENTE
        Usuario usuario = Usuario.builder()
                .nombreUsuario(nombreUsuario)
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .email(request.getEmail())
                .cedula(request.getCedula())
                .telefono(request.getTelefono())
                .contrasena(passwordEncoder.encode("Temporal123"))  // Contraseña temporal
                .rol(Rol.ESTUDIANTE)
                .estado(Estado.ACTIVO)
                .build();

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        // 3. CREAR ESTUDIANTE VINCULADO AL USUARIO
        Estudiante estudiante = estudianteMapper.toEntity(request, usuarioGuardado);
        Estudiante estudianteGuardado = estudianteRepository.save(estudiante);

        return estudianteMapper.toResponseDTO(estudianteGuardado);
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
    public EstudianteResponseDTO obtenerPorId(Long id) {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Estudiante no encontrado con ID: " + id));

        return estudianteMapper.toResponseDTO(estudiante);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EstudianteResponseDTO> listarTodos() {
        return estudianteRepository.findAll().stream()
                .map(estudianteMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EstudianteResponseDTO> listarActivos() {
        return estudianteRepository.findByEstado(Estado.ACTIVO).stream()
                .map(estudianteMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EstudianteResponseDTO actualizar(Long id, EstudianteRequestDTO request) {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Estudiante no encontrado con ID: " + id));

        Usuario usuario = estudiante.getUsuario();

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

        // Validar código de estudiante si cambió
        if (!estudiante.getCodigoEstudiante().equals(request.getCodigoEstudiante())) {
            if (estudianteRepository.existsByCodigoEstudiante(request.getCodigoEstudiante())) {
                throw new RecursoDuplicadoException("Ya existe un estudiante con ese código");
            }
        }

        // Actualizar estudiante Y usuario
        estudianteMapper.updateEntityFromDTO(estudiante, request);

        // Guardar usuario actualizado
        usuarioRepository.save(usuario);

        // Guardar estudiante actualizado
        Estudiante actualizado = estudianteRepository.save(estudiante);

        return estudianteMapper.toResponseDTO(actualizado);
    }

    @Override
    @Transactional
    public void desactivar(Long id, Rol rolUsuarioActual) {
        if (!rolUsuarioActual.puedeDesactivar()) {
            throw new OperacionNoPermitidaException("No tiene permisos para desactivar estudiantes");
        }

        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Estudiante no encontrado con ID: " + id));

        estudiante.setEstado(Estado.INACTIVO);
        estudianteRepository.save(estudiante);
    }

    @Override
    @Transactional
    public void activar(Long id, Rol rolUsuarioActual) {
        if (!rolUsuarioActual.puedeDesactivar()) {
            throw new OperacionNoPermitidaException("No tiene permisos para activar estudiantes");
        }

        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Estudiante no encontrado con ID: " + id));

        estudiante.setEstado(Estado.ACTIVO);
        estudianteRepository.save(estudiante);
    }

    @Override
    @Transactional
    public void eliminar(Long id, Rol rolUsuarioActual) {
        if (!rolUsuarioActual.puedeEliminarFisicamente()) {
            throw new OperacionNoPermitidaException(
                    "Solo SUPER_ADMIN puede eliminar estudiantes físicamente");
        }

        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Estudiante no encontrado con ID: " + id));

        // Eliminar estudiante (el usuario se mantiene)
        estudianteRepository.delete(estudiante);
    }
}