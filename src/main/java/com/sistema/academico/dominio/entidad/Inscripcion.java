package com.sistema.academico.dominio.entidad;

import com.sistema.academico.dominio.enumeracion.EstadoInscripcion;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "inscripciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull(message = "El estudiante es obligatorio")
    @ManyToOne
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @NotNull(message = "El curso es obligatorio")
    @ManyToOne
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    @Column(name = "fecha_inscripcion", nullable = false)
    private LocalDate fechaInscripcion;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoInscripcion estado;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        if (this.fechaInscripcion == null) {
            this.fechaInscripcion = LocalDate.now();
        }
        if (this.estado == null) {
            this.estado = EstadoInscripcion.ACTIVO;
        }
        this.fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    public boolean estaActiva() {
        return this.estado != null && this.estado.esActivo();
    }

    public boolean puedeRetirar() {
        return this.estado != null && this.estado.puedeRetirarse();
    }

    public void retirar() {
        if (this.estado.puedeRetirarse()) {
            this.estado = EstadoInscripcion.RETIRADO;
        }
    }

    public void completar() {
        if (this.estado.esActivo()) {
            this.estado = EstadoInscripcion.COMPLETADO;
        }
    }
}