package com.sistema.academico.aplicacion.servicio.implementacion;

import com.sistema.academico.aplicacion.dto.request.ProfesorRequestDTO;
import com.sistema.academico.aplicacion.dto.response.ProfesorResponseDTO;
import com.sistema.academico.aplicacion.mapper.ProfesorMapper;
import com.sistema.academico.aplicacion.servicio.IProfesorService;
import com.sistema.academico.dominio.entidad.Departamento;
import com.sistema.academico.dominio.entidad.Profesor;
import com.sistema.academico.dominio.entidad.Usuario;
import com.sistema.academico.dominio.enumeracion.Estado;
import com.sistema.academico.dominio.enumeracion.Rol;
import com.sistema.academico.infraestructura.excepcion.RecursoDuplicadoException;
import com.sistema.academico.infraestructura.excepcion.OperacionNoPermitidaException;
import com.sistema.academico.infraestructura.excepcion.RecursoNoEncontradoException;
import com.sistema.academico.infraestructura.repositorio.DepartamentoRepository;
import com.sistema.academico.infraestructura.repositorio.ProfesorRepository;
import com.sistema.academico.infraestructura.repositorio.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}