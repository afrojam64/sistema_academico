package com.sistema.academico.infraestructura.excepcion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Clase para estructurar las respuestas de error
 * Se envía al cliente cuando ocurre una excepción
 */
@Getter
@Setter
@AllArgsConstructor
@Builder
public class ErrorResponse {

    /**
     * Timestamp del momento del error
     * Ejemplo: "2024-12-25T15:30:45"
     */
    private LocalDateTime timestamp;

    /**
     * Código de estado HTTP
     * Ejemplo: 400 (Bad Request), 404 (Not Found), 500 (Internal Server Error)
     */
    private int status;

    /**
     * Nombre del error HTTP
     * Ejemplo: "Bad Request", "Not Found"
     */
    private String error;

    /**
     * Mensaje descriptivo del error
     * Ejemplo: "El email ya está registrado"
     */
    private String message;

    /**
     * Ruta del endpoint donde ocurrió el error
     * Ejemplo: "/api/estudiantes"
     */
    private String path;

    /**
     * Lista de errores de validación (opcional)
     * Usado cuando hay múltiples errores (validaciones de DTO)
     */
    private List<String> errors;
}