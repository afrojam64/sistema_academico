package com.sistema.academico.dominio.entidad;

import com.sistema.academico.dominio.enumeracion.Estado;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "materias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Materia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "El nombre de la materia es obligatorio")
    @Size(min = 3, max = 100)
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "El código es obligatorio")
    @Size(min = 2, max = 20)
    @Column(name = "codigo", nullable = false, unique = true, length = 20)
    private String codigo;

    @Size(max = 500)
    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @NotNull(message = "Los créditos son obligatorios")
    @Min(value = 1, message = "Debe tener al menos 1 crédito")
    @Max(value = 10, message = "No puede tener más de 10 créditos")
    @Column(name = "creditos", nullable = false)
    private Integer creditos;

    @NotNull(message = "El profesor responsable es obligatorio")
    @ManyToOne
    @JoinColumn(name = "profesor_id", nullable = false)
    private Profesor profesor;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private Estado estado;

    @PrePersist
    protected void onCreate() {
        if (this.estado == null) {
            this.estado = Estado.ACTIVO;
        }
    }

    public boolean estaActiva() {
        return this.estado != null && this.estado.esActivo();
    }
}