package oliveiradev.inventario.domain.model.usuarios;

import oliveiradev.inventario.domain.model.enums.NivelUsuario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {
    private Usuario usuario;
    private final String nomePadrao = "Usuário Padrão";
    private final String emailPadrao = "padrao@email.com";
    private final String senhaPadrao = "senha123";

    @BeforeEach
    void setUp() {
        usuario = new Usuario(nomePadrao, emailPadrao, senhaPadrao);
    }

    @Nested
    @DisplayName("Testes de Construtor")
    class ConstrutorTests {
        @Test
        @DisplayName("Deve criar usuário com dados válidos")
        void construtor_ComDadosValidos_DeveCriarUsuario() {
            assertNotNull(usuario);
            assertEquals(nomePadrao, usuario.getNome());
            assertEquals(emailPadrao.toLowerCase(), usuario.getEmail());
            assertEquals(senhaPadrao, usuario.getSenha());
            assertTrue(usuario.getNiveis().isEmpty(), "Usuário novo não deve ter níveis por padrão no construtor base.");
        }

        @Test
        @DisplayName("Deve lançar NullPointerException para nome nulo")
        void construtor_QuandoNomeNulo_DeveLancarNullPointerException() {
            assertThrows(NullPointerException.class, () -> new Usuario(null, emailPadrao, senhaPadrao));
        }

        @Test
        @DisplayName("Deve lançar IllegalArgumentException para nome vazio")
        void construtor_QuandoNomeVazio_DeveLancarIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () -> new Usuario("   ", emailPadrao, senhaPadrao));
        }

        @Test
        @DisplayName("Deve lançar NullPointerException para email nulo")
        void construtor_QuandoEmailNulo_DeveLancarNullPointerException() {
            assertThrows(NullPointerException.class, () -> new Usuario(nomePadrao, null, senhaPadrao));
        }

        @Test
        @DisplayName("Deve lançar IllegalArgumentException para email vazio")
        void construtor_QuandoEmailVazio_DeveLancarIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () -> new Usuario(nomePadrao, "  ", senhaPadrao));
        }

        @Test
        @DisplayName("Deve lançar IllegalArgumentException para email malformado")
        void construtor_QuandoEmailMalformado_DeveLancarIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () -> new Usuario(nomePadrao, "emailinvalido", senhaPadrao));
        }

        @Test
        @DisplayName("Deve lançar NullPointerException para senha nula")
        void construtor_QuandoSenhaNula_DeveLancarNullPointerException() {
            assertThrows(NullPointerException.class, () -> new Usuario(nomePadrao, emailPadrao, null));
        }
    }

    @Nested
    @DisplayName("Testes para Gerenciamento de Níveis")
    class NivelTests {
        @Test
        @DisplayName("Deve adicionar um nível ao usuário")
        void adicionarNivel_QuandoNivelValido_DeveAdicionarALista() {
            usuario.adicionarNivel(NivelUsuario.USUARIO_PADRAO);
            assertTrue(usuario.getNiveis().contains(NivelUsuario.USUARIO_PADRAO));
            assertEquals(1, usuario.getNiveis().size());
        }

        @Test
        @DisplayName("Não deve adicionar nível nulo")
        void adicionarNivel_QuandoNivelNulo_DeveLancarExcecao() {
            assertThrows(NullPointerException.class, () -> usuario.adicionarNivel(null));
        }

        @Test
        @DisplayName("Deve limpar outros níveis ao adicionar USUARIO_MASTER")
        void adicionarNivel_QuandoAdicionaMaster_DeveLimparOutrosNiveis() {
            usuario.adicionarNivel(NivelUsuario.USUARIO_PADRAO);
            usuario.adicionarNivel(NivelUsuario.USUARIO_ADMIN);
            usuario.adicionarNivel(NivelUsuario.USUARIO_MASTER);
            assertEquals(1, usuario.getNiveis().size());
            assertTrue(usuario.getNiveis().contains(NivelUsuario.USUARIO_MASTER));
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar adicionar outro nível a um USUARIO_MASTER")
        void adicionarNivel_QuandoUsuarioJaEhMaster_DeveLancarExcecaoAoAdicionarOutroNivel() {
            usuario.adicionarNivel(NivelUsuario.USUARIO_MASTER);
            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
                usuario.adicionarNivel(NivelUsuario.USUARIO_PADRAO);
            });
            assertEquals("Usuário MASTER não pode ter outros níveis adicionados.", exception.getMessage());
        }

        @Test
        @DisplayName("Remover nível existente")
        void removerNivel_QuandoNivelExiste_DeveRemoverDaLista() {
            usuario.adicionarNivel(NivelUsuario.USUARIO_PADRAO);
            usuario.adicionarNivel(NivelUsuario.USUARIO_ADMIN);
            usuario.removerNivel(NivelUsuario.USUARIO_PADRAO);
            assertFalse(usuario.getNiveis().contains(NivelUsuario.USUARIO_PADRAO));
            assertTrue(usuario.getNiveis().contains(NivelUsuario.USUARIO_ADMIN));
            assertEquals(1, usuario.getNiveis().size());
        }

        @Test
        @DisplayName("Remover nível inexistente não deve alterar a lista")
        void removerNivel_QuandoNivelNaoExiste_NaoDeveAlterarLista() {
            usuario.adicionarNivel(NivelUsuario.USUARIO_ADMIN);
            int tamanhoAntes = usuario.getNiveis().size();
            usuario.removerNivel(NivelUsuario.USUARIO_PADRAO);
            assertEquals(tamanhoAntes, usuario.getNiveis().size());
            assertTrue(usuario.getNiveis().contains(NivelUsuario.USUARIO_ADMIN));
        }

        @Test
        @DisplayName("Remover nível nulo deve lançar NullPointerException")
        void removerNivel_QuandoNivelNulo_DeveLancarNullPointerException() {
            assertThrows(NullPointerException.class, () -> usuario.removerNivel(null));
        }

        @Test
        @DisplayName("definirNiveis deve substituir todos os níveis e respeitar regras")
        void definirNiveis_ComNovosNiveis_DeveSubstituirCorretamente() {
            usuario.adicionarNivel(NivelUsuario.USUARIO_PADRAO);
            Set<NivelUsuario> novosNiveis = Set.of(NivelUsuario.USUARIO_ADMIN);
            usuario.definirNiveis(novosNiveis);
            assertEquals(1, usuario.getNiveis().size());
            assertTrue(usuario.getNiveis().contains(NivelUsuario.USUARIO_ADMIN));
            assertFalse(usuario.getNiveis().contains(NivelUsuario.USUARIO_PADRAO));
        }

        @Test
        @DisplayName("definirNiveis com USUARIO_MASTER deve limpar outros e adicionar MASTER")
        void definirNiveis_ComMasterEOutros_DeveManterApenasMaster() {
            Set<NivelUsuario> novosNiveis = Set.of(NivelUsuario.USUARIO_PADRAO, NivelUsuario.USUARIO_MASTER);
            usuario.definirNiveis(novosNiveis);
            assertEquals(1, usuario.getNiveis().size());
            assertTrue(usuario.getNiveis().contains(NivelUsuario.USUARIO_MASTER));
        }

        @Test
        @DisplayName("definirNiveis com conjunto nulo deve lançar NullPointerException")
        void definirNiveis_ComConjuntoNulo_DeveLancarNullPointerException() {
            assertThrows(NullPointerException.class, () -> usuario.definirNiveis(null));
        }

        @Test
        @DisplayName("possuiNivel deve retornar true se o usuário possui o nível")
        void possuiNivel_QuandoPossui_DeveRetornarTrue() {
            usuario.adicionarNivel(NivelUsuario.USUARIO_ADMIN);
            assertTrue(usuario.possuiNivel(NivelUsuario.USUARIO_ADMIN));
        }

        @Test
        @DisplayName("possuiNivel deve retornar false se o usuário não possui o nível")
        void possuiNivel_QuandoNaoPossui_DeveRetornarFalse() {
            assertFalse(usuario.possuiNivel(NivelUsuario.USUARIO_MASTER));
        }
    }

    @Nested
    @DisplayName("Testes para Métodos de Alteração de Dados")
    class AlteracaoDadosTests {
        @Test
        @DisplayName("alterarNome deve atualizar o nome")
        void alterarNome_ComNomeValido_DeveAtualizarNome() {
            String novoNome = "Nome Atualizado";
            usuario.alterarNome(novoNome);
            assertEquals(novoNome, usuario.getNome());
        }

        @Test
        @DisplayName("alterarNome com nome nulo deve lançar NullPointerException")
        void alterarNome_ComNomeNulo_DeveLancarNullPointerException() {
            assertThrows(NullPointerException.class, () -> usuario.alterarNome(null));
        }

        @Test
        @DisplayName("alterarNome com nome vazio deve lançar IllegalArgumentException")
        void alterarNome_ComNomeVazio_DeveLancarIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () -> usuario.alterarNome("  "));
        }

        @Test
        @DisplayName("alterarEmail deve atualizar o email e converter para minúsculas")
        void alterarEmail_ComEmailValido_DeveAtualizarEmail() {
            String novoEmail = "NOVO@EMAIL.COM";
            usuario.alterarEmail(novoEmail);
            assertEquals("novo@email.com", usuario.getEmail());
        }

        @Test
        @DisplayName("definirSenha deve atualizar a senha")
        void definirSenha_ComSenhaValida_DeveAtualizarSenha() {
            String novaSenhaHashed = "novaSenhaHashed";
            usuario.definirSenha(novaSenhaHashed);
            assertEquals(novaSenhaHashed, usuario.getSenha());
        }
    }

    @Nested
    @DisplayName("Testes para equals e hashCode")
    class EqualsHashCodeTests {
        @Test
        @DisplayName("Dois usuários não persistidos com mesmo email devem ser iguais")
        void equals_NaoPersistidosMesmoEmail_DevemSerIguais() {
            Usuario u1 = new Usuario("Nome", "teste@email.com", "s1");
            Usuario u2 = new Usuario("Outro Nome", "teste@email.com", "s2");
            assertEquals(u1, u2);
            assertEquals(u1.hashCode(), u2.hashCode());
        }

        @Test
        @DisplayName("Dois usuários não persistidos com emails diferentes não devem ser iguais")
        void equals_NaoPersistidosEmailsDiferentes_NaoDevemSerIguais() {
            Usuario u1 = new Usuario("Nome", "teste1@email.com", "s1");
            Usuario u2 = new Usuario("Nome", "teste2@email.com", "s2");
            assertNotEquals(u1, u2);
        }
    }
}