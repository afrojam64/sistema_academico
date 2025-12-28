package com.sistema.academico.infraestructura.controlador;

import com.sistema.academico.aplicacion.dto.request.LoginRequestDTO;
import com.sistema.academico.aplicacion.dto.response.LoginResponseDTO;
import com.sistema.academico.aplicacion.servicio.IAuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para operaciones de autenticación.
 *
 * Endpoints:
 * - POST /auth/login - Iniciar sesión
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private IAuthService authService;

    /**
     * Endpoint para iniciar sesión.
     *
     * @param loginRequest Credenciales del usuario
     * @return Token JWT y datos del usuario
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        LoginResponseDTO response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }
}