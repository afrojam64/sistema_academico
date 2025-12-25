package com.sistema.academico.dominio.entidad;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "calificaciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Calificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull(message = "La inscripción es obligatoria")
    @ManyToOne
    @JoinColumn(name = "inscripcion_id", nullable = false)
    private Inscripcion inscripcion;

    @NotBlank(message = "El nombre de la evaluación es obligatorio")
    @Size(min = 3, max = 100)
    @Column(name = "nombre_evaluacion", nullable = false, length = 100)
    private String nombreEvaluacion;

    @NotNull(message = "La nota es obligatoria")
    @DecimalMin(value = "0.0", message = "La nota mínima es 0.0")
    @DecimalMax(value = "5.0", message = "La nota máxima es 5.0")
    @Column(name = "nota", nullable = false, precision = 3, scale = 1)
    private BigDecimal nota;

    @NotNull(message = "El porcentaje es obligatorio")
    @Min(value = 1, message = "El porcentaje mínimo es 1")
    @Max(value = 100, message = "El porcentaje máximo es 100")
    @Column(name = "porcentaje", nullable = false)
    private Integer porcentaje;

    @Column(name = "fecha_calificacion")
    private LocalDate fechaCalificacion;

    @Size(max = 500)
    @Column(name = "observaciones", length = 500)
    private String observaciones;

    @PrePersist
    protected void onCreate() {
        if (this.fechaCalificacion == null) {
            this.fechaCalificacion = LocalDate.now();
        }
    }

    public boolean esAprobada() {
        return this.nota != null && this.nota.compareTo(new BigDecimal("3.0")) >= 0;
    }

    public BigDecimal getNotaPonderada() {
        if (this.nota == null || this.porcentaje == null) {
            return BigDecimal.ZERO;
        }
        return this.nota.multiply(new BigDecimal(this.porcentaje))
                .divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
    }
}