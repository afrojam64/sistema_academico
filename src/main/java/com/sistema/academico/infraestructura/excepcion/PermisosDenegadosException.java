package com.sistema.academico.infraestructura.excepcion;

public class PermisosDenegadosException extends RuntimeException {
    public PermisosDenegadosException(String mensaje) {
        super(mensaje);
    }
}