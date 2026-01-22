package com.sistema.academico.aplicacion.servicio.implementacion;

import com.sistema.academico.aplicacion.dto.request.HorarioRequestDTO;
import com.sistema.academico.aplicacion.dto.response.HorarioResponseDTO;
import com.sistema.academico.aplicacion.mapper.HorarioMapper;
import com.sistema.academico.aplicacion.servicio.IHorarioService;
import com.sistema.academico.dominio.entidad.Curso;
import com.sistema.academico.dominio.entidad.Horario;
import com.sistema.academico.infraestructura.excepcion.RecursoNoEncontradoException;
import com.sistema.academico.infraestructura.excepcion.ValidacionNegocioException;
import com.sistema.academico.infraestructura.repositorio.CursoRepository;
import com.sistema.academico.infraestructura.repositorio.HorarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HorarioServiceImpl implements IHorarioService {

    private final HorarioRepository horarioRepository;
    private final CursoRepository cursoRepository;
    private final HorarioMapper horarioMapper;

    @Override
    @Transactional
    public HorarioResponseDTO agregarHorario(Long cursoId, HorarioRequestDTO dto) {
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Curso no encontrado con ID: " + cursoId));

        if (dto.getHoraInicio().isAfter(dto.getHoraFin())) {
            throw new ValidacionNegocioException("La hora de inicio no puede ser posterior a la hora de fin");
        }

        Horario horario = horarioMapper.toEntity(dto);
        curso.agregarHorario(horario); // Método helper en Curso que establece la relación bidireccional
        
        Horario guardado = horarioRepository.save(horario);
        return horarioMapper.toDTO(guardado);
    }

    @Override
    @Transactional
    public void eliminarHorario(Long id) {
        if (!horarioRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Horario no encontrado con ID: " + id);
        }
        horarioRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HorarioResponseDTO> obtenerHorariosPorCurso(Long cursoId) {
        if (!cursoRepository.existsById(cursoId)) {
            throw new RecursoNoEncontradoException("Curso no encontrado con ID: " + cursoId);
        }
        List<Horario> horarios = horarioRepository.findByCursoId(cursoId);
        return horarioMapper.toDTOList(horarios);
    }
}