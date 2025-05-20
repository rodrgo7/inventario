package oliveiradev.inventario.domain.model.usuarios;

import oliveiradev.inventario.domain.model.enums.NivelUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario("Usuário Comum", "comum@email.com", "senha123");
    }

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
        assertThrows(NullPointerException.class, () -> {
            usuario.adicionarNivel(null);
        });
    }

    @Test
    @DisplayName("Deve limpar outros níveis ao adicionar USUARIO_MASTER")
    void adicionarNivel_QuandoAdicionaMaster_DeveLimparOutrosNiveis() {
        usuario.adicionarNivel(NivelUsuario.USUARIO_PADRAO);
        usuario.adicionarNivel(NivelUsuario.USUARIO_ADMIN);
        assertEquals(2, usuario.getNiveis().size());

        usuario.adicionarNivel(NivelUsuario.USUARIO_MASTER);
        assertEquals(1, usuario.getNiveis().size());
        assertTrue(usuario.getNiveis().contains(NivelUsuario.USUARIO_MASTER));
        assertFalse(usuario.getNiveis().contains(NivelUsuario.USUARIO_PADRAO));
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
    @DisplayName("Construtor deve validar nome nulo")
    void construtor_QuandoNomeNulo_DeveLancarNullPointerException() {
        assertThrows(NullPointerException.class, () -> {
            new Usuario(null, "email@valido.com", "senha123");
        });
    }

    @Test
    @DisplayName("Construtor deve validar nome vazio")
    void construtor_QuandoNomeVazio_DeveLancarIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Usuario("   ", "email@valido.com", "senha123");
        });
        assertEquals("O nome não pode ser vazio.", exception.getMessage());
    }
}