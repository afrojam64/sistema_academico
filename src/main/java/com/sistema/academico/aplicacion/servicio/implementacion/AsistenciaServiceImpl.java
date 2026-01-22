package com.sistema.academico.aplicacion.servicio.implementacion;

import com.sistema.academico.aplicacion.dto.request.AsistenciaRequestDTO;
import com.sistema.academico.aplicacion.dto.response.AsistenciaResponseDTO;
import com.sistema.academico.aplicacion.mapper.AsistenciaMapper;
import com.sistema.academico.aplicacion.servicio.IAsistenciaService;
import com.sistema.academico.dominio.entidad.Asistencia;
import com.sistema.academico.dominio.entidad.Inscripcion;
import com.sistema.academico.infraestructura.excepcion.RecursoNoEncontradoException;
import com.sistema.academico.infraestructura.repositorio.AsistenciaRepository;
import com.sistema.academico.infraestructura.repositorio.InscripcionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AsistenciaServiceImpl implements IAsistenciaService {

    private final AsistenciaRepository asistenciaRepository;
    private final InscripcionRepository inscripcionRepository;
    private final AsistenciaMapper asistenciaMapper;

    @Override
    @Transactional
    public AsistenciaResponseDTO registrarAsistencia(AsistenciaRequestDTO dto) {
        Inscripcion inscripcion = inscripcionRepository.findById(dto.getInscripcionId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Inscripción no encontrada con ID: " + dto.getInscripcionId()));

        // Buscar si ya existe asistencia para esa fecha e inscripción
        List<Asistencia> existentes = asistenciaRepository.findByInscripcionIdAndFechaBetween(
                dto.getInscripcionId(), dto.getFecha(), dto.getFecha());

        Asistencia asistencia;
        if (!existentes.isEmpty()) {
            // Actualizar existente
            asistencia = existentes.get(0);
            asistencia.setPresente(dto.getPresente());
            asistencia.setObservaciones(dto.getObservaciones());
        } else {
            // Crear nueva
            asistencia = asistenciaMapper.toEntity(dto);
            asistencia.setInscripcion(inscripcion);
        }

        Asistencia guardada = asistenciaRepository.save(asistencia);
        return asistenciaMapper.toDTO(guardada);
    }

    @Override
    @Transactional
    public List<AsistenciaResponseDTO> registrarAsistenciaMasiva(Long cursoId, LocalDate fecha, List<AsistenciaRequestDTO> dtos) {
        List<AsistenciaResponseDTO> resultados = new ArrayList<>();
        for (AsistenciaRequestDTO dto : dtos) {
            // Asegurar que la fecha del DTO coincida con la fecha del lote
            dto.setFecha(fecha);
            resultados.add(registrarAsistencia(dto));
        }
        return resultados;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AsistenciaResponseDTO> obtenerAsistenciaPorInscripcion(Long inscripcionId) {
        if (!inscripcionRepository.existsById(inscripcionId)) {
            throw new RecursoNoEncontradoException("Inscripción no encontrada con ID: " + inscripcionId);
        }
        List<Asistencia> asistencias = asistenciaRepository.findByInscripcionId(inscripcionId);
        return asistenciaMapper.toDTOList(asistencias);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AsistenciaResponseDTO> obtenerAsistenciaPorCursoYFecha(Long cursoId, LocalDate fecha) {
        // Primero obtenemos todas las inscripciones del curso
        List<Inscripcion> inscripciones = inscripcionRepository.findByCursoId(cursoId);
        
        List<AsistenciaResponseDTO> reporte = new ArrayList<>();
        
        for (Inscripcion inscripcion : inscripciones) {
            // Buscamos si tiene asistencia registrada para esa fecha
            List<Asistencia> asistencias = asistenciaRepository.findByInscripcionIdAndFechaBetween(
                    inscripcion.getId(), fecha, fecha);
            
            if (!asistencias.isEmpty()) {
                reporte.add(asistenciaMapper.toDTO(asistencias.get(0)));
            } else {
                // Si no hay registro, devolvemos un DTO "vacío" o marcado como no registrado
                // Por ahora solo devolvemos los registrados.
                // Opcionalmente podríamos devolver un objeto con presente=false por defecto.
            }
        }
        return reporte;
    }
}