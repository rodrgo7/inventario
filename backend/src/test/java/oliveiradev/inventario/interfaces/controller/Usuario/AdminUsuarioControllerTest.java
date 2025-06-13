package oliveiradev.inventario.interfaces.controller.Usuario;

import oliveiradev.inventario.application.dto.usuario.UsuarioRespostaDTO;
import oliveiradev.inventario.application.exception.RecursoNaoEncontradoException;
import oliveiradev.inventario.application.exception.RegraDeNegocioException;
import oliveiradev.inventario.application.service.UsuarioAppService;
import oliveiradev.inventario.domain.model.enums.NivelUsuario;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasItem;

@SpringBootTest
@AutoConfigureMockMvc
class AdminUsuarioControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UsuarioAppService usuarioAppService;
    private String usuarioAlvoId = "userTargetId123";
    private UsuarioRespostaDTO usuarioRespostaDTOMock;
    private Set<NivelUsuario> novosNiveisParaAdmin;

    @BeforeEach
    void setUp() {
        novosNiveisParaAdmin = Set.of(NivelUsuario.USUARIO_ADMIN);

        // DTO de resposta base para os mocks do serviço quando a atualização é bem-sucedida
        usuarioRespostaDTOMock = new UsuarioRespostaDTO(
                usuarioAlvoId,
                "Usuário Alvo Atualizado",
                "alvo@email.com",
                novosNiveisParaAdmin,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now()
        );
    }

    @Nested
    @DisplayName("Testes para Atualizar Níveis de Usuário (PUT /api/admin/usuarios/{id}/niveis)")
    class AtualizarNiveisTests {

        @Test
        @DisplayName("Deve atualizar níveis com sucesso para USUARIO_MASTER")
        @WithMockUser(username = "master@test.com", roles = {"USUARIO_MASTER"})
        void atualizarNiveisDeUsuario_ComoMaster_DeveRetornarOkComUsuarioAtualizado() throws Exception {
            when(usuarioAppService.atualizarNiveisUsuario(eq(usuarioAlvoId), eq(novosNiveisParaAdmin)))
                    .thenReturn(usuarioRespostaDTOMock);

            mockMvc.perform(put("/api/admin/usuarios/{usuarioId}/niveis", usuarioAlvoId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(novosNiveisParaAdmin)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(usuarioAlvoId)))
                    .andExpect(jsonPath("$.niveis", hasItem("USUARIO_ADMIN"))); // Verifica se o nível está na resposta
        }

        @Test
        @DisplayName("Deve retornar 403 Forbidden para USUARIO_ADMIN ao tentar atualizar níveis")
        @WithMockUser(username = "admin@test.com", roles = {"USUARIO_ADMIN"})
        void atualizarNiveisDeUsuario_ComoAdmin_DeveRetornarForbidden() throws Exception {
            mockMvc.perform(put("/api/admin/usuarios/{usuarioId}/niveis", usuarioAlvoId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(novosNiveisParaAdmin)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Deve retornar 403 Forbidden para USUARIO_PADRAO ao tentar atualizar níveis")
        @WithMockUser(username = "padrao@test.com", roles = {"USUARIO_PADRAO"})
        void atualizarNiveisDeUsuario_ComoPadrao_DeveRetornarForbidden() throws Exception {
            mockMvc.perform(put("/api/admin/usuarios/{usuarioId}/niveis", usuarioAlvoId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(novosNiveisParaAdmin)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Deve retornar 401 Unauthorized se não autenticado ao tentar atualizar níveis")
        void atualizarNiveisDeUsuario_NaoAutenticado_DeveRetornarUnauthorized() throws Exception {
            mockMvc.perform(put("/api/admin/usuarios/{usuarioId}/niveis", usuarioAlvoId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(novosNiveisParaAdmin)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Deve retornar 404 Not Found se usuário alvo não existe ao atualizar níveis")
        @WithMockUser(username = "master@test.com", roles = {"USUARIO_MASTER"})
        void atualizarNiveisDeUsuario_UsuarioAlvoNaoEncontrado_DeveRetornarNotFound() throws Exception {
            String idInexistente = "idNaoExiste";
            when(usuarioAppService.atualizarNiveisUsuario(eq(idInexistente), eq(novosNiveisParaAdmin)))
                    .thenThrow(new RecursoNaoEncontradoException("Usuário com ID " + idInexistente + " não encontrado."));

            mockMvc.perform(put("/api/admin/usuarios/{usuarioId}/niveis", idInexistente)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(novosNiveisParaAdmin)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.detail", is("Usuário com ID " + idInexistente + " não encontrado.")));
        }

        @Test
        @DisplayName("Deve retornar 400 Bad Request para regra de negócio violada ao atualizar níveis")
        @WithMockUser(username = "master@test.com", roles = {"USUARIO_MASTER"})
        void atualizarNiveisDeUsuario_RegraDeNegocioViolada_DeveRetornarBadRequest() throws Exception {
            Set<NivelUsuario> niveisProblematicos = Set.of(NivelUsuario.USUARIO_PADRAO); // Exemplo de regra
            String mensagemErroRegra = "Usuário MASTER não pode ser rebaixado para PADRAO por esta operação.";

            when(usuarioAppService.atualizarNiveisUsuario(eq(usuarioAlvoId), eq(niveisProblematicos)))
                    .thenThrow(new RegraDeNegocioException(mensagemErroRegra));

            mockMvc.perform(put("/api/admin/usuarios/{usuarioId}/niveis", usuarioAlvoId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(niveisProblematicos)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.detail", is(mensagemErroRegra)));
        }

        @Test
        @DisplayName("Deve retornar 400 Bad Request se o corpo da requisição (Set de Niveis) for nulo ou inválido")
        @WithMockUser(username = "master@test.com", roles = {"USUARIO_MASTER"})
        void atualizarNiveisDeUsuario_ComCorpoRequisicaoInvalido_DeveRetornarBadRequest() throws Exception {

            when(usuarioAppService.atualizarNiveisUsuario(eq(usuarioAlvoId), any()))
                    .thenThrow(new IllegalArgumentException("O conjunto de novos níveis não pode ser vazio ou nulo."));


            mockMvc.perform(put("/api/admin/usuarios/{usuarioId}/niveis", usuarioAlvoId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Collections.emptySet()))) // Enviando conjunto vazio
                    .andExpect(status().isBadRequest());
        }
    }
}