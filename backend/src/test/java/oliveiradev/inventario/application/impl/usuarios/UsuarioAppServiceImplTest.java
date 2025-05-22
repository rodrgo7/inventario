package oliveiradev.inventario.application.impl.usuarios;

import oliveiradev.inventario.application.dto.usuario.UsuarioCriacaoDTO;
import oliveiradev.inventario.application.dto.usuario.UsuarioRespostaDTO;
import oliveiradev.inventario.application.exception.EmailJaCadastradoException;
import oliveiradev.inventario.application.exception.RecursoNaoEncontradoException;
import oliveiradev.inventario.application.impl.UsuarioAppServiceImpl;
import oliveiradev.inventario.domain.model.enums.NivelUsuario;
import oliveiradev.inventario.domain.model.usuarios.Usuario;
import oliveiradev.inventario.domain.repository.UsuarioRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
    private Usuario usuarioMock;
    private final String mockUserId = "mockUserId123";
    private final String mockUserEmail = "teste@email.com";

    @BeforeEach
    void setUp() {
        usuarioCriacaoDTO = new UsuarioCriacaoDTO(
                "Nome Teste",
                mockUserEmail,
                "senha123",
                Set.of(NivelUsuario.USUARIO_PADRAO)
        );

        usuarioMock = mock(Usuario.class);
        when(usuarioMock.getId()).thenReturn(mockUserId);
        when(usuarioMock.getNome()).thenReturn("Nome Mock");
        when(usuarioMock.getEmail()).thenReturn(mockUserEmail.toLowerCase());
        when(usuarioMock.getSenha()).thenReturn("senhaHashed123"); // Senha já hasheada no mock
        when(usuarioMock.getNiveis()).thenReturn(Set.of(NivelUsuario.USUARIO_PADRAO));
        when(usuarioMock.getDataCriacao()).thenReturn(LocalDateTime.now().minusDays(1));
        when(usuarioMock.getDataModificacao()).thenReturn(LocalDateTime.now());
    }

    // Helper para criar mocks de Usuario para listas
    private Usuario mockUsuario(String id, String nome, String email, NivelUsuario nivel) {
        Usuario uMock = mock(Usuario.class);
        when(uMock.getId()).thenReturn(id);
        when(uMock.getNome()).thenReturn(nome);
        when(uMock.getEmail()).thenReturn(email);
        when(uMock.getNiveis()).thenReturn(Set.of(nivel));
        when(uMock.getDataCriacao()).thenReturn(LocalDateTime.now());
        when(uMock.getDataModificacao()).thenReturn(LocalDateTime.now());
        return uMock;
    }

    @Nested
    @DisplayName("Testes para criarUsuario")
    class CriarUsuarioTests {
        @Test
        @DisplayName("Deve criar um usuário com sucesso e retornar UsuarioRespostaDTO com ID")
        void criarUsuario_ComDadosValidos_DeveRetornarUsuarioRespostaDTOComId() {
            when(usuarioRepository.findByEmail(usuarioCriacaoDTO.email().toLowerCase())).thenReturn(Optional.empty());
            when(passwordEncoder.encode(usuarioCriacaoDTO.senha())).thenReturn("senhaHashed123");

            ArgumentCaptor<Usuario> usuarioArgumentCaptor = ArgumentCaptor.forClass(Usuario.class);
            when(usuarioRepository.save(usuarioArgumentCaptor.capture())).thenReturn(usuarioMock);

            UsuarioRespostaDTO respostaDTO = usuarioAppService.criarUsuario(usuarioCriacaoDTO);

            assertNotNull(respostaDTO);
            assertEquals(mockUserId, respostaDTO.id());
            assertEquals(usuarioCriacaoDTO.nome(), respostaDTO.nome());
            assertEquals(usuarioCriacaoDTO.email().toLowerCase(), respostaDTO.email());
            assertTrue(respostaDTO.niveis().contains(NivelUsuario.USUARIO_PADRAO));
            assertNotNull(respostaDTO.dataCriacao());
            assertNotNull(respostaDTO.dataModificacao());

            Usuario usuarioCapturado = usuarioArgumentCaptor.getValue();
            assertEquals(usuarioCriacaoDTO.nome(), usuarioCapturado.getNome());
            assertEquals(usuarioCriacaoDTO.email().toLowerCase(), usuarioCapturado.getEmail());
            assertEquals("senhaHashed123", usuarioCapturado.getSenha());
            assertTrue(usuarioCapturado.getNiveis().contains(NivelUsuario.USUARIO_PADRAO));

            verify(usuarioRepository).findByEmail(usuarioCriacaoDTO.email().toLowerCase());
            verify(passwordEncoder).encode(usuarioCriacaoDTO.senha());
            verify(usuarioRepository).save(any(Usuario.class));
        }

        @Test
        @DisplayName("Deve lançar EmailJaCadastradoException ao tentar criar usuário com email existente")
        void criarUsuario_ComEmailExistente_DeveLancarEmailJaCadastradoException() {
            when(usuarioRepository.findByEmail(usuarioCriacaoDTO.email().toLowerCase())).thenReturn(Optional.of(usuarioMock));

            EmailJaCadastradoException exception = assertThrows(EmailJaCadastradoException.class, () -> {
                usuarioAppService.criarUsuario(usuarioCriacaoDTO);
            });

            assertEquals("O email '" + usuarioCriacaoDTO.email().toLowerCase() + "' já está cadastrado no sistema.", exception.getMessage());
            verify(passwordEncoder, never()).encode(anyString());
            verify(usuarioRepository, never()).save(any(Usuario.class));
        }

        @Test
        @DisplayName("Deve atribuir USUARIO_PADRAO se nenhum nível for fornecido na criação (DTO com níveis nulos)")
        void criarUsuario_ComNiveisNulosNoDTO_DeveAtribuirUsuarioPadrao() {
            UsuarioCriacaoDTO dtoSemNiveis = new UsuarioCriacaoDTO("Outro Nome", "outro@email.com", "outrasenha", null);
            when(usuarioRepository.findByEmail(dtoSemNiveis.email().toLowerCase())).thenReturn(Optional.empty());
            when(passwordEncoder.encode(dtoSemNiveis.senha())).thenReturn("outrasenhaHashed");

            ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
            Usuario usuarioRetornadoMock = mockUsuario("idOutro", dtoSemNiveis.nome(), dtoSemNiveis.email(), NivelUsuario.USUARIO_PADRAO);
            when(usuarioRepository.save(usuarioCaptor.capture())).thenReturn(usuarioRetornadoMock);

            UsuarioRespostaDTO respostaDTO = usuarioAppService.criarUsuario(dtoSemNiveis);

            Usuario usuarioPersistido = usuarioCaptor.getValue();
            assertNotNull(usuarioPersistido.getNiveis());
            assertEquals(1, usuarioPersistido.getNiveis().size());
            assertTrue(usuarioPersistido.getNiveis().contains(NivelUsuario.USUARIO_PADRAO));

            assertNotNull(respostaDTO);
            assertTrue(respostaDTO.niveis().contains(NivelUsuario.USUARIO_PADRAO));
        }

        @Test
        @DisplayName("Deve atribuir USUARIO_PADRAO se conjunto de níveis for vazio na criação")
        void criarUsuario_ComNiveisVaziosNoDTO_DeveAtribuirUsuarioPadrao() {
            UsuarioCriacaoDTO dtoNiveisVazios = new UsuarioCriacaoDTO("Nome Vazio", "vazio@email.com", "senhaVazia", Collections.emptySet());
            when(usuarioRepository.findByEmail(dtoNiveisVazios.email().toLowerCase())).thenReturn(Optional.empty());
            when(passwordEncoder.encode(dtoNiveisVazios.senha())).thenReturn("senhaVaziaHashed");
            ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
            when(usuarioRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0)); // Retorna o que foi passado

            usuarioAppService.criarUsuario(dtoNiveisVazios);
            assertTrue(captor.getValue().getNiveis().contains(NivelUsuario.USUARIO_PADRAO));
            assertEquals(1, captor.getValue().getNiveis().size());
        }
    }

    @Nested
    @DisplayName("Testes para buscarUsuarioPorId")
    class BuscarUsuarioPorIdTests {
        @Test
        @DisplayName("Deve retornar UsuarioRespostaDTO quando ID existe")
        void buscarUsuarioPorId_QuandoIdExiste_DeveRetornarUsuarioRespostaDTO() {
            when(usuarioRepository.findById(mockUserId)).thenReturn(Optional.of(usuarioMock));
            Optional<UsuarioRespostaDTO> resultado = usuarioAppService.buscarUsuarioPorId(mockUserId);
            assertTrue(resultado.isPresent());
            assertEquals(mockUserId, resultado.get().id());
            assertEquals(usuarioMock.getNome(), resultado.get().nome());
            verify(usuarioRepository).findById(mockUserId);
        }

        @Test
        @DisplayName("Deve retornar Optional vazio quando ID não existe")
        void buscarUsuarioPorId_QuandoIdNaoExiste_DeveRetornarOptionalVazio() {
            when(usuarioRepository.findById("idInexistente")).thenReturn(Optional.empty());
            Optional<UsuarioRespostaDTO> resultado = usuarioAppService.buscarUsuarioPorId("idInexistente");
            assertFalse(resultado.isPresent());
            verify(usuarioRepository).findById("idInexistente");
        }
    }

    @Nested
    @DisplayName("Testes para buscarUsuarioPorEmail")
    class BuscarUsuarioPorEmailTests {
        @Test
        @DisplayName("Deve retornar UsuarioRespostaDTO quando email existe")
        void buscarUsuarioPorEmail_QuandoEmailExiste_DeveRetornarUsuarioRespostaDTO() {
            when(usuarioRepository.findByEmail(mockUserEmail.toLowerCase())).thenReturn(Optional.of(usuarioMock));
            Optional<UsuarioRespostaDTO> resultado = usuarioAppService.buscarUsuarioPorEmail(mockUserEmail);
            assertTrue(resultado.isPresent());
            assertEquals(mockUserEmail.toLowerCase(), resultado.get().email());
            verify(usuarioRepository).findByEmail(mockUserEmail.toLowerCase());
        }

        @Test
        @DisplayName("Deve retornar Optional vazio quando email não existe")
        void buscarUsuarioPorEmail_QuandoEmailNaoExiste_DeveRetornarOptionalVazio() {
            when(usuarioRepository.findByEmail("naoexiste@email.com")).thenReturn(Optional.empty());
            Optional<UsuarioRespostaDTO> resultado = usuarioAppService.buscarUsuarioPorEmail("naoexiste@email.com");
            assertFalse(resultado.isPresent());
            verify(usuarioRepository).findByEmail("naoexiste@email.com");
        }
    }

    @Nested
    @DisplayName("Testes para listarTodosUsuarios")
    class ListarTodosUsuariosTests {
        @Test
        @DisplayName("Deve retornar lista de UsuarioRespostaDTO")
        void listarTodosUsuarios_QuandoExistemUsuarios_DeveRetornarListaDeDTOs() {
            Usuario u1 = mockUsuario("id1", "User 1", "u1@e.com", NivelUsuario.USUARIO_PADRAO);
            Usuario u2 = mockUsuario("id2", "User 2", "u2@e.com", NivelUsuario.USUARIO_ADMIN);
            when(usuarioRepository.findAll()).thenReturn(Arrays.asList(u1, u2));

            List<UsuarioRespostaDTO> resultado = usuarioAppService.listarTodosUsuarios();

            assertEquals(2, resultado.size());
            assertEquals("id1", resultado.get(0).id());
            assertEquals("id2", resultado.get(1).id());
            verify(usuarioRepository).findAll();
        }

        @Test
        @DisplayName("Deve retornar lista vazia se não há usuários")
        void listarTodosUsuarios_QuandoNaoExistemUsuarios_DeveRetornarListaVazia() {
            when(usuarioRepository.findAll()).thenReturn(Collections.emptyList());
            List<UsuarioRespostaDTO> resultado = usuarioAppService.listarTodosUsuarios();
            assertTrue(resultado.isEmpty());
            verify(usuarioRepository).findAll();
        }
    }

    @Nested
    @DisplayName("Testes para atualizarNiveisUsuario")
    class AtualizarNiveisUsuarioTests {
        @Test
        @DisplayName("Deve atualizar níveis com sucesso")
        void atualizarNiveisUsuario_ComDadosValidos_DeveRetornarUsuarioAtualizado() {
            Set<NivelUsuario> novosNiveis = Set.of(NivelUsuario.USUARIO_ADMIN);
            Usuario usuarioParaAtualizarSpy = spy(new Usuario("Nome Original", mockUserEmail, "senhaOriginal"));

            Usuario usuarioDoBancoMock = mock(Usuario.class);
            when(usuarioDoBancoMock.getId()).thenReturn(mockUserId);
            // Configure outros getters se o converterParaRespostaDTO precisar deles
            when(usuarioDoBancoMock.getNome()).thenReturn("Nome Original");
            when(usuarioDoBancoMock.getEmail()).thenReturn(mockUserEmail);
            when(usuarioDoBancoMock.getDataCriacao()).thenReturn(LocalDateTime.now().minusDays(1));
            when(usuarioDoBancoMock.getDataModificacao()).thenReturn(LocalDateTime.now());


            when(usuarioRepository.findById(mockUserId)).thenReturn(Optional.of(usuarioDoBancoMock));
            when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
                Usuario u = invocation.getArgument(0);
                when(u.getNiveis()).thenReturn(novosNiveis); // Faz o mock retornar os novos níveis
                return u;
            });

            UsuarioRespostaDTO resposta = usuarioAppService.atualizarNiveisUsuario(mockUserId, novosNiveis);

            assertNotNull(resposta);
            assertEquals(mockUserId, resposta.id());
            assertEquals(novosNiveis, resposta.niveis());

            verify(usuarioRepository).findById(mockUserId);
            verify(usuarioDoBancoMock).definirNiveis(novosNiveis);
            verify(usuarioRepository).save(usuarioDoBancoMock);
        }

        @Test
        @DisplayName("Deve lançar RecursoNaoEncontradoException se usuário não existe ao atualizar níveis")
        void atualizarNiveisUsuario_UsuarioNaoEncontrado_DeveLancarExcecao() {
            when(usuarioRepository.findById("idInexistente")).thenReturn(Optional.empty());
            assertThrows(RecursoNaoEncontradoException.class, () ->
                    usuarioAppService.atualizarNiveisUsuario("idInexistente", Set.of(NivelUsuario.USUARIO_ADMIN))
            );
            verify(usuarioRepository, never()).save(any(Usuario.class));
        }

        @Test
        @DisplayName("Deve propagar IllegalStateException da entidade ao atualizar níveis com regra violada")
        void atualizarNiveisUsuario_RegraEntidadeViolada_DevePropagarExcecao() {
            Usuario usuarioMasterMock = mock(Usuario.class);
            when(usuarioMasterMock.getId()).thenReturn(mockUserId);
            Set<NivelUsuario> niveisInvalidosParaMaster = Set.of(NivelUsuario.USUARIO_PADRAO);

            when(usuarioRepository.findById(mockUserId)).thenReturn(Optional.of(usuarioMasterMock));
            // Simula que a entidade lança exceção ao tentar definir níveis inválidos
            doThrow(new IllegalStateException("Usuário MASTER não pode ter outros níveis adicionados."))
                    .when(usuarioMasterMock).definirNiveis(niveisInvalidosParaMaster);

            IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                    usuarioAppService.atualizarNiveisUsuario(mockUserId, niveisInvalidosParaMaster)
            );
            assertEquals("Usuário MASTER não pode ter outros níveis adicionados.", ex.getMessage());
            verify(usuarioRepository, never()).save(any(Usuario.class));
        }
    }
}