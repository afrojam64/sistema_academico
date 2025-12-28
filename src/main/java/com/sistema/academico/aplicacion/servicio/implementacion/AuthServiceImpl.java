package com.sistema.academico.aplicacion.servicio.implementacion;

import com.sistema.academico.aplicacion.dto.request.LoginRequestDTO;
import com.sistema.academico.aplicacion.dto.response.LoginResponseDTO;
import com.sistema.academico.aplicacion.servicio.IAuthService;
import com.sistema.academico.dominio.entidad.Usuario;
import com.sistema.academico.infraestructura.excepcion.RecursoNoEncontradoException;
import com.sistema.academico.infraestructura.repositorio.UsuarioRepository;
import com.sistema.academico.infraestructura.seguridad.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Implementación del servicio de autenticación.
 *
 * Responsabilidades:
 * 1. Validar credenciales del usuario
 * 2. Generar token JWT
 * 3. Retornar información del usuario autenticado
 */
@Service
public class AuthServiceImpl implements IAuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Autentica un usuario y genera un token JWT.
     *
     * Flujo:
     * 1. Valida las credenciales (nombreUsuario + contraseña)
     * 2. Si son correctas, genera un token JWT
     * 3. Retorna el token y la información del usuario
     *
     * @param loginRequest Credenciales del usuario
     * @return LoginResponseDTO con el token y datos del usuario
     * @throws BadCredentialsException Si las credenciales son incorrectas
     */
    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        try {
            // 1. Autenticar al usuario con Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getNombreUsuario(),
                            loginRequest.getContrasena()
                    )
            );

            // 2. Establecer la autenticación en el contexto de seguridad
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 3. Generar el token JWT
            String token = tokenProvider.generateToken(authentication);

            // 4. Buscar el usuario en la base de datos para obtener sus datos completos
            Usuario usuario = usuarioRepository.findByNombreUsuario(loginRequest.getNombreUsuario())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

            // 5. Construir y retornar la respuesta
            return LoginResponseDTO.builder()
                    .token(token)
                    .type("Bearer")
                    .id(usuario.getId())
                    .nombreUsuario(usuario.getNombreUsuario())
                    .email(usuario.getEmail())
                    .rol(usuario.getRol().getDescripcion())
                    .build();

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Credenciales inválidas");
        }
    }
}