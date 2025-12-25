package com.sistema.academico.dominio.enumeracion;

/**
 * Enumeración que define los roles de usuario en el sistema.
 *
 * Los roles determinan los permisos y accesos que tiene cada usuario:
 * - ADMIN: Acceso total al sistema, puede gestionar todas las entidades
 * - PROFESOR: Puede ver sus cursos y gestionar calificaciones de sus estudiantes
 * - ESTUDIANTE: Puede ver sus cursos, calificaciones e historial académico
 *
 * UBICACIÓN EN ARQUITECTURA:
 * - Capa: DOMINIO
 * - Paquete: dominio.enumeracion
 * - Usado por: Entidad Usuario, SecurityConfig, Controllers
 *
 * @author Sistema Académico
 * @version 1.0
 */
public enum Rol {

    /**
     * Rol de Administrador del sistema.
     * Permisos:
     * - CRUD completo de todas las entidades
     * - Acceso a todos los reportes y estadísticas
     * - Gestión de usuarios (crear profesores y estudiantes)
     * - Asignación de cursos y materias
     */
    ADMIN("Administrador", "Control total del sistema"),

    /**
     * Rol de Profesor.
     * Permisos:
     * - Ver sus materias y cursos asignados
     * - Registrar y editar calificaciones de sus estudiantes
     * - Ver estadísticas de sus cursos
     * - Consultar lista de estudiantes inscritos en sus cursos
     */
    PROFESOR("Profesor", "Gestión de cursos y calificaciones"),

    /**
     * Rol de Estudiante.
     * Permisos:
     * - Ver sus cursos inscritos
     * - Consultar sus calificaciones
     * - Ver su historial académico y promedio
     * - Consultar información de profesores de sus cursos
     */
    ESTUDIANTE("Estudiante", "Consulta de información académica");

    // =========================================================================
    // ATRIBUTOS DEL ENUM
    // =========================================================================

    /**
     * Nombre legible del rol para mostrar en la interfaz de usuario.
     * Ejemplo: "Administrador" en lugar de "ADMIN"
     *
     * Se usa en:
     * - Vistas Thymeleaf para mostrar al usuario
     * - Mensajes de logs
     * - Reportes
     */
    private final String nombreMostrar;

    /**
     * Descripción breve del rol y sus responsabilidades.
     * Se puede usar en:
     * - Tooltips en la interfaz
     * - Páginas de ayuda
     * - Documentación del sistema
     */
    private final String descripcion;

    // =========================================================================
    // CONSTRUCTOR
    // =========================================================================

    /**
     * Constructor privado del enum.
     * Los enums en Java tienen constructores privados por diseño.
     * No se pueden crear instancias con 'new Rol()', las instancias ya existen
     * como constantes (ADMIN, PROFESOR, ESTUDIANTE).
     *
     * @param nombreMostrar Nombre legible para mostrar en UI
     * @param descripcion Descripción de las responsabilidades del rol
     */
    Rol(String nombreMostrar, String descripcion) {
        this.nombreMostrar = nombreMostrar;
        this.descripcion = descripcion;
    }

    // =========================================================================
    // GETTERS
    // =========================================================================

    /**
     * Obtiene el nombre legible del rol.
     *
     * Ejemplo de uso en vista Thymeleaf:
     * <span th:text="${usuario.rol.nombreMostrar}"></span>
     *
     * @return Nombre del rol para mostrar en interfaz (ej: "Administrador")
     */
    public String getNombreMostrar() {
        return nombreMostrar;
    }

    /**
     * Obtiene la descripción del rol.
     *
     * @return Descripción de las responsabilidades del rol
     */
    public String getDescripcion() {
        return descripcion;
    }

    // =========================================================================
    // MÉTODOS DE VALIDACIÓN
    // =========================================================================

    /**
     * Verifica si el rol es de tipo ADMIN.
     *
     * Útil para validaciones de permisos en el código:
     * if (usuario.getRol().esAdmin()) {
     *     // Permitir acceso a funcionalidad administrativa
     * }
     *
     * @return true si el rol es ADMIN, false en caso contrario
     */
    public boolean esAdmin() {
        return this == ADMIN;
    }

    /**
     * Verifica si el rol es de tipo PROFESOR.
     *
     * Ejemplo de uso en servicio:
     * if (usuario.getRol().esProfesor()) {
     *     return cursoService.obtenerCursosDelProfesor(usuario.getId());
     * }
     *
     * @return true si el rol es PROFESOR, false en caso contrario
     */
    public boolean esProfesor() {
        return this == PROFESOR;
    }

    /**
     * Verifica si el rol es de tipo ESTUDIANTE.
     *
     * @return true si el rol es ESTUDIANTE, false en caso contrario
     */
    public boolean esEstudiante() {
        return this == ESTUDIANTE;
    }

    // =========================================================================
    // INTEGRACIÓN CON SPRING SECURITY
    // =========================================================================

    /**
     * Obtiene el nombre del rol con prefijo "ROLE_" para Spring Security.
     *
     * Spring Security internamente maneja los roles con el prefijo "ROLE_".
     * Este método facilita la integración con las anotaciones de seguridad.
     *
     * Ejemplo de transformación:
     * - ADMIN       → "ROLE_ADMIN"
     * - PROFESOR    → "ROLE_PROFESOR"
     * - ESTUDIANTE  → "ROLE_ESTUDIANTE"
     *
     * Uso en SecurityConfig:
     * authorities.add(new SimpleGrantedAuthority(rol.getRolParaSpringSecurity()));
     *
     * Uso en anotaciones:
     * @PreAuthorize("hasRole('ADMIN')") // Spring Security busca ROLE_ADMIN internamente
     *
     * @return Nombre del rol con prefijo ROLE_ para usar en Spring Security
     */
    public String getRolParaSpringSecurity() {
        return "ROLE_" + this.name();
    }

    // =========================================================================
    // MÉTODO TOSTRING
    // =========================================================================

    /**
     * Método toString personalizado.
     * Devuelve el nombre legible del rol en lugar del nombre de la constante.
     *
     * Comportamiento:
     * - Sin override: Rol.ADMIN.toString() → "ADMIN"
     * - Con override:  Rol.ADMIN.toString() → "Administrador"
     *
     * Útil cuando se imprime el rol en logs o se usa en mensajes.
     *
     * @return Nombre legible del rol
     */
    @Override
    public String toString() {
        return nombreMostrar;
    }
}