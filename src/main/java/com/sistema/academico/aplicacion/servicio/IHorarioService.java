package com.sistema.academico.aplicacion.servicio;

import com.sistema.academico.aplicacion.dto.request.HorarioRequestDTO;
import com.sistema.academico.aplicacion.dto.response.HorarioResponseDTO;
import java.util.List;

/**
 * Interfaz para la gestión de horarios de cursos.
 */
public interface IHorarioService {
    
    /**
     * Agrega un horario a un curso existente.
     * @param cursoId ID del curso
     * @param dto Datos del horario
     * @return Horario creado
     */
    HorarioResponseDTO agregarHorario(Long cursoId, HorarioRequestDTO dto);

    /**
     * Elimina un horario específico.
     * @param id ID del horario
     */
    void eliminarHorario(Long id);

    /**
     * Obtiene todos los horarios de un curso.
     * @param cursoId ID del curso
     * @return Lista de horarios
     */
    List<HorarioResponseDTO> obtenerHorariosPorCurso(Long cursoId);
}