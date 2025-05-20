package oliveiradev.inventario.application.impl.usuarios; // Seu pacote

import oliveiradev.inventario.application.dto.usuario.UsuarioCriacaoDTO;
import oliveiradev.inventario.application.dto.usuario.UsuarioRespostaDTO;
import oliveiradev.inventario.application.exception.EmailJaCadastradoException;
import oliveiradev.inventario.application.impl.UsuarioAppServiceImpl;
import oliveiradev.inventario.domain.model.enums.NivelUsuario;
import oliveiradev.inventario.domain.model.usuarios.Usuario;
import oliveiradev.inventario.domain.repository.UsuarioRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioAppServiceImplTest {
    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioAppServiceImpl usuarioAppService;

    private UsuarioCriacaoDTO usuarioCriacaoDTO;

    @BeforeEach
    void setUp() {
        usuarioCriacaoDTO = new UsuarioCriacaoDTO(
                "Nome Teste",
                "teste@email.com",
                "senha123",
                Set.of(NivelUsuario.USUARIO_PADRAO)
        );
    }

    @Test
    @DisplayName("Deve criar um usuário com sucesso e retornar UsuarioRespostaDTO com ID")
    void criarUsuario_ComDadosValidos_DeveRetornarUsuarioRespostaDTOComId() {
        when(usuarioRepository.findByEmail(usuarioCriacaoDTO.email().toLowerCase())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(usuarioCriacaoDTO.senha())).thenReturn("senhaHashed123");

        // Para capturar o objeto Usuario que é passado para o método save
        ArgumentCaptor<Usuario> usuarioArgumentCaptor = ArgumentCaptor.forClass(Usuario.class);

        // Simular o comportamento do save: ele recebe um Usuario e retorna um Usuario (que teria um ID e datas)
        Usuario usuarioRetornadoPeloSaveMock = mock(Usuario.class);
        when(usuarioRetornadoPeloSaveMock.getId()).thenReturn("idSimulado123");
        when(usuarioRetornadoPeloSaveMock.getNome()).thenReturn(usuarioCriacaoDTO.nome());
        when(usuarioRetornadoPeloSaveMock.getEmail()).thenReturn(usuarioCriacaoDTO.email().toLowerCase());
        when(usuarioRetornadoPeloSaveMock.getNiveis()).thenReturn(Set.of(NivelUsuario.USUARIO_PADRAO)); // Ou os níveis esperados
        when(usuarioRetornadoPeloSaveMock.getDataCriacao()).thenReturn(LocalDateTime.now()); // Simular data
        when(usuarioRetornadoPeloSaveMock.getDataModificacao()).thenReturn(LocalDateTime.now()); // Simular data

        when(usuarioRepository.save(usuarioArgumentCaptor.capture())).thenReturn(usuarioRetornadoPeloSaveMock);

        UsuarioRespostaDTO respostaDTO = usuarioAppService.criarUsuario(usuarioCriacaoDTO);

        assertNotNull(respostaDTO);
        assertEquals("idSimulado123", respostaDTO.id());
        assertEquals(usuarioCriacaoDTO.nome(), respostaDTO.nome());
        assertEquals(usuarioCriacaoDTO.email().toLowerCase(), respostaDTO.email());
        assertTrue(respostaDTO.niveis().contains(NivelUsuario.USUARIO_PADRAO));
        assertNotNull(respostaDTO.dataCriacao());
        assertNotNull(respostaDTO.dataModificacao());

        Usuario usuarioCapturado = usuarioArgumentCaptor.getValue();
        assertEquals(usuarioCriacaoDTO.nome(), usuarioCapturado.getNome());
        assertEquals(usuarioCriacaoDTO.email().toLowerCase(), usuarioCapturado.getEmail());
        assertEquals("senhaHashed123", usuarioCapturado.getSenha()); // Senha hasheada
        assertTrue(usuarioCapturado.getNiveis().contains(NivelUsuario.USUARIO_PADRAO));

        verify(usuarioRepository).findByEmail(usuarioCriacaoDTO.email().toLowerCase());
        verify(passwordEncoder).encode(usuarioCriacaoDTO.senha());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar EmailJaCadastradoException ao tentar criar usuário com email existente")
    void criarUsuario_ComEmailExistente_DeveLancarEmailJaCadastradoException() {
        Usuario usuarioExistente = new Usuario("Existente", usuarioCriacaoDTO.email().toLowerCase(), "senhaExistente");
        when(usuarioRepository.findByEmail(usuarioCriacaoDTO.email().toLowerCase())).thenReturn(Optional.of(usuarioExistente));

        EmailJaCadastradoException exception = assertThrows(EmailJaCadastradoException.class, () -> {
            usuarioAppService.criarUsuario(usuarioCriacaoDTO);
        });

        assertEquals("O email '" + usuarioCriacaoDTO.email().toLowerCase() + "' já está cadastrado no sistema.", exception.getMessage());
        verify(passwordEncoder, never()).encode(anyString());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve atribuir USUARIO_PADRAO se nenhum nível for fornecido na criação")
    void criarUsuario_SemNiveisFornecidos_DeveAtribuirUsuarioPadrao() {
        UsuarioCriacaoDTO dtoSemNiveis = new UsuarioCriacaoDTO("Outro Nome", "outro@email.com", "outrasenha", null); // Níveis nulos

        when(usuarioRepository.findByEmail(dtoSemNiveis.email().toLowerCase())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(dtoSemNiveis.senha())).thenReturn("outrasenhaHashed");

        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        // Para simplificar, o save retorna o próprio objeto capturado (sem ID simulado aqui, já que não é o foco do teste)
        when(usuarioRepository.save(usuarioCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        usuarioAppService.criarUsuario(dtoSemNiveis);

        Usuario usuarioPersistido = usuarioCaptor.getValue();
        assertNotNull(usuarioPersistido.getNiveis());
        assertEquals(1, usuarioPersistido.getNiveis().size());
        assertTrue(usuarioPersistido.getNiveis().contains(NivelUsuario.USUARIO_PADRAO),
                "Usuário deveria ter o nível PADRAO atribuído por padrão.");
        assertEquals("Outro Nome", usuarioPersistido.getNome());
        assertEquals("outro@email.com", usuarioPersistido.getEmail());
        assertEquals("outrasenhaHashed", usuarioPersistido.getSenha());
    }
}