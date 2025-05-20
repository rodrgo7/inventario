package oliveiradev.inventario.interfaces.controller;

import oliveiradev.inventario.application.dto.usuario.LoginRequestDTO;
import oliveiradev.inventario.application.dto.usuario.UsuarioCriacaoDTO;
import oliveiradev.inventario.application.dto.usuario.UsuarioRespostaDTO;
import oliveiradev.inventario.application.service.UsuarioAppService;
import oliveiradev.inventario.domain.model.enums.NivelUsuario;
import oliveiradev.inventario.infra.security.jwt.JwtTokenProvider;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;


@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtTokenProvider tokenProvider;

    @MockBean
    private UsuarioAppService usuarioAppService;

    private LoginRequestDTO loginRequestDTO;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        loginRequestDTO = new LoginRequestDTO("teste@email.com", "senha123");
        // Simula um UserDetails que seria retornado após autenticação bem-sucedida
        userDetails = new User(
                "teste@email.com",
                "senhaHashed",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USUARIO_PADRAO"))
        );
    }

    @Test
    @DisplayName("POST /api/auth/login - Deve retornar JWT e status 200 para credenciais válidas")
    void authenticateUser_ComCredenciaisValidas_DeveRetornarTokenEJwt() throws Exception {
        // Simula que o authenticationManager autentica com sucesso
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authenticationToken);

        // Simula que o tokenProvider gera um token
        when(tokenProvider.generateToken(any(UserDetails.class))).thenReturn("jwt.token.simulado");

        ResultActions resultActions = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDTO)));

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is("jwt.token.simulado")))
                .andExpect(jsonPath("$.tipoToken", is("Bearer")));
    }

    @Test
    @DisplayName("POST /api/auth/login - Deve retornar status 401 para credenciais inválidas")
    void authenticateUser_ComCredenciaisInvalidas_DeveRetornarUnauthorized() throws Exception {
        // Simula que o authenticationManager falha ao autenticar
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        ResultActions resultActions = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDTO)));

        resultActions
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Email ou senha inválidos."));
    }

    @Test
    @DisplayName("POST /api/auth/registrar - Deve retornar status 201 e usuário criado para dados válidos")
    void registrarUsuario_ComDadosValidos_DeveRetornarUsuarioCriado() throws Exception {
        UsuarioCriacaoDTO criacaoDTO = new UsuarioCriacaoDTO(
                "Novo Registro", "novoRegistro@email.com", "senhaValida123", Set.of(NivelUsuario.USUARIO_PADRAO));
        UsuarioRespostaDTO respostaDTO = new UsuarioRespostaDTO(
                "idGerado123", "Novo Registro", "novoregistro@email.com", Set.of(NivelUsuario.USUARIO_PADRAO), LocalDateTime.now(), LocalDateTime.now());

        when(usuarioAppService.criarUsuario(any(UsuarioCriacaoDTO.class))).thenReturn(respostaDTO);

        ResultActions resultActions = mockMvc.perform(post("/api/auth/registrar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(criacaoDTO)));

        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is("idGerado123")))
                .andExpect(jsonPath("$.nome", is("Novo Registro")))
                .andExpect(jsonPath("$.email", is("novoregistro@email.com"))); // Ajuste para o email normalizado se for o caso
    }
}