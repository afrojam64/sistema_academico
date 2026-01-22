package com.sistema.academico.dominio.enumeracion;

/**
 * Enumeración que define los días de la semana para los horarios de clase.
 *
 * UBICACIÓN EN ARQUITECTURA:
 * - Capa: DOMINIO
 * - Paquete: dominio.enumeracion
 * - Usado por: Entidad Horario
 *
 * @author Sistema Académico
 * @version 1.0
 */
public enum DiaSemana {
    LUNES("Lunes"),
    MARTES("Martes"),
    MIERCOLES("Miércoles"),
    JUEVES("Jueves"),
    VIERNES("Viernes"),
    SABADO("Sábado"),
    DOMINGO("Domingo");

    private final String nombreMostrar;

    DiaSemana(String nombreMostrar) {
        this.nombreMostrar = nombreMostrar;
    }

    public String getNombreMostrar() {
        return nombreMostrar;
    }
}