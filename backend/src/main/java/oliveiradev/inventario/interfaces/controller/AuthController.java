package oliveiradev.inventario.interfaces.controller;

import oliveiradev.inventario.application.dto.usuario.LoginRequestDTO;
import oliveiradev.inventario.application.dto.usuario.JwtAuthenticationResponseDTO;
import oliveiradev.inventario.application.dto.usuario.UsuarioCriacaoDTO;
import oliveiradev.inventario.application.dto.usuario.UsuarioRespostaDTO;
import oliveiradev.inventario.application.service.UsuarioAppService;
import oliveiradev.inventario.infra.security.jwt.JwtTokenProvider;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
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
    public ResponseEntity<JwtAuthenticationResponseDTO> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequest) {
        logger.info("Requisição de login recebida para o email: {}", loginRequest.email());

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
    }

    @PostMapping("/registrar")
    public ResponseEntity<UsuarioRespostaDTO> registrarUsuario(@Valid @RequestBody UsuarioCriacaoDTO usuarioCriacaoDTO) {
        logger.info("Requisição de registro recebida para o email: {}", usuarioCriacaoDTO.email());
        UsuarioRespostaDTO usuarioRegistrado = usuarioAppService.criarUsuario(usuarioCriacaoDTO);
        logger.info("Usuário registrado com sucesso: {}", usuarioRegistrado.email());
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioRegistrado);
    }
}