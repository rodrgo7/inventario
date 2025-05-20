package oliveiradev.inventario.interfaces.controller;

import oliveiradev.inventario.application.dto.LoginRequestDTO;
import oliveiradev.inventario.application.dto.JwtAuthenticationResponseDTO;
import oliveiradev.inventario.application.dto.UsuarioCriacaoDTO;
import oliveiradev.inventario.application.dto.UsuarioRespostaDTO;
import oliveiradev.inventario.application.exception.EmailJaCadastradoException;
import oliveiradev.inventario.application.service.UsuarioAppService;
import oliveiradev.inventario.infra.security.jwt.JwtTokenProvider;

import jakarta.validation.Valid; // IMPORTANTE: Importar @Valid
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

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
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequest) { // Adicionado @Valid
        logger.info("Requisição de login recebida para o email: {}", loginRequest.email());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.email().trim().toLowerCase(),
                            loginRequest.senha()
                    )
            );
            logger.info("Usuário autenticado com sucesso: {}", loginRequest.email());

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = tokenProvider.generateToken(userDetails);
            logger.info("Token JWT gerado para o usuário: {}", loginRequest.email());

            return ResponseEntity.ok(new JwtAuthenticationResponseDTO(jwt));

        } catch (BadCredentialsException e) {
            logger.warn("Falha na autenticação para o email {}: Credenciais inválidas", loginRequest.email());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email ou senha inválidos.");
        } catch (Exception e) {
            logger.error("Erro durante a autenticação para o email {}: ", loginRequest.email(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno ao tentar autenticar: " + e.getMessage());
        }
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarUsuario(@Valid @RequestBody UsuarioCriacaoDTO usuarioCriacaoDTO) { // Adicionado @Valid
        logger.info("Requisição de registro recebida para o email: {}", usuarioCriacaoDTO.email());
        try {
            UsuarioRespostaDTO usuarioRegistrado = usuarioAppService.criarUsuario(usuarioCriacaoDTO);
            logger.info("Usuário registrado com sucesso: {}", usuarioRegistrado.email());
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioRegistrado);
        } catch (EmailJaCadastradoException e) {
            logger.warn("Tentativa de registro com email já cadastrado: {}", usuarioCriacaoDTO.email());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IllegalArgumentException e) { // Esta exceção agora pode ser lançada pelas validações do DTO também
            logger.warn("Dados inválidos na tentativa de registro para o email {}: {}",
                    (usuarioCriacaoDTO != null ? usuarioCriacaoDTO.email() : "desconhecido"),
                    e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Erro durante o registro do usuário {}: ",
                    (usuarioCriacaoDTO != null ? usuarioCriacaoDTO.email() : "desconhecido"),
                    e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno ao tentar registrar usuário: " + e.getMessage());
        }
    }
}