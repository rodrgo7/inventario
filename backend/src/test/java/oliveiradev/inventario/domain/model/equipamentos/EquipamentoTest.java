package oliveiradev.inventario.domain.model.equipamentos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EquipamentoTest {
    private Equipamento equipamento;
    private final String usuarioTeste = "teste@usuario.com";

    @BeforeEach
    void setUp() {
        equipamento = new Equipamento("Notebook Dell XPS 15", "SN123XYZ", "Notebook de alta performance");
    }

    @Test
    @DisplayName("Construtor deve criar equipamento com dados válidos")
    void construtor_ComDadosValidos_DeveCriarEquipamento() {
        assertNotNull(equipamento);
        assertEquals("Notebook Dell XPS 15", equipamento.getNome());
        assertEquals("SN123XYZ", equipamento.getNumeroDeSerie());
        assertEquals("Notebook de alta performance", equipamento.getDescricaoDetalhada());
        assertTrue(equipamento.getLogs().isEmpty());
    }

    @Test
    @DisplayName("Construtor deve lançar NullPointerException para nome nulo")
    void construtor_QuandoNomeNulo_DeveLancarNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new Equipamento(null, "SNVALIDO", "Descrição")
        );
    }

    @Test
    @DisplayName("Construtor deve lançar IllegalArgumentException para nome vazio")
    void construtor_QuandoNomeVazio_DeveLancarIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Equipamento("   ", "SNVALIDO", "Descrição")
        );
    }

    @Test
    @DisplayName("Construtor deve lançar NullPointerException para número de série nulo")
    void construtor_QuandoNumeroSerieNulo_DeveLancarNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new Equipamento("Nome Válido", null, "Descrição")
        );
    }

    @Test
    @DisplayName("Construtor deve lançar IllegalArgumentException para número de série vazio")
    void construtor_QuandoNumeroSerieVazio_DeveLancarIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Equipamento("Nome Válido", "   ", "Descrição")
        );
    }

    @Test
    @DisplayName("adicionarLog deve adicionar uma entrada à lista de logs")
    void adicionarLog_ComDadosValidos_DeveAdicionarLog() {
        equipamento.adicionarLog(usuarioTeste, "Primeira alteração de teste.");
        assertEquals(1, equipamento.getLogs().size());
        LogAlteracao primeiroLog = equipamento.getLogs().get(0);
        assertEquals(usuarioTeste, primeiroLog.getUsuarioResponsavel());
        assertEquals("Primeira alteração de teste.", primeiroLog.getDescricao());
        assertNotNull(primeiroLog.getDataHora());
    }

    @Test
    @DisplayName("adicionarLog deve lançar NullPointerException para usuário responsável nulo")
    void adicionarLog_QuandoUsuarioNulo_DeveLancarNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                equipamento.adicionarLog(null, "Log sem usuário.")
        );
    }

    @Test
    @DisplayName("adicionarLog deve lançar NullPointerException para descrição nula")
    void adicionarLog_QuandoDescricaoNula_DeveLancarNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                equipamento.adicionarLog(usuarioTeste, null)
        );
    }

    @Test
    @DisplayName("adicionarLog deve lançar IllegalArgumentException para descrição vazia")
    void adicionarLog_QuandoDescricaoVazia_DeveLancarIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                equipamento.adicionarLog(usuarioTeste, "   ")
        );
    }

    @Test
    @DisplayName("alterarNome deve atualizar o nome e adicionar log")
    void alterarNome_ComNovoNomeValido_DeveAtualizarNomeEAdicionarLog() {
        String nomeAntigo = equipamento.getNome();
        String novoNome = "Notebook Dell XPS 15 (Revisado)";
        equipamento.alterarNome(novoNome, usuarioTeste);

        assertEquals(novoNome, equipamento.getNome());
        assertEquals(1, equipamento.getLogs().size());
        LogAlteracao log = equipamento.getLogs().get(0);
        assertEquals(usuarioTeste, log.getUsuarioResponsavel());
        assertEquals("Nome alterado de '" + nomeAntigo + "' para '" + novoNome + "'.", log.getDescricao());
    }

    @Test
    @DisplayName("alterarNome não deve adicionar log se o nome for o mesmo")
    void alterarNome_ComMesmoNome_NaoDeveAdicionarLog() {
        String nomeAtual = equipamento.getNome();
        equipamento.alterarNome(nomeAtual, usuarioTeste); // Tenta alterar para o mesmo nome

        assertEquals(nomeAtual, equipamento.getNome());
        assertTrue(equipamento.getLogs().isEmpty()); // Nenhum log deve ser adicionado
    }


    @Test
    @DisplayName("alterarDescricaoDetalhada deve atualizar descrição e adicionar log")
    void alterarDescricaoDetalhada_ComNovaDescricao_DeveAtualizarDescricaoEAdicionarLog() {
        String novaDescricao = "Descrição atualizada para teste.";
        equipamento.alterarDescricaoDetalhada(novaDescricao, usuarioTeste);

        assertEquals(novaDescricao, equipamento.getDescricaoDetalhada());
        assertEquals(1, equipamento.getLogs().size());
        LogAlteracao log = equipamento.getLogs().get(0);
        assertEquals(usuarioTeste, log.getUsuarioResponsavel());
        assertTrue(log.getDescricao().contains("Descrição detalhada alterada") || log.getDescricao().contains("definida"));
    }

    @Test
    @DisplayName("alterarDescricaoDetalhada para nulo deve limpar descrição e adicionar log")
    void alterarDescricaoDetalhada_ComDescricaoNula_DeveLimparDescricaoEAdicionarLog() {
        // Garante que há uma descrição inicial para testar a remoção
        equipamento.alterarDescricaoDetalhada("Descrição inicial", "setup_user");
        equipamento.alterarDescricaoDetalhada(null, usuarioTeste); // Tenta limpar a descrição

        assertNull(equipamento.getDescricaoDetalhada());
        // Espera-se 2 logs: um da definição inicial, outro da remoção
        assertEquals(2, equipamento.getLogs().size());
        LogAlteracao logRemocao = equipamento.getLogs().get(1); // Pega o segundo log
        assertEquals(usuarioTeste, logRemocao.getUsuarioResponsavel());
        assertEquals("Descrição detalhada removida.", logRemocao.getDescricao());
    }
}