package com.sistema.academico.infraestructura.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Controlador para servir vistas HTML del sistema
 * Maneja la navegación entre páginas
 */
@Controller
public class ViewController {

    // ========================================
    // PÁGINAS PÚBLICAS
    // ========================================

    /**
     * Página de login
     * Ruta: http://localhost:8080/
     * Ruta: http://localhost:8080/login
     */
    @GetMapping({"/", "/login"})
    public String login() {
        return "login";
    }

    // ========================================
    // DASHBOARDS POR ROL
    // ========================================

    /**
     * Dashboard Administrador
     * Ruta: http://localhost:8080/dashboard/admin
     */
    @GetMapping("/dashboard/admin")
    public String dashboardAdmin() {
        return "dashboards/admin";
    }

    /**
     * Dashboard Super Administrador
     * Ruta: http://localhost:8080/dashboard/super-admin
     */
    @GetMapping("/dashboard/super-admin")
    public String dashboardSuperAdmin() {
        return "dashboards/super-admin";
    }

    /**
     * Dashboard Profesor
     * Ruta: http://localhost:8080/dashboard/profesor
     */
    @GetMapping("/dashboard/profesor")
    public String dashboardProfesor() {
        return "dashboards/profesor";
    }

    /**
     * Dashboard Estudiante
     * Ruta: http://localhost:8080/dashboard/estudiante
     */
    @GetMapping("/dashboard/estudiante")
    public String dashboardEstudiante() {
        return "dashboards/estudiante";
    }

    // ========================================
    // MÓDULO: USUARIOS
    // ========================================

    /**
     * Listar todos los usuarios
     * Ruta: http://localhost:8080/usuarios
     */
    @GetMapping("/usuarios")
    public String listarUsuarios() {
        return "usuarios/lista";
    }

    /**
     * Formulario para crear nuevo usuario
     * Ruta: http://localhost:8080/usuarios/nuevo
     */
    @GetMapping("/usuarios/nuevo")
    public String crearUsuario() {
        return "usuarios/crear";
    }

    /**
     * Formulario para editar usuario
     * Ruta: http://localhost:8080/usuarios/editar/{id}
     */
    @GetMapping("/usuarios/editar/{id}")
    public String editarUsuario(@PathVariable Long id) {
        return "usuarios/editar";
    }

    /**
     * Ver detalles de un usuario
     * Ruta: http://localhost:8080/usuarios/ver/{id}
     */
    @GetMapping("/usuarios/ver/{id}")
    public String verUsuario(@PathVariable Long id) {
        return "usuarios/ver";
    }

    // ========================================
    // MÓDULO: DEPARTAMENTOS
    // ========================================

    /**
     * Listar todos los departamentos
     * Ruta: http://localhost:8080/departamentos
     */
    @GetMapping("/departamentos")
    public String listarDepartamentos() {
        return "departamentos/lista";
    }

    /**
     * Formulario para crear nuevo departamento
     * Ruta: http://localhost:8080/departamentos/nuevo
     */
    @GetMapping("/departamentos/nuevo")
    public String crearDepartamento() {
        return "departamentos/crear";
    }

    /**
     * Formulario para editar departamento
     * Ruta: http://localhost:8080/departamentos/editar/{id}
     */
    @GetMapping("/departamentos/editar/{id}")
    public String editarDepartamento(@PathVariable Long id) {
        return "departamentos/editar";
    }

    /**
     * Ver detalles de un departamento
     * Ruta: http://localhost:8080/departamentos/ver/{id}
     */
    @GetMapping("/departamentos/ver/{id}")
    public String verDepartamento(@PathVariable Long id) {
        return "departamentos/ver";
    }

    // ========================================
    // MÓDULO: MATERIAS
    // ========================================

    @GetMapping("/materias")
    public String listarMaterias() {
        return "materias/lista";
    }

    @GetMapping("/materias/nuevo")
    public String crearMateria() {
        return "materias/crear";
    }

    @GetMapping("/materias/editar/{id}")
    public String editarMateria(@PathVariable Long id) {
        return "materias/editar";
    }

    @GetMapping("/materias/ver/{id}")
    public String verMateria(@PathVariable Long id) {
        return "materias/ver";
    }

    // ========================================
    // MÓDULO: PROFESORES
    // ========================================

    /**
     * Listar todos los profesores
     * Ruta: http://localhost:8080/profesores
     */
    @GetMapping("/profesores")
    public String listarProfesores() {
        return "profesores/lista";
    }

    /**
     * Formulario para crear nuevo profesor
     * Ruta: http://localhost:8080/profesores/nuevo
     */
    @GetMapping("/profesores/nuevo")
    public String crearProfesor() {
        return "profesores/crear";
    }

    /**
     * Formulario para editar profesor
     * Ruta: http://localhost:8080/profesores/editar/{id}
     */
    @GetMapping("/profesores/editar/{id}")
    public String editarProfesor(@PathVariable Long id) {
        return "profesores/editar";
    }

    /**
     * Ver detalles de un profesor
     * Ruta: http://localhost:8080/profesores/ver/{id}
     */
    @GetMapping("/profesores/ver/{id}")
    public String verProfesor(@PathVariable Long id) {
        return "profesores/ver";
    }

    // ========================================
    // MÓDULO: ESTUDIANTES
    // ========================================

    /**
     * Listar todos los estudiantes
     * Ruta: http://localhost:8080/estudiantes
     */
    @GetMapping("/estudiantes")
    public String listarEstudiantes() {
        return "estudiantes/lista";
    }

    /**
     * Formulario para crear nuevo estudiante
     * Ruta: http://localhost:8080/estudiantes/nuevo
     */
    @GetMapping("/estudiantes/nuevo")
    public String crearEstudiante() {
        return "estudiantes/crear";
    }

    /**
     * Formulario para editar estudiante
     * Ruta: http://localhost:8080/estudiantes/editar/{id}
     */
    @GetMapping("/estudiantes/editar/{id}")
    public String editarEstudiante(@PathVariable Long id) {
        return "estudiantes/editar";
    }

    /**
     * Ver detalles de un estudiante
     * Ruta: http://localhost:8080/estudiantes/ver/{id}
     */
    @GetMapping("/estudiantes/ver/{id}")
    public String verEstudiante(@PathVariable Long id) {
        return "estudiantes/ver";
    }

    // ========================================
    // MÓDULO: CURSOS
    // ========================================

    /**
     * Listar todos los cursos
     * Ruta: http://localhost:8080/cursos
     */
    @GetMapping("/cursos")
    public String listarCursos() {
        return "cursos/lista";
    }

    /**
     * Formulario para crear nuevo curso
     * Ruta: http://localhost:8080/cursos/nuevo
     */
    @GetMapping("/cursos/nuevo")
    public String crearCurso() {
        return "cursos/crear";
    }

    /**
     * Formulario para editar curso
     * Ruta: http://localhost:8080/cursos/editar/{id}
     */
    @GetMapping("/cursos/editar/{id}")
    public String editarCurso(@PathVariable Long id) {
        return "cursos/editar";
    }

    /**
     * Ver detalles de un curso
     * Ruta: http://localhost:8080/cursos/ver/{id}
     */
    @GetMapping("/cursos/ver/{id}")
    public String verCurso(@PathVariable Long id) {
        return "cursos/ver";
    }

    // ========================================
    // MÓDULO: CALIFICACIONES
    // ========================================

    /**
     * Listar todas las calificaciones
     * Ruta: http://localhost:8080/calificaciones
     */
    @GetMapping("/calificaciones")
    public String listarCalificaciones() {
        return "calificaciones/lista";
    }

    /**
     * Formulario para registrar calificación
     * Ruta: http://localhost:8080/calificaciones/registrar
     */
    @GetMapping("/calificaciones/nuevo")
    public String calificacionesNuevo() {
        return "calificaciones/crear";
    }

    @GetMapping("/calificaciones/editar/{id}")
    public String calificacionesEditar(@PathVariable Long id, Model model) {
        model.addAttribute("id", id);
        return "calificaciones/editar";
    }

    @GetMapping("/calificaciones/ver/{id}")
    public String calificacionesVer(@PathVariable Long id, Model model) {
        model.addAttribute("id", id);
        return "calificaciones/ver";
    }

    /**
     * Ver calificaciones de un estudiante
     * Ruta: http://localhost:8080/calificaciones/estudiante/{id}

    @GetMapping("/calificaciones/estudiante/{id}")
    public String verCalificacionesEstudiante(@PathVariable Long id) {
        return "calificaciones/ver";
    }*/

    // ========================================
    // MÓDULO: INSCRIPCIONES
    // ========================================

    /**
     * Listar todas las inscripciones
     * Ruta: http://localhost:8080/inscripciones
     */
    @GetMapping("/inscripciones")
    public String listarInscripciones() {
        return "inscripciones/lista";
    }

    /**
     * Formulario para crear inscripción
     * Ruta: http://localhost:8080/inscripciones/nuevo
     */
    @GetMapping("/inscripciones/nuevo")
    public String crearInscripcion() {
        return "inscripciones/crear";
    }

    /**
     * Ver detalles de una inscripción
     * Ruta: http://localhost:8080/inscripciones/ver/{id}
     */
    @GetMapping("/inscripciones/ver/{id}")
    public String verInscripcion(@PathVariable Long id) {
        return "inscripciones/ver";
    }

    @GetMapping("/inscripciones/inscripcion-estudiante")
    public String inscripcionEstudiante() {
        return "inscripciones/inscripcion-estudiante";
    }

    /**
     * Dashboard Ejecutivo
     * Ruta: http://localhost:8080/reportes/dashboard-ejecutivo
     */
    @GetMapping("/reportes/dashboard-ejecutivo")
    public String dashboardEjecutivo() {
        return "reportes/dashboard-ejecutivo";
    }

    // ========================================
    // MÓDULO: ASISTENCIAS
    // ========================================

    /**
     * Vista para tomar asistencia de un curso
     * Ruta: http://localhost:8080/asistencias/tomar/{cursoId}
     */
    @GetMapping("/asistencias/tomar/{cursoId}")
    public String tomarAsistencia(@PathVariable Long cursoId, Model model) {
        model.addAttribute("cursoId", cursoId);
        return "asistencias/tomar";
    }
}