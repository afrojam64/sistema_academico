package com.sistema.academico.dominio.entidad;

import com.sistema.academico.dominio.enumeracion.Estado;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "estudiantes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull(message = "El usuario es obligatorio")
    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @NotBlank(message = "El código de estudiante es obligatorio")
    @Size(max = 20)
    @Column(name = "codigo_estudiante", nullable = false, unique = true, length = 20)
    private String codigoEstudiante;

    @Size(max = 100)
    @Column(name = "carrera", length = 100)
    private String carrera;

    @Column(name = "semestre")
    private Integer semestre;

    @Column(name = "fecha_ingreso")
    private LocalDate fechaIngreso;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private Estado estado;

    @PrePersist
    protected void onCreate() {
        if (this.estado == null) {
            this.estado = Estado.ACTIVO;
        }
        if (this.fechaIngreso == null) {
            this.fechaIngreso = LocalDate.now();
        }
    }

    public boolean estaActivo() {
        return this.estado != null && this.estado.esActivo();
    }

    /**
     * Obtiene el nombre completo del estudiante desde el usuario asociado
     */
    public String getNombreCompleto() {
        if (this.usuario != null) {
            return this.usuario.getNombre() + " " + this.usuario.getApellido();
        }
        return "";
    }
}