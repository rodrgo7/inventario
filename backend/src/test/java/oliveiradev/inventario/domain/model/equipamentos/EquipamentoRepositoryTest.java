package oliveiradev.inventario.domain.repository;

import oliveiradev.inventario.domain.model.equipamentos.Equipamento;
import oliveiradev.inventario.domain.model.equipamentos.LogAlteracao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@DataMongoTest
class EquipamentoRepositoryTest {
    @Container
    static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"));

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        // Usar getConnectionString() para uma instância standalone simples do MongoDB via Testcontainers
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getConnectionString);
    }

    @Autowired
    private EquipamentoRepository equipamentoRepository;
    @Autowired
    private MongoTemplate mongoTemplate;
    private Equipamento equipamento1;
    private Equipamento equipamento2;

    @BeforeEach
    void setUp() {
        equipamentoRepository.deleteAll(); // Limpa antes de cada teste

        equipamento1 = new Equipamento("Notebook Gamer Alien", "SN-ALIEN-001", "Notebook para jogos pesados");
        equipamento1.adicionarLog("setup_user@test.com", "Equipamento 1 criado para teste.");
        equipamentoRepository.save(equipamento1);

        equipamento2 = new Equipamento("Monitor Dell 24", "SN-DELL-M24", "Monitor Full HD para escritório");
        equipamento2.adicionarLog("setup_user@test.com", "Equipamento 2 criado para teste.");
        equipamentoRepository.save(equipamento2);
    }

    @AfterEach
    void tearDown() {
        equipamentoRepository.deleteAll(); // Limpa após cada teste
    }

    @Test
    @DisplayName("Deve encontrar um equipamento pelo número de série quando existe")
    void findByNumeroDeSerie_QuandoExiste_DeveRetornarEquipamento() {
        Optional<Equipamento> encontrado = equipamentoRepository.findByNumeroDeSerie("SN-ALIEN-001");
        assertTrue(encontrado.isPresent());
        assertEquals("SN-ALIEN-001", encontrado.get().getNumeroDeSerie());
        assertEquals(equipamento1.getNome(), encontrado.get().getNome());
    }

    @Test
    @DisplayName("Não deve encontrar equipamento por número de série quando não existe")
    void findByNumeroDeSerie_QuandoNaoExiste_DeveRetornarOptionalVazio() {
        Optional<Equipamento> encontrado = equipamentoRepository.findByNumeroDeSerie("SN-INEXISTENTE-999");
        assertFalse(encontrado.isPresent());
    }

    @Test
    @DisplayName("Deve salvar um novo equipamento corretamente com ID e campos de auditoria")
    void save_NovoEquipamento_DevePersistirCorretamente() {
        Equipamento novoEquipamento = new Equipamento("Teclado Mecânico RGB", "SN-TECRGB-007", "Teclado com switches Cherry MX Red");
        novoEquipamento.adicionarLog("criador@teste.com", "Registro inicial do teclado.");

        Equipamento salvo = equipamentoRepository.save(novoEquipamento);

        assertNotNull(salvo);
        assertNotNull(salvo.getId(), "ID não deveria ser nulo após salvar");
        assertEquals("Teclado Mecânico RGB", salvo.getNome());
        assertEquals("SN-TECRGB-007", salvo.getNumeroDeSerie());
        assertFalse(salvo.getLogs().isEmpty(), "Logs não deveriam estar vazios");
        assertEquals("Registro inicial do teclado.", salvo.getLogs().get(0).getDescricao());
    }

    @Test
    @DisplayName("Deve falhar ao salvar equipamento com número de série duplicado devido ao índice unique")
    void save_ComNumeroDeSerieDuplicado_DeveLancarExcecao() {
        Equipamento equipamentoDuplicado = new Equipamento("Outro Nome", "SN-ALIEN-001", "Tentativa de duplicar serial");

        assertThrows(DuplicateKeyException.class, () -> {
            equipamentoRepository.save(equipamentoDuplicado);
        });
    }

    @Test
    @DisplayName("Deve listar todos os equipamentos salvos")
    void findAll_DeveRetornarTodosEquipamentos() {
        List<Equipamento> todos = equipamentoRepository.findAll();
        assertEquals(2, todos.size(), "Deveria haver 2 equipamentos do setUp");
    }

    @Test
    @DisplayName("Deve deletar um equipamento por ID")
    void deleteById_QuandoEquipamentoExiste_DeveRemoverDoBanco() {
        String idParaDeletar = equipamento1.getId();
        assertNotNull(idParaDeletar);
        assertTrue(equipamentoRepository.findById(idParaDeletar).isPresent(), "Equipamento deveria existir antes de deletar");

        equipamentoRepository.deleteById(idParaDeletar);

        assertFalse(equipamentoRepository.findById(idParaDeletar).isPresent(), "Equipamento não deveria mais existir após deletar");
    }

    @Test
    @DisplayName("Deve atualizar um equipamento e seus logs")
    void update_EquipamentoExistente_DeveRefletirMudancas() {
        Optional<Equipamento> optionalEquip = equipamentoRepository.findById(equipamento1.getId());
        assertTrue(optionalEquip.isPresent());
        Equipamento equipParaAtualizar = optionalEquip.get();

        String novoNome = "Notebook Alienware Atualizado";
        String userAtualizador = "updater@test.com";
        equipParaAtualizar.alterarNome(novoNome, userAtualizador);
        equipParaAtualizar.adicionarLog(userAtualizador, "Status alterado para 'Em Uso'.");

        Equipamento atualizado = equipamentoRepository.save(equipParaAtualizar);

        assertNotNull(atualizado);
        assertEquals(novoNome, atualizado.getNome());
        assertEquals(2, atualizado.getLogs().size(), "Deveria ter o log original + 2 novos logs (nome e status)");
        assertTrue(atualizado.getLogs().get(1).getDescricao().contains("Nome alterado"));
        assertEquals("Status alterado para 'Em Uso'.", atualizado.getLogs().get(2).getDescricao()); // Ajustar índice se o log de criação da entidade for diferente
    }

    @Test
    @DisplayName("findByNomeContainingIgnoreCase deve encontrar equipamentos")
    void findByNomeContainingIgnoreCase_DeveRetornarEquipamentosCorrespondentes() {
        List<Equipamento> encontrados = equipamentoRepository.findByNomeContainingIgnoreCase("alien");
        assertEquals(1, encontrados.size());
        assertEquals(equipamento1.getNome(), encontrados.get(0).getNome());

        List<Equipamento> encontradosDell = equipamentoRepository.findByNomeContainingIgnoreCase("dell");
        assertEquals(1, encontradosDell.size());
        assertEquals(equipamento2.getNome(), encontradosDell.get(0).getNome());

        List<Equipamento> naoEncontrados = equipamentoRepository.findByNomeContainingIgnoreCase("inexistente");
        assertTrue(naoEncontrados.isEmpty());
    }
}