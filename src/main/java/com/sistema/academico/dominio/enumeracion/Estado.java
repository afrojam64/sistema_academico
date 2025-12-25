package com.sistema.academico.dominio.enumeracion;

/**
 * Enumeración general que define el estado activo/inactivo de las entidades del sistema.
 *
 * Este enum es reutilizable y se aplica a múltiples entidades que necesitan
 * gestionar su disponibilidad o vigencia sin eliminarlas físicamente de la base de datos.
 *
 * Estados disponibles:
 * - ACTIVO: La entidad está disponible y operativa
 * - INACTIVO: La entidad existe pero no está disponible para operaciones
 *
 * USADO POR LAS SIGUIENTES ENTIDADES:
 * - Usuario: Cuenta activa/suspendida
 * - Profesor: Profesor en ejercicio/cesado
 * - Estudiante: Estudiante activo/graduado/retirado
 * - Departamento: Departamento operativo/cerrado
 * - Materia: Materia que se dicta/no se dicta
 * - Curso: Curso abierto/cerrado
 *
 * NOTA: Las inscripciones usan EstadoInscripcion porque requieren
 * estados más específicos (ACTIVO, RETIRADO, COMPLETADO) con lógica de negocio diferente.
 *
 * UBICACIÓN EN ARQUITECTURA:
 * - Capa: DOMINIO
 * - Paquete: dominio.enumeracion
 * - Tipo: Enum general reutilizable
 *
 * @author Sistema Académico
 * @version 1.0
 */
public enum Estado {

    /**
     * Estado ACTIVO: La entidad está disponible y operativa.
     *
     * Significado según la entidad:
     *
     * Usuario ACTIVO:
     * - Puede iniciar sesión en el sistema
     * - Tiene acceso a las funcionalidades según su rol
     * - Aparece en búsquedas y listados
     *
     * Profesor ACTIVO:
     * - Puede ser asignado a materias y cursos
     * - Aparece en listados de profesores disponibles
     * - Puede registrar calificaciones
     *
     * Estudiante ACTIVO:
     * - Puede inscribirse en cursos
     * - Aparece en listados de estudiantes
     * - Puede recibir calificaciones
     *
     * Departamento ACTIVO:
     * - Está operativo
     * - Puede tener profesores asignados
     * - Aparece en listados y reportes
     *
     * Materia ACTIVA:
     * - Se está dictando actualmente
     * - Puede tener cursos asociados
     * - Disponible para inscripciones
     *
     * Curso ACTIVO:
     * - Está abierto para inscripciones o en progreso
     * - Los estudiantes pueden matricularse
     * - El profesor puede registrar calificaciones
     */
    ACTIVO(
            "Activo",
            "Disponible y operativo en el sistema",
            "badge-success",      // Verde en Bootstrap
            "text-success",       // Texto verde
            "bg-success-subtle"   // Fondo verde claro
    ),

    /**
     * Estado INACTIVO: La entidad existe pero no está disponible.
     *
     * Significado según la entidad:
     *
     * Usuario INACTIVO:
     * - No puede iniciar sesión
     * - Cuenta suspendida o deshabilitada
     * - Se mantiene el historial pero sin acceso
     *
     * Profesor INACTIVO:
     * - Ya no trabaja en la institución
     * - No puede ser asignado a nuevos cursos
     * - Se mantiene el historial de cursos dictados
     *
     * Estudiante INACTIVO:
     * - Graduado, retirado o suspendido
     * - No puede inscribirse en nuevos cursos
     * - Se mantiene el historial académico
     *
     * Departamento INACTIVO:
     * - Cerrado temporalmente o permanentemente
     * - No acepta nuevos profesores
     * - Los profesores existentes quedan sin departamento activo
     *
     * Materia INACTIVA:
     * - Ya no se dicta
     * - No se pueden crear nuevos cursos
     * - Se mantiene el historial de cursos anteriores
     *
     * Curso INACTIVO:
     * - Finalizado o cancelado
     * - No acepta nuevas inscripciones
     * - Se mantienen las inscripciones y calificaciones existentes
     *
     * IMPORTANTE: Inactivar NO es eliminar.
     * - Los datos se conservan en la base de datos
     * - El historial y relaciones se mantienen
     * - Se puede reactivar si es necesario
     */
    INACTIVO(
            "Inactivo",
            "No disponible u operativo en el sistema",
            "badge-secondary",    // Gris en Bootstrap
            "text-secondary",     // Texto gris
            "bg-secondary-subtle" // Fondo gris claro
    );

    // =========================================================================
    // ATRIBUTOS DEL ENUM
    // =========================================================================

    /**
     * Nombre legible del estado para mostrar en la interfaz de usuario.
     * Ejemplo: "Activo" en lugar de "ACTIVO"
     *
     * Se usa en:
     * - Tablas de listados
     * - Filtros de búsqueda
     * - Reportes y estadísticas
     * - Labels en formularios
     */
    private final String nombreMostrar;

    /**
     * Descripción general del significado del estado.
     *
     * Útil para:
     * - Tooltips en la interfaz
     * - Mensajes de ayuda contextual
     * - Documentación del sistema
     */
    private final String descripcion;

    /**
     * Clase CSS de Bootstrap para badges (etiquetas).
     * Permite mostrar el estado con colores distintivos:
     * - ACTIVO: badge-success (verde)
     * - INACTIVO: badge-secondary (gris)
     *
     * Ejemplo en HTML con Thymeleaf:
     * <span th:class="${'badge ' + entidad.estado.claseCssBadge}"
     *       th:text="${entidad.estado.nombreMostrar}">
     * </span>
     */
    private final String claseCssBadge;

    /**
     * Clase CSS de Bootstrap para texto.
     * Permite colorear texto según el estado:
     * - ACTIVO: text-success (verde)
     * - INACTIVO: text-secondary (gris)
     *
     * Ejemplo:
     * <span th:class="${usuario.estado.claseCssTexto}">
     *     Usuario está activo
     * </span>
     */
    private final String claseCssTexto;

    /**
     * Clase CSS de Bootstrap para fondo sutil.
     * Permite resaltar filas o secciones según el estado:
     * - ACTIVO: bg-success-subtle (verde claro)
     * - INACTIVO: bg-secondary-subtle (gris claro)
     *
     * Ejemplo en tabla:
     * <tr th:class="${estudiante.estado.claseCssFondo}">
     *     <td>...</td>
     * </tr>
     */
    private final String claseCssFondo;

    // =========================================================================
    // CONSTRUCTOR
    // =========================================================================

    /**
     * Constructor privado del enum.
     *
     * @param nombreMostrar Nombre legible para la interfaz
     * @param descripcion Descripción del estado
     * @param claseCssBadge Clase CSS para badges
     * @param claseCssTexto Clase CSS para texto
     * @param claseCssFondo Clase CSS para fondo
     */
    Estado(String nombreMostrar, String descripcion, String claseCssBadge,
           String claseCssTexto, String claseCssFondo) {
        this.nombreMostrar = nombreMostrar;
        this.descripcion = descripcion;
        this.claseCssBadge = claseCssBadge;
        this.claseCssTexto = claseCssTexto;
        this.claseCssFondo = claseCssFondo;
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
     * @return Clase CSS (ej: "badge-success", "badge-secondary")
     */
    public String getClaseCssBadge() {
        return claseCssBadge;
    }

    /**
     * Obtiene la clase CSS de Bootstrap para texto.
     *
     * @return Clase CSS (ej: "text-success", "text-secondary")
     */
    public String getClaseCssTexto() {
        return claseCssTexto;
    }

    /**
     * Obtiene la clase CSS de Bootstrap para fondo.
     *
     * @return Clase CSS (ej: "bg-success-subtle", "bg-secondary-subtle")
     */
    public String getClaseCssFondo() {
        return claseCssFondo;
    }

    // =========================================================================
    // MÉTODOS DE VALIDACIÓN
    // =========================================================================

    /**
     * Verifica si el estado es ACTIVO.
     *
     * Útil para validaciones de negocio:
     * - Verificar si un usuario puede iniciar sesión
     * - Comprobar si un profesor puede recibir asignaciones
     * - Validar si un estudiante puede inscribirse
     * - Verificar si un curso acepta inscripciones
     *
     * Ejemplo de uso:
     * if (usuario.getEstado().esActivo()) {
     *     // Permitir login
     * } else {
     *     throw new CuentaInactivaException("La cuenta está deshabilitada");
     * }
     *
     * @return true si el estado es ACTIVO, false en caso contrario
     */
    public boolean esActivo() {
        return this == ACTIVO;
    }

    /**
     * Verifica si el estado es INACTIVO.
     *
     * Útil para:
     * - Filtrar entidades inactivas en listados
     * - Mostrar advertencias en la interfaz
     * - Validar operaciones no permitidas
     *
     * Ejemplo:
     * if (profesor.getEstado().esInactivo()) {
     *     throw new IllegalStateException("No se puede asignar un profesor inactivo");
     * }
     *
     * @return true si el estado es INACTIVO, false en caso contrario
     */
    public boolean esInactivo() {
        return this == INACTIVO;
    }

    /**
     * Verifica si la entidad puede realizar operaciones.
     *
     * Alias de esActivo() para mayor claridad semántica en el código.
     *
     * Ejemplo:
     * if (!estudiante.getEstado().puedeOperar()) {
     *     return "redirect:/estudiante/cuenta-inactiva";
     * }
     *
     * @return true si puede operar (está activo)
     */
    public boolean puedeOperar() {
        return esActivo();
    }

    /**
     * Verifica si la entidad está disponible para asignaciones o inscripciones.
     *
     * @return true si está disponible (activo)
     */
    public boolean estaDisponible() {
        return esActivo();
    }

    // =========================================================================
    // MÉTODOS DE UTILIDAD
    // =========================================================================

    /**
     * Alterna el estado entre ACTIVO e INACTIVO.
     *
     * Útil para botones de activar/desactivar:
     * - Si está ACTIVO, pasa a INACTIVO
     * - Si está INACTIVO, pasa a ACTIVO
     *
     * Ejemplo de uso en servicio:
     * public void cambiarEstadoUsuario(Long id) {
     *     Usuario usuario = usuarioRepository.findById(id).orElseThrow();
     *     usuario.setEstado(usuario.getEstado().alternar());
     *     usuarioRepository.save(usuario);
     * }
     *
     * @return El estado opuesto al actual
     */
    public Estado alternar() {
        return this == ACTIVO ? INACTIVO : ACTIVO;
    }

    /**
     * Obtiene el estado opuesto.
     * Alias de alternar() para mayor claridad.
     *
     * @return El estado contrario
     */
    public Estado opuesto() {
        return alternar();
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