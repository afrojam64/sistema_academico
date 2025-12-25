package com.sistema.academico.dominio.entidad;

import com.sistema.academico.dominio.enumeracion.Estado;
import com.sistema.academico.dominio.enumeracion.Rol;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad que representa un usuario del sistema.
 *
 * Esta entidad gestiona la autenticación y autorización en el sistema.
 * Cada usuario tiene credenciales únicas (nombreUsuario y contraseña) y un rol
 * que determina sus permisos (ADMIN, PROFESOR, ESTUDIANTE).
 *
 * RELACIONES:
 * - Un Usuario puede ser un Profesor (1:1 opcional)
 * - Un Usuario puede ser un Estudiante (1:1 opcional)
 * - Un Usuario tiene un Rol (enum)
 * - Un Usuario tiene un Estado (enum)
 *
 * TABLA EN BASE DE DATOS: usuario
 *
 * UBICACIÓN EN ARQUITECTURA:
 * - Capa: DOMINIO
 * - Paquete: dominio.entidad
 * - Tipo: Entidad JPA (mapea a tabla)
 *
 * @author Sistema Académico
 * @version 1.0
 */
@Entity
// @Entity: Marca esta clase como una entidad JPA que se mapeará a una tabla en la BD
// Spring Data JPA detectará automáticamente esta anotación y creará la tabla

@Table(name = "usuarios")
// @Table: Define el nombre de la tabla en la base de datos
// Si no se especifica, JPA usa el nombre de la clase en minúsculas
// Aquí lo ponemos explícito para claridad

@Getter
@Setter
// @Getter y @Setter: Lombok genera automáticamente todos los getters y setters
// Equivalente a escribir manualmente:
//   public Long getId() { return id; }
//   public void setId(Long id) { this.id = id; }
// Para TODOS los atributos

@NoArgsConstructor
// @NoArgsConstructor: Lombok genera un constructor sin parámetros
// Equivalente a: public Usuario() {}
// OBLIGATORIO para JPA (Hibernate lo necesita para instanciar objetos)

@AllArgsConstructor
// @AllArgsConstructor: Lombok genera un constructor con TODOS los parámetros
// Equivalente a: public Usuario(Long id, String nombreUsuario, String contraseña, ...)
// Útil para crear objetos en tests o código

@Builder
// @Builder: Lombok implementa el patrón Builder
// Permite crear objetos de forma más legible:
// Usuario usuario = Usuario.builder()
//                          .nombreUsuario("admin")
//                          .contraseña("123456")
//                          .rol(Rol.ADMIN)
//                          .build();

@ToString(exclude = {"contrasena"})
// @ToString: Genera el método toString() automáticamente
// exclude = {"contrasena"}: Excluye la contraseña por seguridad
// Al imprimir el objeto no se mostrará la contraseña en logs

public class Usuario {

    // =========================================================================
    // CLAVE PRIMARIA
    // =========================================================================

    @Id
    // @Id: Marca este campo como la clave primaria de la tabla
    // OBLIGATORIO: Toda entidad JPA debe tener un @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @GeneratedValue: Define cómo se genera el valor de la clave primaria
    // GenerationType.IDENTITY: La base de datos genera el ID automáticamente (AUTO_INCREMENT en MySQL, SERIAL en PostgreSQL)
    // Otras opciones:
    //   - GenerationType.AUTO: JPA elige la mejor estrategia
    //   - GenerationType.SEQUENCE: Usa secuencias de BD (Oracle, PostgreSQL)
    //   - GenerationType.TABLE: Usa una tabla separada para IDs

    @Column(name = "id")
    // @Column: Define propiedades de la columna en la tabla
    // name = "id": Nombre de la columna en la BD
    // Si no se especifica, JPA usa el nombre del atributo

    private Long id;
    // Long (con L mayúscula): Tipo wrapper, puede ser null
    // Importante: Antes de guardar en BD el id es null, después de guardar tiene valor

    // =========================================================================
    // CREDENCIALES DE ACCESO
    // =========================================================================

    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    // @NotBlank: Validación de Bean Validation
    // Verifica que el campo:
    //   - No sea null
    //   - No sea una cadena vacía ""
    //   - No sea solo espacios en blanco "   "
    // message: Mensaje de error personalizado si la validación falla

    @Size(min = 4, max = 50, message = "El nombre de usuario debe tener entre 4 y 50 caracteres")
    // @Size: Valida la longitud de la cadena
    // min = 4: Mínimo 4 caracteres
    // max = 50: Máximo 50 caracteres

    @Column(name = "nombre_usuario", nullable = false, unique = true, length = 50)
    // nullable = false: No permite valores NULL en la BD (equivalente a NOT NULL en SQL)
    // unique = true: Crea un índice único (no puede haber dos usuarios con el mismo nombre)
    // length = 50: Define el tamaño máximo de la columna VARCHAR(50)

    private String nombreUsuario;
    // Nombre de usuario para login (ej: "admin", "jperez", "mgarcia")
    // Debe ser único en todo el sistema

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    // En producción, la contraseña se almacenará encriptada (BCrypt)

    @Column(name = "contrasena", nullable = false, length = 255)
    // length = 255: Las contraseñas encriptadas con BCrypt ocupan ~60 caracteres
    // Ponemos 255 por seguridad y flexibilidad

    private String contrasena;
    // Contraseña del usuario (se guardará encriptada, NO en texto plano)
    // IMPORTANTE: Nunca almacenar contraseñas sin encriptar

    // =========================================================================
    // ROL Y PERMISOS
    // =========================================================================

    @NotNull(message = "El rol no puede ser nulo")
    // @NotNull: Solo valida que no sea null (pero puede ser vacío si fuera String)
    // Para Strings usar @NotBlank

    @Enumerated(EnumType.STRING)
    // @Enumerated: Indica cómo guardar el enum en la BD
    // EnumType.STRING: Guarda el nombre del enum como texto ("ADMIN", "PROFESOR", "ESTUDIANTE")
    //   Ventajas: Legible en la BD, no se rompe si reordenas los enums
    // EnumType.ORDINAL: Guardaría la posición numérica (0, 1, 2)
    //   Desventajas: Si reordenas los enums, los datos se corrompen
    // RECOMENDACIÓN: SIEMPRE usar STRING

    @Column(name = "rol", nullable = false, length = 20)
    // length = 20: Suficiente para "ADMIN", "PROFESOR", "ESTUDIANTE"

    private Rol rol;
    // Rol del usuario: ADMIN, PROFESOR o ESTUDIANTE
    // Determina los permisos y funcionalidades disponibles

    // =========================================================================
    // ESTADO DEL USUARIO
    // =========================================================================

    @NotNull(message = "El estado no puede ser nulo")
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private Estado estado;
    // Estado del usuario: ACTIVO o INACTIVO
    // - ACTIVO: Puede iniciar sesión y usar el sistema
    // - INACTIVO: Cuenta deshabilitada, no puede iniciar sesión
    // Útil para suspender cuentas sin eliminar el historial

    // =========================================================================
    // AUDITORÍA
    // =========================================================================

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    // updatable = false: Este campo NO se puede modificar después de crearse
    // Asegura que la fecha de creación nunca cambie

    private LocalDateTime fechaCreacion;
    // LocalDateTime: Clase de Java 8+ para fechas y horas
    // Incluye: año, mes, día, hora, minutos, segundos
    // Ejemplo: 2024-12-15T14:30:00
    // Más moderno que Date (obsoleto)

    @Column(name = "fecha_actualizacion")
    // updatable por defecto es true, se puede modificar

    private LocalDateTime fechaActualizacion;
    // Fecha de la última modificación del usuario
    // Se actualiza cada vez que se modifica el usuario

    // =========================================================================
    // MÉTODOS DE CICLO DE VIDA JPA
    // =========================================================================

    @PrePersist
    // @PrePersist: Método que se ejecuta AUTOMÁTICAMENTE antes de guardar (INSERT)
    // Se ejecuta una sola vez cuando el objeto es nuevo
    // Útil para inicializar valores por defecto

    protected void onCreate() {
        // Establece la fecha y hora actual al crear el usuario
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();

        // Si no se especificó un estado, por defecto es ACTIVO
        if (this.estado == null) {
            this.estado = Estado.ACTIVO;
        }
    }

    @PreUpdate
    // @PreUpdate: Método que se ejecuta AUTOMÁTICAMENTE antes de actualizar (UPDATE)
    // Se ejecuta cada vez que se modifica un usuario existente

    protected void onUpdate() {
        // Actualiza la fecha de modificación cada vez que se edita el usuario
        this.fechaActualizacion = LocalDateTime.now();
    }

    // =========================================================================
    // MÉTODOS DE NEGOCIO
    // =========================================================================

    /**
     * Verifica si el usuario está activo.
     *
     * Uso típico:
     * if (!usuario.estaActivo()) {
     *     throw new CuentaInactivaException("Su cuenta está deshabilitada");
     * }
     *
     * @return true si el estado es ACTIVO, false si es INACTIVO
     */
    public boolean estaActivo() {
        return this.estado != null && this.estado.esActivo();
    }

    /**
     * Verifica si el usuario tiene rol de Administrador.
     *
     * @return true si el rol es ADMIN
     */
    public boolean esAdmin() {
        return this.rol != null && this.rol.esAdmin();
    }

    /**
     * Verifica si el usuario tiene rol de Profesor.
     *
     * @return true si el rol es PROFESOR
     */
    public boolean esProfesor() {
        return this.rol != null && this.rol.esProfesor();
    }

    /**
     * Verifica si el usuario tiene rol de Estudiante.
     *
     * @return true si el rol es ESTUDIANTE
     */
    public boolean esEstudiante() {
        return this.rol != null && this.rol.esEstudiante();
    }

    /**
     * Activa la cuenta del usuario.
     * Cambia el estado a ACTIVO.
     */
    public void activar() {
        this.estado = Estado.ACTIVO;
    }

    /**
     * Desactiva la cuenta del usuario.
     * Cambia el estado a INACTIVO.
     * El usuario no podrá iniciar sesión.
     */
    public void desactivar() {
        this.estado = Estado.INACTIVO;
    }

    /**
     * Alterna el estado del usuario entre ACTIVO e INACTIVO.
     */
    public void cambiarEstado() {
        this.estado = this.estado.alternar();
    }
}