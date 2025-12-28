package com.sistema.academico.aplicacion.servicio;

import com.sistema.academico.aplicacion.dto.request.LoginRequestDTO;
import com.sistema.academico.aplicacion.dto.response.LoginResponseDTO;

/**
 * Interfaz del servicio de autenticación.
 */
public interface IAuthService {

    /**
     * Autentica un usuario y genera un token JWT
     *
     * @param loginRequest Credenciales del usuario
     * @return Token JWT y datos del usuario
     */
    LoginResponseDTO login(LoginRequestDTO loginRequest);
}