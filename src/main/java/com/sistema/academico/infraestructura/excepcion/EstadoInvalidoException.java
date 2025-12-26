package com.sistema.academico.infraestructura.excepcion;

/**
 * Excepción lanzada cuando una entidad no está en el estado correcto para una operación
 *
 * Casos de uso:
 * - Intentar inscribir en un curso INACTIVO
 * - Asignar un profesor INACTIVO a una materia
 * - Calificar una inscripción RETIRADA
 * - Modificar un departamento INACTIVO
 *
 * HTTP Status: 400 BAD REQUEST
 */
public class EstadoInvalidoException extends RuntimeException {

    /**
     * Constructor con mensaje personalizado
     *
     * @param mensaje Descripción del estado inválido
     */
    public EstadoInvalidoException(String mensaje) {
        super(mensaje);
    }

    /**
     * Constructor con mensaje y causa
     *
     * @param mensaje Descripción del error
     * @param causa Excepción original
     */
    public EstadoInvalidoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}