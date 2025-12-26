package com.sistema.academico.aplicacion.servicio.implementacion;

import com.sistema.academico.aplicacion.dto.request.DepartamentoRequestDTO;
import com.sistema.academico.aplicacion.dto.response.DepartamentoResponseDTO;
import com.sistema.academico.aplicacion.mapper.DepartamentoMapper;
import com.sistema.academico.aplicacion.servicio.IDepartamentoService;
import com.sistema.academico.dominio.entidad.Departamento;
import com.sistema.academico.dominio.enumeracion.Estado;
import com.sistema.academico.dominio.enumeracion.Rol;
import com.sistema.academico.infraestructura.excepcion.RecursoDuplicadoException;
import com.sistema.academico.infraestructura.excepcion.OperacionNoPermitidaException;
import com.sistema.academico.infraestructura.excepcion.RecursoNoEncontradoException;
import com.sistema.academico.infraestructura.repositorio.DepartamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartamentoServiceImpl implements IDepartamentoService {

    private final DepartamentoRepository departamentoRepository;
    private final DepartamentoMapper departamentoMapper;

    @Override
    @Transactional
    public DepartamentoResponseDTO crear(DepartamentoRequestDTO request) {
        // Validar que el código no exista
        if (departamentoRepository.existsByCodigo(request.getCodigo())) {
            throw new RecursoDuplicadoException("El código de departamento ya existe");
        }

        // Validar que el nombre no exista
        if (departamentoRepository.existsByNombre(request.getNombre())) {
            throw new RecursoDuplicadoException("El nombre de departamento ya existe");
        }

        Departamento departamento = departamentoMapper.toEntity(request);
        Departamento guardado = departamentoRepository.save(departamento);

        return departamentoMapper.toResponseDTO(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public DepartamentoResponseDTO obtenerPorId(Long id) {
        Departamento departamento = departamentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Departamento no encontrado con ID: " + id));

        return departamentoMapper.toResponseDTO(departamento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartamentoResponseDTO> listarTodos() {
        return departamentoRepository.findAll().stream()
                .map(departamentoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartamentoResponseDTO> listarActivos() {
        return departamentoRepository.findActivos().stream()
                .map(departamentoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DepartamentoResponseDTO actualizar(Long id, DepartamentoRequestDTO request) {
        Departamento departamento = departamentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Departamento no encontrado con ID: " + id));

        // Validar código si cambió
        if (request.getCodigo() != null && !request.getCodigo().equals(departamento.getCodigo())) {
            if (departamentoRepository.existsByCodigo(request.getCodigo())) {
                throw new RecursoDuplicadoException("El código de departamento ya existe");
            }
        }

        // Validar nombre si cambió
        if (request.getNombre() != null && !request.getNombre().equals(departamento.getNombre())) {
            if (departamentoRepository.existsByNombre(request.getNombre())) {
                throw new RecursoDuplicadoException("El nombre de departamento ya existe");
            }
        }

        departamentoMapper.updateEntityFromDTO(departamento, request);
        Departamento actualizado = departamentoRepository.save(departamento);

        return departamentoMapper.toResponseDTO(actualizado);
    }

    @Override
    @Transactional
    public void desactivar(Long id, Rol rolUsuarioActual) {
        if (!rolUsuarioActual.puedeDesactivar()) {
            throw new OperacionNoPermitidaException("No tiene permisos para desactivar departamentos");
        }

        Departamento departamento = departamentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Departamento no encontrado con ID: " + id));

        departamento.setEstado(Estado.INACTIVO);
        departamentoRepository.save(departamento);
    }

    @Override
    @Transactional
    public void activar(Long id, Rol rolUsuarioActual) {
        if (!rolUsuarioActual.puedeDesactivar()) {
            throw new OperacionNoPermitidaException("No tiene permisos para activar departamentos");
        }

        Departamento departamento = departamentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Departamento no encontrado con ID: " + id));

        departamento.setEstado(Estado.ACTIVO);
        departamentoRepository.save(departamento);
    }

    @Override
    @Transactional
    public void eliminar(Long id, Rol rolUsuarioActual) {
        if (!rolUsuarioActual.puedeEliminarFisicamente()) {
            throw new OperacionNoPermitidaException("Solo SUPER_ADMIN puede eliminar departamentos físicamente");
        }

        Departamento departamento = departamentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Departamento no encontrado con ID: " + id));

        departamentoRepository.delete(departamento);
    }
}