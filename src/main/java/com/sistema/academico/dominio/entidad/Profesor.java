package com.sistema.academico.dominio.entidad;

import com.sistema.academico.dominio.enumeracion.Estado;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "profesores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profesor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull(message = "El usuario es obligatorio")
    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @Size(max = 20)
    @Column(name = "telefono", length = 20)
    private String telefono;

    @Size(max = 200)
    @Column(name = "especialidad", length = 200)
    private String especialidad;

    @NotNull(message = "El departamento es obligatorio")
    @ManyToOne
    @JoinColumn(name = "departamento_id", nullable = false)
    private Departamento departamento;

    @Column(name = "fecha_contratacion")
    private LocalDate fechaContratacion;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private Estado estado;

    @PrePersist
    protected void onCreate() {
        if (this.estado == null) {
            this.estado = Estado.ACTIVO;
        }
        if (this.fechaContratacion == null) {
            this.fechaContratacion = LocalDate.now();
        }
    }

    public boolean estaActivo() {
        return this.estado != null && this.estado.esActivo();
    }

    /**
     * Obtiene el nombre completo del profesor desde el usuario asociado
     */
    public String getNombreCompleto() {
        if (this.usuario != null) {
            return this.usuario.getNombre() + " " + this.usuario.getApellido();
        }
        return "";
    }
}