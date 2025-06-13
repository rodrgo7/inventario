package oliveiradev.inventario.domain.model.usuarios;

import oliveiradev.inventario.domain.model.enums.NivelUsuario;
import oliveiradev.inventario.domain.model.usuarios.Usuario;
import oliveiradev.inventario.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@DataMongoTest
class UsuarioRepositoryTest {

    @Container
    static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"));
    // Para MongoDB 5 ou 6, pode ser "mongo:6.0" ou "mongo:5.0"

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        String connectionString = mongoDBContainer.getConnectionString() + "/estoquedb";
        registry.add("spring.data.mongodb.uri", () -> connectionString);
    }

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario usuario1;

    @BeforeEach
    void setUp() {
        // Limpar o repositório antes de cada teste para evitar interferência
        usuarioRepository.deleteAll();

        // Criar e salvar um usuário de teste
        usuario1 = new Usuario("Usuário Teste 1", "teste1@email.com", "senhaHashed");
        usuario1.adicionarNivel(NivelUsuario.USUARIO_PADRAO);
        usuarioRepository.save(usuario1);
    }

    @AfterEach
    void tearDown() {
        usuarioRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve encontrar um usuário pelo email quando o email existe")
    void findByEmail_QuandoEmailExiste_DeveRetornarUsuario() {
        Optional<Usuario> usuarioEncontrado = usuarioRepository.findByEmail("teste1@email.com");

        assertTrue(usuarioEncontrado.isPresent());
        assertEquals("teste1@email.com", usuarioEncontrado.get().getEmail());
        assertEquals("Usuário Teste 1", usuarioEncontrado.get().getNome());
    }

    @Test
    @DisplayName("Não deve encontrar um usuário pelo email quando o email não existe")
    void findByEmail_QuandoEmailNaoExiste_DeveRetornarOptionalVazio() {
        Optional<Usuario> usuarioEncontrado = usuarioRepository.findByEmail("naoexiste@email.com");

        assertFalse(usuarioEncontrado.isPresent());
    }

    @Test
    @DisplayName("Deve salvar um novo usuário corretamente")
    void save_NovoUsuario_DevePersistirComId() {
        Usuario novoUsuario = new Usuario("Novo Usuário", "novo@email.com", "outrasenha");
        novoUsuario.adicionarNivel(NivelUsuario.USUARIO_ADMIN);

        Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);

        assertNotNull(usuarioSalvo);
        assertNotNull(usuarioSalvo.getId()); // MongoDB deve ter gerado um ID
        assertEquals("Novo Usuário", usuarioSalvo.getNome());
        assertEquals("novo@email.com", usuarioSalvo.getEmail());
        assertTrue(usuarioSalvo.getNiveis().contains(NivelUsuario.USUARIO_ADMIN));

        // Verifica se ele pode ser encontrado
        Optional<Usuario> usuarioRecuperado = usuarioRepository.findById(usuarioSalvo.getId());
        assertTrue(usuarioRecuperado.isPresent());
        assertEquals("Novo Usuário", usuarioRecuperado.get().getNome());
    }
}