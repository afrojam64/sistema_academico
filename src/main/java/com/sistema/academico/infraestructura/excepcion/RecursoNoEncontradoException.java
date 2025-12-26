package com.sistema.academico.infraestructura.excepcion;

/**
 * Excepción lanzada cuando un recurso solicitado no existe en la BD
 *
 * Casos de uso:
 * - Buscar un Estudiante por ID que no existe
 * - Buscar una Materia por código que no existe
 *
 * HTTP Status: 404 NOT FOUND
 */
public class RecursoNoEncontradoException extends RuntimeException {

    /**
     * Constructor con mensaje personalizado
     *
     * @param mensaje Descripción del recurso no encontrado
     */
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }

    /**
     * Constructor con mensaje y causa
     *
     * @param mensaje Descripción del error
     * @param causa Excepción original que causó el error
     */
    public RecursoNoEncontradoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}