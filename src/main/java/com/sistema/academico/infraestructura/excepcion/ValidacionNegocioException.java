package com.sistema.academico.infraestructura.excepcion;

/**
 * Excepción lanzada cuando una regla de negocio no se cumple
 *
 * Casos de uso:
 * - El curso no tiene cupos disponibles
 * - El estudiante ya está inscrito en el curso
 * - El porcentaje de calificaciones excede 100%
 * - La fecha de fin es anterior a la fecha de inicio
 * - El usuario no tiene el rol correcto
 *
 * HTTP Status: 400 BAD REQUEST
 */
public class ValidacionNegocioException extends RuntimeException {

    /**
     * Constructor con mensaje personalizado
     *
     * @param mensaje Descripción de la regla de negocio violada
     */
    public ValidacionNegocioException(String mensaje) {
        super(mensaje);
    }

    /**
     * Constructor con mensaje y causa
     *
     * @param mensaje Descripción del error
     * @param causa Excepción original
     */
    public ValidacionNegocioException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}