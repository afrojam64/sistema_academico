package com.sistema.academico.dominio.entidad;

import com.sistema.academico.dominio.enumeracion.Estado;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "cursos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "El nombre del curso es obligatorio")
    @Size(min = 3, max = 100)
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "El código es obligatorio")
    @Size(min = 3, max = 20)
    @Column(name = "codigo", nullable = false, unique = true, length = 20)
    private String codigo;

    @NotNull(message = "La materia es obligatoria")
    @ManyToOne
    @JoinColumn(name = "materia_id", nullable = false)
    private Materia materia;

    @NotNull(message = "El profesor es obligatorio")
    @ManyToOne
    @JoinColumn(name = "profesor_id", nullable = false)
    private Profesor profesor;

    @NotNull(message = "El cupo máximo es obligatorio")
    @Min(value = 1, message = "Debe tener al menos 1 cupo")
    @Max(value = 100, message = "No puede tener más de 100 cupos")
    @Column(name = "cupo_maximo", nullable = false)
    private Integer cupoMaximo;

    @Column(name = "cupo_actual", nullable = false)
    private Integer cupoActual;

    @NotBlank(message = "El periodo es obligatorio")
    @Size(max = 20)
    @Column(name = "periodo", nullable = false, length = 20)
    private String periodo;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private Estado estado;

    @PrePersist
    protected void onCreate() {
        if (this.estado == null) {
            this.estado = Estado.ACTIVO;
        }
        if (this.cupoActual == null) {
            this.cupoActual = 0;
        }
    }

    public boolean estaActivo() {
        return this.estado != null && this.estado.esActivo();
    }

    public boolean tieneCuposDisponibles() {
        return this.cupoActual < this.cupoMaximo;
    }

    public Integer getCuposDisponibles() {
        return this.cupoMaximo - this.cupoActual;
    }

    public void incrementarCupo() {
        if (this.cupoActual < this.cupoMaximo) {
            this.cupoActual++;
        }
    }

    public void decrementarCupo() {
        if (this.cupoActual > 0) {
            this.cupoActual--;
        }
    }
}