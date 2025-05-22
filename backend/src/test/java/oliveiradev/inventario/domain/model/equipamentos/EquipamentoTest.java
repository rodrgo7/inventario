package oliveiradev.inventario.domain.model.equipamentos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EquipamentoTest {
    private Equipamento equipamento;
    private final String usuarioTeste = "teste@usuario.com";
    private final String nomeEquipPadrao = "Notebook Dell XPS 15";
    private final String serialPadrao = "SN123XYZ";
    private final String descPadrao = "Notebook de alta performance";

    @BeforeEach
    void setUp() {
        equipamento = new Equipamento(nomeEquipPadrao, serialPadrao, descPadrao);
    }

    @Nested
    @DisplayName("Testes de Construtor")
    class ConstrutorTests {
        @Test
        @DisplayName("Deve criar equipamento com dados válidos")
        void construtor_ComDadosValidos_DeveCriarEquipamento() {
            assertNotNull(equipamento);
            assertEquals(nomeEquipPadrao, equipamento.getNome());
            assertEquals(serialPadrao, equipamento.getNumeroDeSerie());
            assertEquals(descPadrao, equipamento.getDescricaoDetalhada());
            assertTrue(equipamento.getLogs().isEmpty());
        }

        @Test
        @DisplayName("Deve permitir descrição detalhada nula no construtor")
        void construtor_ComDescricaoNula_DeveCriarEquipamento() {
            Equipamento eqSemDesc = new Equipamento(nomeEquipPadrao, serialPadrao, null);
            assertNotNull(eqSemDesc);
            assertNull(eqSemDesc.getDescricaoDetalhada());
        }

        @Test
        @DisplayName("Deve lançar NullPointerException para nome nulo")
        void construtor_QuandoNomeNulo_DeveLancarNullPointerException() {
            assertThrows(NullPointerException.class, () -> new Equipamento(null, serialPadrao, descPadrao));
        }

        @Test
        @DisplayName("Deve lançar IllegalArgumentException para nome vazio")
        void construtor_QuandoNomeVazio_DeveLancarIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () -> new Equipamento("   ", serialPadrao, descPadrao));
        }

        @Test
        @DisplayName("Deve lançar NullPointerException para número de série nulo")
        void construtor_QuandoNumeroSerieNulo_DeveLancarNullPointerException() {
            assertThrows(NullPointerException.class, () -> new Equipamento(nomeEquipPadrao, null, descPadrao));
        }

        @Test
        @DisplayName("Deve lançar IllegalArgumentException para número de série vazio")
        void construtor_QuandoNumeroSerieVazio_DeveLancarIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () -> new Equipamento(nomeEquipPadrao, "   ", descPadrao));
        }
    }

    @Nested
    @DisplayName("Testes para adicionarLog")
    class AdicionarLogTests {
        @Test
        @DisplayName("Deve adicionar uma entrada à lista de logs")
        void adicionarLog_ComDadosValidos_DeveAdicionarLog() {
            equipamento.adicionarLog(usuarioTeste, "Primeira alteração.");
            assertEquals(1, equipamento.getLogs().size());
            LogAlteracao log = equipamento.getLogs().get(0);
            assertEquals(usuarioTeste, log.getUsuarioResponsavel());
            assertEquals("Primeira alteração.", log.getDescricao());
            assertNotNull(log.getDataHora());
        }

        @Test
        @DisplayName("Deve lançar NullPointerException para usuário responsável nulo")
        void adicionarLog_QuandoUsuarioNulo_DeveLancarNullPointerException() {
            assertThrows(NullPointerException.class, () -> equipamento.adicionarLog(null, "Log."));
        }

        @Test
        @DisplayName("Deve lançar NullPointerException para descrição nula")
        void adicionarLog_QuandoDescricaoNula_DeveLancarNullPointerException() {
            assertThrows(NullPointerException.class, () -> equipamento.adicionarLog(usuarioTeste, null));
        }

        @Test
        @DisplayName("Deve lançar IllegalArgumentException para descrição vazia")
        void adicionarLog_QuandoDescricaoVazia_DeveLancarIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () -> equipamento.adicionarLog(usuarioTeste, "   "));
        }
    }

    @Nested
    @DisplayName("Testes para Métodos de Alteração")
    class AlteracaoTests {
        @Test
        @DisplayName("alterarNome deve atualizar o nome e adicionar log")
        void alterarNome_ComNovoNomeValido_DeveAtualizarNomeEAdicionarLog() {
            String novoNome = "Notebook Dell XPS 15 (Rev2)";
            equipamento.alterarNome(novoNome, usuarioTeste);
            assertEquals(novoNome, equipamento.getNome());
            assertEquals(1, equipamento.getLogs().size());
            assertTrue(equipamento.getLogs().get(0).getDescricao().contains("Nome alterado de '" + nomeEquipPadrao + "' para '" + novoNome + "'."));
        }

        @Test
        @DisplayName("alterarNome não deve adicionar log se o nome for o mesmo")
        void alterarNome_ComMesmoNome_NaoDeveAdicionarLog() {
            equipamento.alterarNome(nomeEquipPadrao, usuarioTeste);
            assertTrue(equipamento.getLogs().isEmpty());
        }

        @Test
        @DisplayName("alterarNumeroDeSerie deve atualizar e adicionar log")
        void alterarNumeroDeSerie_ComNovoSerial_DeveAtualizarEAdicionarLog() {
            String novoSerial = "SN-XYZ-NEW";
            equipamento.alterarNumeroDeSerie(novoSerial, usuarioTeste);
            assertEquals(novoSerial, equipamento.getNumeroDeSerie());
            assertEquals(1, equipamento.getLogs().size());
            assertTrue(equipamento.getLogs().get(0).getDescricao().contains("Número de série alterado de '" + serialPadrao + "' para '" + novoSerial + "'."));
        }

        @Test
        @DisplayName("alterarNumeroDeSerie não deve adicionar log se o serial for o mesmo")
        void alterarNumeroDeSerie_ComMesmoSerial_NaoDeveAdicionarLog() {
            equipamento.alterarNumeroDeSerie(serialPadrao, usuarioTeste);
            assertTrue(equipamento.getLogs().isEmpty());
        }

        @Test
        @DisplayName("alterarDescricaoDetalhada de nulo para valor deve adicionar log 'definida'")
        void alterarDescricaoDetalhada_DeNuloParaValor_DeveAdicionarLogDefinida() {
            Equipamento eqSemDesc = new Equipamento("Nome", "Serial", null);
            eqSemDesc.alterarDescricaoDetalhada("Nova Descrição", usuarioTeste);
            assertEquals("Nova Descrição", eqSemDesc.getDescricaoDetalhada());
            assertEquals(1, eqSemDesc.getLogs().size());
            assertEquals("Descrição detalhada definida.", eqSemDesc.getLogs().get(0).getDescricao());
        }

        @Test
        @DisplayName("alterarDescricaoDetalhada de valor para nulo deve adicionar log 'removida'")
        void alterarDescricaoDetalhada_DeValorParaNulo_DeveAdicionarLogRemovida() {
            equipamento.alterarDescricaoDetalhada(null, usuarioTeste); // descPadrao -> null
            assertNull(equipamento.getDescricaoDetalhada());
            assertEquals(1, equipamento.getLogs().size());
            assertEquals("Descrição detalhada removida.", equipamento.getLogs().get(0).getDescricao());
        }

        @Test
        @DisplayName("alterarDescricaoDetalhada de valor para outro valor deve adicionar log 'alterada'")
        void alterarDescricaoDetalhada_DeValorParaOutroValor_DeveAdicionarLogAlterada() {
            equipamento.alterarDescricaoDetalhada("Outra Descrição", usuarioTeste);
            assertEquals("Outra Descrição", equipamento.getDescricaoDetalhada());
            assertEquals(1, equipamento.getLogs().size());
            assertEquals("Descrição detalhada alterada.", equipamento.getLogs().get(0).getDescricao());
        }

        @Test
        @DisplayName("alterarDescricaoDetalhada não deve adicionar log se a descrição for a mesma")
        void alterarDescricaoDetalhada_ComMesmaDescricao_NaoDeveAdicionarLog() {
            equipamento.alterarDescricaoDetalhada(descPadrao, usuarioTeste);
            assertTrue(equipamento.getLogs().isEmpty());
        }
    }

    @Nested
    @DisplayName("Testes para equals e hashCode")
    class EqualsHashCodeTests {
        @Test
        @DisplayName("Dois equipamentos não persistidos com mesmo serial devem ser iguais")
        void equals_NaoPersistidosMesmoSerial_DevemSerIguais() {
            Equipamento e1 = new Equipamento("EQ1", "SERIAL123", "d1");
            Equipamento e2 = new Equipamento("EQ2", "SERIAL123", "d2");
            assertEquals(e1, e2);
            assertEquals(e1.hashCode(), e2.hashCode());
        }

        @Test
        @DisplayName("Dois equipamentos não persistidos com seriais diferentes não devem ser iguais")
        void equals_NaoPersistidosSeriaisDiferentes_NaoDevemSerIguais() {
            Equipamento e1 = new Equipamento("EQ", "SERIAL1", "d");
            Equipamento e2 = new Equipamento("EQ", "SERIAL2", "d");
            assertNotEquals(e1, e2);
        }
    }
}