package com.sistema.academico.infraestructura.excepcion;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Manejador global de excepciones para toda la aplicación
 *
 * @RestControllerAdvice:
 * - Intercepta TODAS las excepciones lanzadas en Controllers
 * - Permite manejarlas de forma centralizada
 * - Convierte excepciones en respuestas HTTP estructuradas
 *
 * Ventajas:
 * ✅ Un solo lugar para manejar errores
 * ✅ Respuestas consistentes para el cliente
 * ✅ Códigos HTTP apropiados
 * ✅ Logs centralizados
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja excepciones de tipo RecursoNoEncontradoException
     *
     * Se lanza cuando:
     * - findById() no encuentra el registro
     * - findByEmail() no encuentra al usuario
     * - findByCodigo() no encuentra la materia
     *
     * HTTP Status: 404 NOT FOUND
     */
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleRecursoNoEncontrado(
            RecursoNoEncontradoException ex,
            WebRequest request
    ) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Maneja excepciones de tipo RecursoDuplicadoException
     *
     * Se lanza cuando:
     * - Email ya existe en BD
     * - Código de materia duplicado
     * - Matrícula de estudiante duplicada
     *
     * HTTP Status: 409 CONFLICT
     */
    @ExceptionHandler(RecursoDuplicadoException.class)
    public ResponseEntity<ErrorResponse> handleRecursoDuplicado(
            RecursoDuplicadoException ex,
            WebRequest request
    ) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /**
     * Maneja excepciones de validación de negocio
     *
     * Se lanza cuando:
     * - No hay cupos disponibles
     * - Estudiante ya inscrito
     * - Porcentaje excede 100%
     * - Fechas inválidas
     *
     * HTTP Status: 400 BAD REQUEST
     */
    @ExceptionHandler(ValidacionNegocioException.class)
    public ResponseEntity<ErrorResponse> handleValidacionNegocio(
            ValidacionNegocioException ex,
            WebRequest request
    ) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja excepciones de estado inválido
     *
     * Se lanza cuando:
     * - Intentar operar sobre entidad INACTIVA
     * - Calificar inscripción RETIRADA
     *
     * HTTP Status: 400 BAD REQUEST
     */
    @ExceptionHandler(EstadoInvalidoException.class)
    public ResponseEntity<ErrorResponse> handleEstadoInvalido(
            EstadoInvalidoException ex,
            WebRequest request
    ) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja excepciones de operación no permitida
     *
     * Se lanza cuando:
     * - Eliminar departamento con profesores
     * - Eliminar materia con cursos
     * - Retirar inscripción completada
     *
     * HTTP Status: 403 FORBIDDEN
     */
    @ExceptionHandler(OperacionNoPermitidaException.class)
    public ResponseEntity<ErrorResponse> handleOperacionNoPermitida(
            OperacionNoPermitidaException ex,
            WebRequest request
    ) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    /**
     * Maneja errores de validación de DTOs (@Valid)
     *
     * Se lanza cuando:
     * - @NotBlank falla (campo vacío)
     * - @Email falla (email inválido)
     * - @Min/@Max falla (fuera de rango)
     * - @Size falla (tamaño incorrecto)
     *
     * HTTP Status: 400 BAD REQUEST
     *
     * Retorna TODOS los errores de validación en una lista
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidacionDTO(
            MethodArgumentNotValidException ex,
            WebRequest request
    ) {
        // Extraer todos los errores de validación
        List<String> errores = new ArrayList<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errores.add(fieldName + ": " + errorMessage);
        });

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Error de validación en los datos enviados")
                .path(request.getDescription(false).replace("uri=", ""))
                .errors(errores)  // Lista de todos los errores
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja cualquier otra excepción no capturada
     *
     * Se lanza cuando:
     * - Errores inesperados
     * - NullPointerException
     * - Errores de BD
     * - Cualquier RuntimeException no manejada
     *
     * HTTP Status: 500 INTERNAL SERVER ERROR
     *
     * IMPORTANTE: En producción, NO exponer el stack trace completo
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleExcepcionGeneral(
            Exception ex,
            WebRequest request
    ) {
        // Log del error completo (para debugging)
        ex.printStackTrace();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("Ha ocurrido un error interno en el servidor")
                // En desarrollo: ex.getMessage()
                // En producción: mensaje genérico (no exponer detalles)
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}