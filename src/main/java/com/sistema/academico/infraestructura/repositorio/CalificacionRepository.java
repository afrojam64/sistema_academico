package com.sistema.academico.infraestructura.repositorio;

import com.sistema.academico.dominio.entidad.Calificacion;
import com.sistema.academico.dominio.entidad.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CalificacionRepository extends JpaRepository<Calificacion, Long> {

    // Buscar calificaciones de una inscripción
    List<Calificacion> findByInscripcion(Inscripcion inscripcion);

    // Buscar calificaciones de una inscripción ordenadas por fecha
    List<Calificacion> findByInscripcionOrderByFechaCalificacionAsc(Inscripcion inscripcion);

    // Calcular promedio ponderado de una inscripción
    @Query("SELECT SUM(c.nota * c.porcentaje) / 100.0 FROM Calificacion c WHERE c.inscripcion = :inscripcion")
    BigDecimal calcularPromedioPonderado(@Param("inscripcion") Inscripcion inscripcion);

    // Verificar si una inscripción tiene todas las calificaciones
    @Query("SELECT CASE WHEN SUM(c.porcentaje) = 100 THEN true ELSE false END FROM Calificacion c WHERE c.inscripcion = :inscripcion")
    Boolean tieneCalificacionCompleta(@Param("inscripcion") Inscripcion inscripcion);
}