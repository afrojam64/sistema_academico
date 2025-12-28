package com.sistema.academico.infraestructura.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller para servir las vistas HTML (Thymeleaf).
 *
 * Maneja las rutas de navegación del frontend.
 */
@Controller
public class ViewController {

    /**
     * Página de Login
     * Ruta: http://localhost:8080/
     * Ruta: http://localhost:8080/login
     */
    @GetMapping({"/", "/login"})
    public String login() {
        return "login";
    }

    /**
     * Dashboard Super Administrador
     * Ruta: http://localhost:8080/dashboard/super-admin
     */
    @GetMapping("/dashboard/super-admin")
    public String dashboardSuperAdmin() {
        return "dashboard-super-admin";
    }

    /**
     * Dashboard Administrador
     * Ruta: http://localhost:8080/dashboard/admin
     */
    @GetMapping("/dashboard/admin")
    public String dashboardAdmin() {
        return "dashboard-admin";
    }

    /**
     * Dashboard Profesor
     * Ruta: http://localhost:8080/dashboard/profesor
     */
    @GetMapping("/dashboard/profesor")
    public String dashboardProfesor() {
        return "dashboard-profesor";
    }

    /**
     * Dashboard Estudiante
     * Ruta: http://localhost:8080/dashboard/estudiante
     */
    @GetMapping("/dashboard/estudiante")
    public String dashboardEstudiante() {
        return "dashboard-estudiante";
    }

    /**
     * Dashboard genérico (fallback)
     * Ruta: http://localhost:8080/dashboard
     */
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    /**
     * Página de Error 404
     * Ruta: http://localhost:8080/404
     */
    @GetMapping("/404")
    public String notFound() {
        return "error-404";
    }

    /**
     * Página de Error 403 - Sin permisos
     * Ruta: http://localhost:8080/403
     */
    @GetMapping("/403")
    public String forbidden() {
        return "error-403";
    }

    /**
     * Página de Error 500 - Error del servidor
     * Ruta: http://localhost:8080/500
     */
    @GetMapping("/500")
    public String serverError() {
        return "error-500";
    }
}