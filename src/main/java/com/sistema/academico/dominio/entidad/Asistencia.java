package com.sistema.academico.dominio.entidad;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Entidad que representa la asistencia de un estudiante a una clase en una fecha específica.
 *
 * UBICACIÓN EN ARQUITECTURA:
 * - Capa: DOMINIO
 * - Paquete: dominio.entidad
 *
 * @author Sistema Académico
 * @version 1.0
 */
@Entity
@Table(name = "asistencias", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"inscripcion_id", "fecha"})
})
public class Asistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inscripcion_id", nullable = false)
    private Inscripcion inscripcion;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private boolean presente;

    @Column(length = 255)
    private String observaciones;

    public Asistencia() {
    }

    public Asistencia(Inscripcion inscripcion, LocalDate fecha, boolean presente, String observaciones) {
        this.inscripcion = inscripcion;
        this.fecha = fecha;
        this.presente = presente;
        this.observaciones = observaciones;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Inscripcion getInscripcion() {
        return inscripcion;
    }

    public void setInscripcion(Inscripcion inscripcion) {
        this.inscripcion = inscripcion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public boolean isPresente() {
        return presente;
    }

    public void setPresente(boolean presente) {
        this.presente = presente;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}