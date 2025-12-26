package com.sistema.academico.infraestructura.excepcion;

/**
 * Excepción lanzada cuando se intenta una operación que no está permitida
 *
 * Casos de uso:
 * - Eliminar un Departamento que tiene Profesores asignados
 * - Eliminar una Materia que tiene Cursos asociados
 * - Eliminar un Curso que tiene Inscripciones
 * - Retirar una inscripción COMPLETADA
 *
 * HTTP Status: 403 FORBIDDEN
 */
public class OperacionNoPermitidaException extends RuntimeException {

    /**
     * Constructor con mensaje personalizado
     *
     * @param mensaje Descripción de la operación no permitida
     */
    public OperacionNoPermitidaException(String mensaje) {
        super(mensaje);
    }

    /**
     * Constructor con mensaje y causa
     *
     * @param mensaje Descripción del error
     * @param causa Excepción original
     */
    public OperacionNoPermitidaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}