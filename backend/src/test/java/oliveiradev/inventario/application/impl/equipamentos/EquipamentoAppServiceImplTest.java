package oliveiradev.inventario.application.impl.equipamentos;

import oliveiradev.inventario.application.dto.equipamento.EquipamentoAtualizacaoDTO;
import oliveiradev.inventario.application.dto.equipamento.EquipamentoCriacaoDTO;
import oliveiradev.inventario.application.dto.equipamento.EquipamentoRespostaDTO;
import oliveiradev.inventario.application.exception.RecursoNaoEncontradoException;
import oliveiradev.inventario.application.exception.RegraDeNegocioException;
import oliveiradev.inventario.application.impl.EquipamentoAppServiceImpl;
import oliveiradev.inventario.domain.model.equipamentos.Equipamento;
import oliveiradev.inventario.domain.model.equipamentos.LogAlteracao;
import oliveiradev.inventario.domain.repository.EquipamentoRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EquipamentoAppServiceImplTest {
    @Mock
    private EquipamentoRepository equipamentoRepository;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;
    @InjectMocks
    private EquipamentoAppServiceImpl equipamentoAppService;

    private EquipamentoCriacaoDTO equipamentoCriacaoDTO;
    private UserDetails mockUserDetails;
    private final String mockUserEmail = "admin@teste.com";
    private final String mockEquipId = "equipIdSimulado";

    @BeforeEach
    void setUp() {
        equipamentoCriacaoDTO = new EquipamentoCriacaoDTO(
                "Monitor Gamer 27",
                "SNMG27XYZ",
                "Monitor curvo de alta resolução"
        );

        mockUserDetails = new User(mockUserEmail, "password", Collections.emptyList());
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(mockUserDetails);
        when(authentication.isAuthenticated()).thenReturn(true);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        // Limpa o contexto de segurança para não afetar outros testes
        SecurityContextHolder.clearContext();
    }


    // Helper para criar um mock de Equipamento configurado para retornos
    private Equipamento mockEquipamentoConfigurado(String id, EquipamentoCriacaoDTO dto, String usuarioLogado, int numLogsIniciais) {
        Equipamento mockEquip = mock(Equipamento.class);
        when(mockEquip.getId()).thenReturn(id);
        when(mockEquip.getNome()).thenReturn(dto.nome());
        when(mockEquip.getNumeroDeSerie()).thenReturn(dto.numeroDeSerie());
        when(mockEquip.getDescricaoDetalhada()).thenReturn(dto.descricaoDetalhada());
        when(mockEquip.getDataInclusaoNoSistema()).thenReturn(LocalDateTime.now().minusMinutes(5));
        when(mockEquip.getDataUltimaModificacao()).thenReturn(LocalDateTime.now().minusMinutes(1));
        when(mockEquip.getCriadoPorUsuario()).thenReturn(usuarioLogado);
        when(mockEquip.getModificadoPorUsuario()).thenReturn(usuarioLogado);
        List<LogAlteracao> logs = new ArrayList<>();
        if (numLogsIniciais > 0) {
            logs.add(new LogAlteracao(LocalDateTime.now().minusMinutes(5), usuarioLogado, "Equipamento registrado no sistema por " + usuarioLogado + "."));
        }
        when(mockEquip.getLogs()).thenReturn(logs);
        return mockEquip;
    }

    @Nested
    @DisplayName("Testes para criarEquipamento")
    class CriarEquipamentoTests {
        @Test
        @DisplayName("Deve criar um equipamento com sucesso")
        void criarEquipamento_ComDadosValidos_DeveRetornarDTO() {
            when(equipamentoRepository.findByNumeroDeSerie(equipamentoCriacaoDTO.numeroDeSerie())).thenReturn(Optional.empty());

            ArgumentCaptor<Equipamento> captor = ArgumentCaptor.forClass(Equipamento.class);
            // O save deve retornar o equipamento com ID, datas e usuário de auditoria (simulados no mock)
            Equipamento equipamentoRetornadoPeloSave = mockEquipamentoConfigurado(mockEquipId, equipamentoCriacaoDTO, mockUserEmail, 1);
            when(equipamentoRepository.save(captor.capture())).thenReturn(equipamentoRetornadoPeloSave);

            EquipamentoRespostaDTO resposta = equipamentoAppService.criarEquipamento(equipamentoCriacaoDTO);

            assertNotNull(resposta);
            assertEquals(mockEquipId, resposta.id());
            assertEquals(equipamentoCriacaoDTO.nome(), resposta.nome());
            assertEquals(mockUserEmail, resposta.criadoPorUsuario());
            assertEquals(1, resposta.logs().size());
            assertEquals("Equipamento registrado no sistema por " + mockUserEmail + ".", resposta.logs().get(0).descricao());

            Equipamento equipamentoSalvo = captor.getValue();
            assertEquals(equipamentoCriacaoDTO.nome(), equipamentoSalvo.getNome());
            assertEquals(1, equipamentoSalvo.getLogs().size()); // Verifica se o log foi adicionado pela entidade/serviço
            assertEquals("Equipamento registrado no sistema por " + mockUserEmail + ".", equipamentoSalvo.getLogs().get(0).getDescricao());

            verify(equipamentoRepository).save(any(Equipamento.class));
        }

        @Test
        @DisplayName("Deve lançar RegraDeNegocioException ao criar com número de série existente")
        void criarEquipamento_ComNumeroSerieExistente_DeveLancarExcecao() {
            Equipamento existente = new Equipamento("Existente", equipamentoCriacaoDTO.numeroDeSerie(), "desc");
            when(equipamentoRepository.findByNumeroDeSerie(equipamentoCriacaoDTO.numeroDeSerie())).thenReturn(Optional.of(existente));

            assertThrows(RegraDeNegocioException.class, () -> equipamentoAppService.criarEquipamento(equipamentoCriacaoDTO));
            verify(equipamentoRepository, never()).save(any(Equipamento.class));
        }
    }

    @Nested
    @DisplayName("Testes para buscar Equipamento")
    class BuscarEquipamentoTests {
        @Test
        @DisplayName("buscarEquipamentoPorId deve retornar DTO se encontrado")
        void buscarEquipamentoPorId_Encontrado_RetornaDTO() {
            Equipamento mockEquip = mockEquipamentoConfigurado(mockEquipId, equipamentoCriacaoDTO, mockUserEmail, 1);
            when(equipamentoRepository.findById(mockEquipId)).thenReturn(Optional.of(mockEquip));

            Optional<EquipamentoRespostaDTO> resultado = equipamentoAppService.buscarEquipamentoPorId(mockEquipId);

            assertTrue(resultado.isPresent());
            assertEquals(mockEquipId, resultado.get().id());
            verify(equipamentoRepository).findById(mockEquipId);
        }

        @Test
        @DisplayName("buscarEquipamentoPorId deve retornar Optional vazio se não encontrado")
        void buscarEquipamentoPorId_NaoEncontrado_RetornaOptionalVazio() {
            when(equipamentoRepository.findById("idNaoExiste")).thenReturn(Optional.empty());
            Optional<EquipamentoRespostaDTO> resultado = equipamentoAppService.buscarEquipamentoPorId("idNaoExiste");
            assertFalse(resultado.isPresent());
        }
    }


    @Nested
    @DisplayName("Testes para atualizarEquipamento")
    class AtualizarEquipamentoTests {
        @Test
        @DisplayName("Deve atualizar nome e descrição e adicionar logs")
        void atualizarEquipamento_ComNovosDados_DeveAtualizarEAdicionarLogs() {
            EquipamentoAtualizacaoDTO atualizacaoDTO = new EquipamentoAtualizacaoDTO("Nome Atualizado", "Descrição Atualizada");
            // Usar spy para interagir com o objeto real Equipamento e verificar chamadas de métod
            Equipamento equipamentoExistenteSpy = spy(new Equipamento(equipamentoCriacaoDTO.nome(), equipamentoCriacaoDTO.numeroDeSerie(), equipamentoCriacaoDTO.descricaoDetalhada()));
            // Simular que o equipamento já tem um log de criação
            equipamentoExistenteSpy.adicionarLog(mockUserEmail, "Equipamento registrado no sistema por " + mockUserEmail + ".");


            when(equipamentoRepository.findById(mockEquipId)).thenReturn(Optional.of(equipamentoExistenteSpy));
            // O save deve retornar o equipamento atualizado (o spy modificado)
            when(equipamentoRepository.save(any(Equipamento.class))).thenAnswer(invocation -> invocation.getArgument(0));


            EquipamentoRespostaDTO resposta = equipamentoAppService.atualizarEquipamento(mockEquipId, atualizacaoDTO);

            assertNotNull(resposta);
            assertEquals("Nome Atualizado", resposta.nome());
            assertEquals("Descrição Atualizada", resposta.descricaoDetalhada());
            assertEquals(3, resposta.logs().size()); // 1 (criação) + 1 (nome) + 1 (descrição)
            assertTrue(resposta.logs().get(1).descricao().contains("Nome alterado"));
            assertTrue(resposta.logs().get(2).descricao().contains("Descrição detalhada alterada"));

            verify(equipamentoExistenteSpy).alterarNome("Nome Atualizado", mockUserEmail);
            verify(equipamentoExistenteSpy).alterarDescricaoDetalhada("Descrição Atualizada", mockUserEmail);
            verify(equipamentoRepository).save(equipamentoExistenteSpy);
        }

        @Test
        @DisplayName("Não deve atualizar nem adicionar log se DTO não traz alterações")
        void atualizarEquipamento_SemAlteracoesNoDTO_NaoDeveSalvarOuLogar() {
            // DTO com os mesmos dados do equipamento existente (ou campos nulos)
            EquipamentoAtualizacaoDTO dtoSemMudancas = new EquipamentoAtualizacaoDTO(
                    equipamentoCriacaoDTO.nome(), // Mesmo nome
                    equipamentoCriacaoDTO.descricaoDetalhada() // Mesma descrição
            );

            Equipamento equipamentoExistente = new Equipamento(
                    equipamentoCriacaoDTO.nome(),
                    equipamentoCriacaoDTO.numeroDeSerie(),
                    equipamentoCriacaoDTO.descricaoDetalhada()
            );
            equipamentoExistente.adicionarLog(mockUserEmail, "Log inicial");


            when(equipamentoRepository.findById(mockEquipId)).thenReturn(Optional.of(equipamentoExistente));

            EquipamentoRespostaDTO resposta = equipamentoAppService.atualizarEquipamento(mockEquipId, dtoSemMudancas);

            assertNotNull(resposta);
            assertEquals(equipamentoCriacaoDTO.nome(), resposta.nome());
            assertEquals(1, resposta.logs().size());

            verify(equipamentoRepository, never()).save(any(Equipamento.class));
        }

        @Test
        @DisplayName("Deve lançar RecursoNaoEncontradoException ao atualizar equipamento inexistente")
        void atualizarEquipamento_EquipamentoNaoEncontrado_DeveLancarExcecao() {
            EquipamentoAtualizacaoDTO atualizacaoDTO = new EquipamentoAtualizacaoDTO("Nome", "Desc");
            when(equipamentoRepository.findById("idNaoExiste")).thenReturn(Optional.empty());

            assertThrows(RecursoNaoEncontradoException.class, () ->
                    equipamentoAppService.atualizarEquipamento("idNaoExiste", atualizacaoDTO)
            );
            verify(equipamentoRepository, never()).save(any(Equipamento.class));
        }
    }

    @Nested
    @DisplayName("Testes para deletarEquipamento")
    class DeletarEquipamentoTests {
        @Test
        @DisplayName("Deve chamar deleteById quando equipamento existe")
        void deletarEquipamento_QuandoEquipamentoExiste_DeveChamarDeleteById() {
            when(equipamentoRepository.existsById(mockEquipId)).thenReturn(true);
            doNothing().when(equipamentoRepository).deleteById(mockEquipId);

            assertDoesNotThrow(() -> equipamentoAppService.deletarEquipamento(mockEquipId));

            verify(equipamentoRepository).existsById(mockEquipId);
            verify(equipamentoRepository).deleteById(mockEquipId);
        }

        @Test
        @DisplayName("Deve lançar RecursoNaoEncontradoException quando equipamento não existe para deleção")
        void deletarEquipamento_QuandoEquipamentoNaoExiste_DeveLancarExcecao() {
            when(equipamentoRepository.existsById("idNaoExiste")).thenReturn(false);
            assertThrows(RecursoNaoEncontradoException.class, () ->
                    equipamentoAppService.deletarEquipamento("idNaoExiste")
            );
            verify(equipamentoRepository, never()).deleteById(anyString());
        }
    }
}