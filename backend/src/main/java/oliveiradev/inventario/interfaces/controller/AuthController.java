package oliveiradev.inventario.interfaces.controller;

import oliveiradev.inventario.application.dto.LoginRequestDTO;
import oliveiradev.inventario.application.dto.JwtAuthenticationResponseDTO;
import oliveiradev.inventario.application.dto.UsuarioCriacaoDTO;
import oliveiradev.inventario.application.dto.UsuarioRespostaDTO;
import oliveiradev.inventario.application.exception.EmailJaCadastradoException;
import oliveiradev.inventario.application.service.UsuarioAppService;
import oliveiradev.inventario.infra.security.jwt.JwtTokenProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UsuarioAppService usuarioAppService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenProvider tokenProvider,
                          UsuarioAppService usuarioAppService) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.usuarioAppService = usuarioAppService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequestDTO loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.email().trim().toLowerCase(),
                            loginRequest.senha()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = tokenProvider.generateToken(userDetails);

            return ResponseEntity.ok(new JwtAuthenticationResponseDTO(jwt));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email ou senha inválidos.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno ao tentar autenticar.");
        }
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarUsuario(@RequestBody UsuarioCriacaoDTO usuarioCriacaoDTO) {
        try {
            UsuarioRespostaDTO usuarioRegistrado = usuarioAppService.criarUsuario(usuarioCriacaoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioRegistrado);
        } catch (EmailJaCadastradoException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno ao tentar registrar usuário.");
        }
    }
}