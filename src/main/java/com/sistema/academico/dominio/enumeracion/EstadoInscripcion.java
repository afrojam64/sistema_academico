package com.sistema.academico.dominio.enumeracion;

/**
 * Enumeración que define los estados posibles de una inscripción.
 *
 * Una inscripción representa la matrícula de un estudiante en un curso específico.
 * Durante el periodo académico, esta inscripción puede pasar por diferentes estados.
 *
 * Estados disponibles:
 * - ACTIVO: El estudiante está cursando actualmente la materia
 * - RETIRADO: El estudiante se retiró del curso antes de finalizarlo
 * - COMPLETADO: El estudiante finalizó el curso (aprobado o reprobado)
 *
 * UBICACIÓN EN ARQUITECTURA:
 * - Capa: DOMINIO
 * - Paquete: dominio.enumeracion
 * - Usado por: Entidad Inscripcion
 *
 * @author Sistema Académico
 * @version 1.0
 */
public enum EstadoInscripcion {

    /**
     * Estado ACTIVO: El estudiante está cursando actualmente.
     *
     * Características:
     * - El estudiante puede recibir calificaciones
     * - Aparece en las listas de asistencia del profesor
     * - Se considera para estadísticas de cursos activos
     * - Puede cambiar a RETIRADO o COMPLETADO
     *
     * Transiciones permitidas:
     * ACTIVO → RETIRADO (si el estudiante se retira)
     * ACTIVO → COMPLETADO (al finalizar el curso)
     */
    ACTIVO(
            "Activo",
            "El estudiante está cursando actualmente",
            "badge-success"  // Clase CSS Bootstrap para color verde
    ),

    /**
     * Estado RETIRADO: El estudiante se retiró del curso.
     *
     * Características:
     * - El estudiante ya no recibe calificaciones nuevas
     * - No aparece en listas de asistencia activas
     * - Se mantiene el historial de calificaciones parciales
     * - No se considera para estadísticas de finalización
     *
     * Motivos comunes de retiro:
     * - Solicitud voluntaria del estudiante
     * - Cambio de horario o programa
     * - Problemas académicos o personales
     *
     * Estado final: No puede cambiar a otros estados
     */
    RETIRADO(
            "Retirado",
            "El estudiante se retiró del curso",
            "badge-warning"  // Clase CSS Bootstrap para color amarillo
    ),

    /**
     * Estado COMPLETADO: El curso finalizó para el estudiante.
     *
     * Características:
     * - Todas las calificaciones están registradas
     * - Se calculó el promedio final
     * - Aparece en el historial académico
     * - Se usa para estadísticas de aprobación/reprobación
     *
     * Incluye dos casos:
     * - APROBADO: Nota final >= nota mínima aprobatoria
     * - REPROBADO: Nota final < nota mínima aprobatoria
     *
     * Estado final: No puede cambiar a otros estados
     */
    COMPLETADO(
            "Completado",
            "El estudiante finalizó el curso",
            "badge-primary"  // Clase CSS Bootstrap para color azul
    );

    // =========================================================================
    // ATRIBUTOS DEL ENUM
    // =========================================================================

    /**
     * Nombre legible del estado para mostrar en la interfaz de usuario.
     * Ejemplo: "Activo" en lugar de "ACTIVO"
     *
     * Se usa en:
     * - Tablas de inscripciones
     * - Filtros de búsqueda
     * - Reportes académicos
     */
    private final String nombreMostrar;

    /**
     * Descripción detallada del significado del estado.
     * Útil para:
     * - Tooltips en la interfaz
     * - Documentación del sistema
     * - Ayuda contextual para usuarios
     */
    private final String descripcion;

    /**
     * Clase CSS de Bootstrap para el badge (etiqueta) en la UI.
     * Permite mostrar el estado con colores distintivos:
     * - ACTIVO: Verde (success)
     * - RETIRADO: Amarillo (warning)
     * - COMPLETADO: Azul (primary)
     *
     * Ejemplo en HTML:
     * <span class="badge badge-success">Activo</span>
     */
    private final String claseCssBadge;

    // =========================================================================
    // CONSTRUCTOR
    // =========================================================================

    /**
     * Constructor privado del enum.
     *
     * @param nombreMostrar Nombre legible para la interfaz
     * @param descripcion Descripción del estado
     * @param claseCssBadge Clase CSS de Bootstrap para el badge
     */
    EstadoInscripcion(String nombreMostrar, String descripcion, String claseCssBadge) {
        this.nombreMostrar = nombreMostrar;
        this.descripcion = descripcion;
        this.claseCssBadge = claseCssBadge;
    }

    // =========================================================================
    // GETTERS
    // =========================================================================

    /**
     * Obtiene el nombre legible del estado.
     *
     * @return Nombre del estado para mostrar en UI
     */
    public String getNombreMostrar() {
        return nombreMostrar;
    }

    /**
     * Obtiene la descripción del estado.
     *
     * @return Descripción detallada del estado
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Obtiene la clase CSS de Bootstrap para el badge.
     *
     * Uso en Thymeleaf:
     * <span th:class="${'badge ' + inscripcion.estado.claseCssBadge}"
     *       th:text="${inscripcion.estado.nombreMostrar}">
     * </span>
     *
     * @return Clase CSS (ej: "badge-success", "badge-warning")
     */
    public String getClaseCssBadge() {
        return claseCssBadge;
    }

    // =========================================================================
    // MÉTODOS DE VALIDACIÓN
    // =========================================================================

    /**
     * Verifica si el estado es ACTIVO.
     *
     * Útil para validaciones:
     * - Permitir registro de calificaciones solo si está activo
     * - Mostrar botón de "Retirar" solo si está activo
     * - Filtrar estudiantes activos en un curso
     *
     * @return true si el estado es ACTIVO, false en caso contrario
     */
    public boolean esActivo() {
        return this == ACTIVO;
    }

    /**
     * Verifica si el estado es RETIRADO.
     *
     * @return true si el estado es RETIRADO, false en caso contrario
     */
    public boolean esRetirado() {
        return this == RETIRADO;
    }

    /**
     * Verifica si el estado es COMPLETADO.
     *
     * Útil para:
     * - Calcular estadísticas de finalización
     * - Bloquear cambios en calificaciones
     * - Generar certificados académicos
     *
     * @return true si el estado es COMPLETADO, false en caso contrario
     */
    public boolean esCompletado() {
        return this == COMPLETADO;
    }

    /**
     * Verifica si el estado es final (RETIRADO o COMPLETADO).
     *
     * Los estados finales no pueden cambiar a otros estados.
     * Útil para validar transiciones de estado.
     *
     * Ejemplo de validación:
     * if (inscripcion.getEstado().esFinal()) {
     *     throw new IllegalStateException("No se puede modificar una inscripción finalizada");
     * }
     *
     * @return true si es RETIRADO o COMPLETADO, false si es ACTIVO
     */
    public boolean esFinal() {
        return this == RETIRADO || this == COMPLETADO;
    }

    /**
     * Verifica si se puede registrar una calificación en este estado.
     *
     * Solo se permiten calificaciones si el estado es ACTIVO.
     * Los estados RETIRADO y COMPLETADO no aceptan nuevas calificaciones.
     *
     * Uso en servicio de calificaciones:
     * if (!inscripcion.getEstado().puedeRegistrarCalificacion()) {
     *     throw new EstadoInvalidoException("No se puede calificar una inscripción no activa");
     * }
     *
     * @return true si el estado permite registrar calificaciones
     */
    public boolean puedeRegistrarCalificacion() {
        return this == ACTIVO;
    }

    /**
     * Verifica si la inscripción se puede retirar.
     *
     * Solo las inscripciones ACTIVAS pueden retirarse.
     *
     * @return true si el estudiante puede retirarse del curso
     */
    public boolean puedeRetirarse() {
        return this == ACTIVO;
    }

    // =========================================================================
    // MÉTODO TOSTRING
    // =========================================================================

    /**
     * Método toString personalizado.
     * Devuelve el nombre legible del estado.
     *
     * @return Nombre legible del estado
     */
    @Override
    public String toString() {
        return nombreMostrar;
    }
}