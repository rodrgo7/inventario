package oliveiradev.inventario.application.impl.equipamentos;

import oliveiradev.inventario.domain.model.equipamentos.LogAlteracao;
import oliveiradev.inventario.application.dto.equipamento.EquipamentoAtualizacaoDTO;
import oliveiradev.inventario.application.dto.equipamento.EquipamentoCriacaoDTO;
import oliveiradev.inventario.application.dto.equipamento.EquipamentoRespostaDTO;
import oliveiradev.inventario.application.exception.RecursoNaoEncontradoException;
import oliveiradev.inventario.application.exception.RegraDeNegocioException;
import oliveiradev.inventario.application.impl.EquipamentoAppServiceImpl;
import oliveiradev.inventario.domain.model.equipamentos.Equipamento;
import oliveiradev.inventario.domain.repository.EquipamentoRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User; // User do Spring Security
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private Equipamento equipamentoSalvo;
    private UserDetails mockUserDetails;

    @BeforeEach
    void setUp() {
        equipamentoCriacaoDTO = new EquipamentoCriacaoDTO(
                "Monitor Gamer 27",
                "SNMG27XYZ",
                "Monitor curvo de alta resolução"
        );

        // Para simular o usuário logado
        mockUserDetails = new User("admin@teste.com", "password", Collections.emptyList());
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(mockUserDetails);
        when(authentication.isAuthenticated()).thenReturn(true);
        SecurityContextHolder.setContext(securityContext);

        equipamentoSalvo = new Equipamento(
                equipamentoCriacaoDTO.nome(),
                equipamentoCriacaoDTO.numeroDeSerie(),
                equipamentoCriacaoDTO.descricaoDetalhada()
        );
    }

    private Equipamento mockEquipamentoRetornadoPeloSave(String id, Equipamento dadosBase, String usuarioLogado) {
        Equipamento mockEquip = mock(Equipamento.class);
        when(mockEquip.getId()).thenReturn(id);
        when(mockEquip.getNome()).thenReturn(dadosBase.getNome());
        when(mockEquip.getNumeroDeSerie()).thenReturn(dadosBase.getNumeroDeSerie());
        when(mockEquip.getDescricaoDetalhada()).thenReturn(dadosBase.getDescricaoDetalhada());
        when(mockEquip.getDataInclusaoNoSistema()).thenReturn(LocalDateTime.now().minusHours(1)); // Simula
        when(mockEquip.getDataUltimaModificacao()).thenReturn(LocalDateTime.now()); // Simula
        when(mockEquip.getCriadoPorUsuario()).thenReturn(usuarioLogado); // Simula
        when(mockEquip.getModificadoPorUsuario()).thenReturn(usuarioLogado); // Simula
        // Simula a lista de logs; o primeiro log seria o de criação.
        List<LogAlteracao> logsSimulados = new ArrayList<>();
        logsSimulados.add(new LogAlteracao(LocalDateTime.now().minusHours(1), usuarioLogado, "Equipamento registrado no sistema por " + usuarioLogado + "."));
        when(mockEquip.getLogs()).thenReturn(logsSimulados);
        return mockEquip;
    }


    @Test
    @DisplayName("Deve criar um equipamento com sucesso")
    void criarEquipamento_ComDadosValidos_DeveRetornarEquipamentoRespostaDTO() {
        when(equipamentoRepository.findByNumeroDeSerie(equipamentoCriacaoDTO.numeroDeSerie())).thenReturn(Optional.empty());

        ArgumentCaptor<Equipamento> equipamentoCaptor = ArgumentCaptor.forClass(Equipamento.class);
        Equipamento equipamentoMockRetorno = mockEquipamentoRetornadoPeloSave("idEquipSimulado", equipamentoSalvo, "admin@teste.com");
        when(equipamentoRepository.save(equipamentoCaptor.capture())).thenReturn(equipamentoMockRetorno);

        EquipamentoRespostaDTO respostaDTO = equipamentoAppService.criarEquipamento(equipamentoCriacaoDTO);

        assertNotNull(respostaDTO);
        assertEquals("idEquipSimulado", respostaDTO.id());
        assertEquals(equipamentoCriacaoDTO.nome(), respostaDTO.nome());
        assertEquals(equipamentoCriacaoDTO.numeroDeSerie(), respostaDTO.numeroDeSerie());
        assertEquals("admin@teste.com", respostaDTO.criadoPorUsuario());
        assertFalse(respostaDTO.logs().isEmpty());
        assertEquals("Equipamento registrado no sistema por admin@teste.com.", respostaDTO.logs().get(0).descricao());


        Equipamento equipamentoPersistido = equipamentoCaptor.getValue();
        assertEquals(equipamentoCriacaoDTO.nome(), equipamentoPersistido.getNome());
        // Verifica se o log de criação foi adicionado pela lógica do serviço
        assertFalse(equipamentoPersistido.getLogs().isEmpty());
        assertEquals("Equipamento registrado no sistema por admin@teste.com.", equipamentoPersistido.getLogs().get(0).getDescricao());


        verify(equipamentoRepository).findByNumeroDeSerie(equipamentoCriacaoDTO.numeroDeSerie());
        verify(equipamentoRepository).save(any(Equipamento.class));
    }

    @Test
    @DisplayName("Deve lançar RegraDeNegocioException ao criar equipamento com número de série existente")
    void criarEquipamento_ComNumeroSerieExistente_DeveLancarRegraDeNegocioException() {
        when(equipamentoRepository.findByNumeroDeSerie(equipamentoCriacaoDTO.numeroDeSerie())).thenReturn(Optional.of(equipamentoSalvo));

        RegraDeNegocioException exception = assertThrows(RegraDeNegocioException.class, () -> {
            equipamentoAppService.criarEquipamento(equipamentoCriacaoDTO);
        });
        assertEquals("Número de série '" + equipamentoCriacaoDTO.numeroDeSerie() + "' já cadastrado.", exception.getMessage());
        verify(equipamentoRepository, never()).save(any(Equipamento.class));
    }


    @Test
    @DisplayName("atualizarEquipamento deve atualizar nome e adicionar log")
    void atualizarEquipamento_ComNovoNome_DeveAtualizarNomeEAdicionarLog() {
        String idExistente = "equip123";
        Equipamento equipamentoExistente = new Equipamento("Nome Antigo", "SN001", "Desc Antiga");
        Equipamento mockEquipamentoDoBanco = spy(equipamentoExistente); // spy para poder chamar métodos reais e verificar interações

        when(equipamentoRepository.findById(idExistente)).thenReturn(Optional.of(mockEquipamentoDoBanco));
        when(equipamentoRepository.save(any(Equipamento.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Retorna o mesmo objeto

        EquipamentoAtualizacaoDTO atualizacaoDTO = new EquipamentoAtualizacaoDTO("Nome Novo", "Desc Nova");

        EquipamentoRespostaDTO respostaDTO = equipamentoAppService.atualizarEquipamento(idExistente, atualizacaoDTO);

        assertNotNull(respostaDTO);
        assertEquals("Nome Novo", respostaDTO.nome());
        assertEquals("Desc Nova", respostaDTO.descricaoDetalhada());
        // Verificar se os logs foram adicionados
        ArgumentCaptor<Equipamento> captor = ArgumentCaptor.forClass(Equipamento.class);
        verify(equipamentoRepository).save(captor.capture());

        Equipamento equipamentoSalvoNoTeste = captor.getValue();
        assertEquals(2, equipamentoSalvoNoTeste.getLogs().size()); // Log de alteração de nome E descrição
        assertTrue(equipamentoSalvoNoTeste.getLogs().get(0).getDescricao().contains("Nome alterado de 'Nome Antigo' para 'Nome Novo'."));
        assertTrue(equipamentoSalvoNoTeste.getLogs().get(1).getDescricao().contains("Descrição detalhada alterada."));
        assertEquals("admin@teste.com", equipamentoSalvoNoTeste.getModificadoPorUsuario()); // Verificado via mock de SecurityContext
    }

    @Test
    @DisplayName("deletarEquipamento deve chamar deleteById quando equipamento existe")
    void deletarEquipamento_QuandoEquipamentoExiste_DeveChamarDeleteById() {
        String idExistente = "equip123";
        when(equipamentoRepository.existsById(idExistente)).thenReturn(true);
        doNothing().when(equipamentoRepository).deleteById(idExistente); // Mock para não fazer nada no delete

        assertDoesNotThrow(() -> equipamentoAppService.deletarEquipamento(idExistente));

        verify(equipamentoRepository).existsById(idExistente);
        verify(equipamentoRepository).deleteById(idExistente);
    }

    @Test
    @DisplayName("deletarEquipamento deve lançar RecursoNaoEncontradoException quando equipamento não existe")
    void deletarEquipamento_QuandoEquipamentoNaoExiste_DeveLancarExcecao() {
        String idNaoExistente = "naoexiste";
        when(equipamentoRepository.existsById(idNaoExistente)).thenReturn(false);

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            equipamentoAppService.deletarEquipamento(idNaoExistente);
        });
        verify(equipamentoRepository, never()).deleteById(idNaoExistente);
    }
}