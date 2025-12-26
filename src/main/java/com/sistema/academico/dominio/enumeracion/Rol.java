package com.sistema.academico.dominio.enumeracion;

/**
 * Enumeración que define los roles de usuario en el sistema.
 *
 * Los roles determinan los permisos y accesos que tiene cada usuario:
 * - SUPER_ADMIN: Acceso total incluyendo eliminación física de registros
 * - ADMIN: Acceso total al sistema, puede gestionar todas las entidades (soft delete)
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
     * Rol de Super Administrador del sistema.
     * Permisos:
     * - CRUD completo de todas las entidades
     * - Acceso a todos los reportes y estadísticas
     * - Gestión de usuarios (crear profesores, estudiantes y administradores)
     * - Asignación de cursos y materias
     * - ÚNICO ROL que puede ELIMINAR FÍSICAMENTE registros de la BD (hard delete)
     * - Puede restaurar registros inactivos
     */
    SUPER_ADMIN("Super Administrador", "Control total del sistema con eliminación física"),

    /**
     * Rol de Administrador del sistema.
     * Permisos:
     * - CRUD completo de todas las entidades
     * - Acceso a todos los reportes y estadísticas
     * - Gestión de usuarios (crear profesores y estudiantes)
     * - Asignación de cursos y materias
     * - Solo puede desactivar registros (soft delete), NO eliminar físicamente
     */
    ADMIN("Administrador", "Control total del sistema"),

    /**
     * Rol de Profesor.
     * Permisos:
     * - Ver sus materias y cursos asignados
     * - Registrar y editar calificaciones de sus estudiantes
     * - Ver estadísticas de sus cursos
     * - Consultar lista de estudiantes inscritos en sus cursos
     * - NO puede eliminar ni desactivar registros
     */
    PROFESOR("Profesor", "Gestión de cursos y calificaciones"),

    /**
     * Rol de Estudiante.
     * Permisos:
     * - Ver sus cursos inscritos
     * - Consultar sus calificaciones
     * - Ver su historial académico y promedio
     * - Consultar información de profesores de sus cursos
     * - NO puede eliminar ni desactivar registros
     */
    ESTUDIANTE("Estudiante", "Consulta de información académica");

    // =========================================================================
    // ATRIBUTOS DEL ENUM
    // =========================================================================

    private final String nombreMostrar;
    private final String descripcion;

    // =========================================================================
    // CONSTRUCTOR
    // =========================================================================

    Rol(String nombreMostrar, String descripcion) {
        this.nombreMostrar = nombreMostrar;
        this.descripcion = descripcion;
    }

    // =========================================================================
    // GETTERS
    // =========================================================================

    public String getNombreMostrar() {
        return nombreMostrar;
    }

    public String getDescripcion() {
        return descripcion;
    }

    // =========================================================================
    // MÉTODOS DE VALIDACIÓN
    // =========================================================================

    /**
     * Verifica si el rol es de tipo SUPER_ADMIN.
     *
     * @return true si el rol es SUPER_ADMIN, false en caso contrario
     */
    public boolean esSuperAdmin() {
        return this == SUPER_ADMIN;
    }

    /**
     * Verifica si el rol es de tipo ADMIN.
     *
     * @return true si el rol es ADMIN, false en caso contrario
     */
    public boolean esAdmin() {
        return this == ADMIN;
    }

    /**
     * Verifica si el rol es de tipo PROFESOR.
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

    /**
     * Verifica si el rol tiene permisos administrativos (SUPER_ADMIN o ADMIN).
     *
     * @return true si es SUPER_ADMIN o ADMIN, false en caso contrario
     */
    public boolean esAdministrativo() {
        return this == SUPER_ADMIN || this == ADMIN;
    }

    /**
     * Verifica si el rol puede eliminar físicamente registros.
     * Solo SUPER_ADMIN tiene este permiso.
     *
     * @return true si puede hacer hard delete, false en caso contrario
     */
    public boolean puedeEliminarFisicamente() {
        return this == SUPER_ADMIN;
    }

    /**
     * Verifica si el rol puede desactivar registros (soft delete).
     * SUPER_ADMIN y ADMIN tienen este permiso.
     *
     * @return true si puede hacer soft delete, false en caso contrario
     */
    public boolean puedeDesactivar() {
        return this == SUPER_ADMIN || this == ADMIN;
    }

    // =========================================================================
    // INTEGRACIÓN CON SPRING SECURITY
    // =========================================================================

    public String getRolParaSpringSecurity() {
        return "ROLE_" + this.name();
    }

    // =========================================================================
    // MÉTODO TOSTRING
    // =========================================================================

    @Override
    public String toString() {
        return nombreMostrar;
    }
}