package com.sistema.academico.infraestructura.excepcion;

/**
 * Excepción lanzada cuando se intenta crear un recurso que ya existe
 *
 * Casos de uso:
 * - Crear un Estudiante con email duplicado
 * - Crear una Materia con código duplicado
 * - Crear un Departamento con nombre duplicado
 *
 * HTTP Status: 409 CONFLICT
 */
public class RecursoDuplicadoException extends RuntimeException {

    /**
     * Constructor con mensaje personalizado
     *
     * @param mensaje Descripción del recurso duplicado
     */
    public RecursoDuplicadoException(String mensaje) {
        super(mensaje);
    }

    /**
     * Constructor con mensaje y causa
     *
     * @param mensaje Descripción del error
     * @param causa Excepción original
     */
    public RecursoDuplicadoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}