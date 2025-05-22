package oliveiradev.inventario.interfaces.controller.Equipamentos;

import oliveiradev.inventario.application.dto.equipamento.EquipamentoAtualizacaoDTO;
import oliveiradev.inventario.application.dto.equipamento.EquipamentoCriacaoDTO;
import oliveiradev.inventario.application.dto.equipamento.EquipamentoRespostaDTO;
import oliveiradev.inventario.application.dto.equipamento.LogAlteracaoDTO;
import oliveiradev.inventario.application.exception.RecursoNaoEncontradoException;
import oliveiradev.inventario.application.exception.RegraDeNegocioException;
import oliveiradev.inventario.application.service.EquipamentoAppService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EquipamentoControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private EquipamentoAppService equipamentoAppService;
    private EquipamentoCriacaoDTO equipamentoCriacaoDTO;
    private EquipamentoRespostaDTO equipamentoRespostaDTO;
    private EquipamentoAtualizacaoDTO equipamentoAtualizacaoDTO;

    @BeforeEach
    void setUp() {
        equipamentoCriacaoDTO = new EquipamentoCriacaoDTO(
                "Impressora Laser", "SN-LASER-PRINT01", "Impressora rápida P&B"
        );

        List<LogAlteracaoDTO> logs = List.of(new LogAlteracaoDTO(LocalDateTime.now(), "user@test.com", "Criado"));
        equipamentoRespostaDTO = new EquipamentoRespostaDTO(
                "equipId123", "Impressora Laser", "SN-LASER-PRINT01", "Impressora rápida P&B",
                LocalDateTime.now(), LocalDateTime.now(), "user@test.com", "user@test.com", logs
        );

        equipamentoAtualizacaoDTO = new EquipamentoAtualizacaoDTO("Impressora Laser XL", "Nova descrição P&B");
    }

    // --- Testes para criarEquipamento (POST /api/equipamentos) ---
    @Test
    @DisplayName("POST /api/equipamentos - Deve criar equipamento com sucesso para ADMIN")
    @WithMockUser(username = "admin@test.com", roles = {"USUARIO_ADMIN"}) // Simula usuário ADMIN
    void criarEquipamento_ComoAdmin_DeveRetornarCreated() throws Exception {
        when(equipamentoAppService.criarEquipamento(any(EquipamentoCriacaoDTO.class))).thenReturn(equipamentoRespostaDTO);

        mockMvc.perform(post("/api/equipamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(equipamentoCriacaoDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is("equipId123")))
                .andExpect(jsonPath("$.nome", is(equipamentoCriacaoDTO.nome())));
    }

    @Test
    @DisplayName("POST /api/equipamentos - Deve retornar 403 Forbidden para USUARIO_PADRAO")
    @WithMockUser(username = "padrao@test.com", roles = {"USUARIO_PADRAO"})
    void criarEquipamento_ComoPadrao_DeveRetornarForbidden() throws Exception {
        mockMvc.perform(post("/api/equipamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(equipamentoCriacaoDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/equipamentos - Deve retornar 401 Unauthorized se não autenticado")
    void criarEquipamento_NaoAutenticado_DeveRetornarUnauthorized() throws Exception {
        mockMvc.perform(post("/api/equipamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(equipamentoCriacaoDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/equipamentos - Deve retornar 409 Conflict se número de série duplicado")
    @WithMockUser(username = "admin@test.com", roles = {"USUARIO_ADMIN"})
    void criarEquipamento_NumeroSerieDuplicado_DeveRetornarConflict() throws Exception {
        when(equipamentoAppService.criarEquipamento(any(EquipamentoCriacaoDTO.class)))
                .thenThrow(new RegraDeNegocioException("Número de série já existe"));

        mockMvc.perform(post("/api/equipamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(equipamentoCriacaoDTO)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Número de série já existe"));
    }

    @Test
    @DisplayName("POST /api/equipamentos - Deve retornar 400 Bad Request para DTO inválido")
    @WithMockUser(username = "admin@test.com", roles = {"USUARIO_ADMIN"})
    void criarEquipamento_ComDTOInvalido_DeveRetornarBadRequest() throws Exception {
        EquipamentoCriacaoDTO dtoInvalido = new EquipamentoCriacaoDTO("", "", null);

        // Não precisamos mockar o serviço aqui, pois a validação do DTO deve falhar antes
        mockMvc.perform(post("/api/equipamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest());
    }


    // --- Testes para listarTodosEquipamentos (GET /api/equipamentos) ---
    @Test
    @DisplayName("GET /api/equipamentos - Deve retornar lista de equipamentos para usuário autenticado")
    @WithMockUser(username = "user@test.com", roles = {"USUARIO_PADRAO"}) // Qualquer usuário autenticado
    void listarTodosEquipamentos_Autenticado_DeveRetornarLista() throws Exception {
        when(equipamentoAppService.listarTodosEquipamentos()).thenReturn(List.of(equipamentoRespostaDTO));

        mockMvc.perform(get("/api/equipamentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is("equipId123")));
    }

    // --- Testes para buscarEquipamentoPorId (GET /api/equipamentos/{id}) ---
    @Test
    @DisplayName("GET /api/equipamentos/{id} - Deve retornar equipamento se ID existe para usuário autenticado")
    @WithMockUser(username = "user@test.com") // Qualquer usuário autenticado
    void buscarEquipamentoPorId_ExistenteEAutenticado_DeveRetornarEquipamento() throws Exception {
        when(equipamentoAppService.buscarEquipamentoPorId("equipId123")).thenReturn(Optional.of(equipamentoRespostaDTO));

        mockMvc.perform(get("/api/equipamentos/equipId123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("equipId123")));
    }

    @Test
    @DisplayName("GET /api/equipamentos/{id} - Deve retornar 404 Not Found se ID não existe")
    @WithMockUser(username = "user@test.com")
    void buscarEquipamentoPorId_NaoExistente_DeveRetornarNotFound() throws Exception {
        when(equipamentoAppService.buscarEquipamentoPorId("idNaoExiste")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/equipamentos/idNaoExiste"))
                .andExpect(status().isNotFound());
    }


    // --- Testes para atualizarEquipamento (PUT /api/equipamentos/{id}) ---
    @Test
    @DisplayName("PUT /api/equipamentos/{id} - Deve atualizar com sucesso para ADMIN")
    @WithMockUser(username = "admin@test.com", roles = {"USUARIO_ADMIN"})
    void atualizarEquipamento_ComoAdmin_DeveRetornarOk() throws Exception {
        EquipamentoRespostaDTO dtoAtualizado = new EquipamentoRespostaDTO(
                "equipId123", "Impressora Laser XL", "SN-LASER-PRINT01", "Nova descrição P&B",
                LocalDateTime.now(), LocalDateTime.now(), "user@test.com", "admin@test.com", Collections.emptyList()
        );
        when(equipamentoAppService.atualizarEquipamento(eq("equipId123"), any(EquipamentoAtualizacaoDTO.class)))
                .thenReturn(dtoAtualizado);

        mockMvc.perform(put("/api/equipamentos/equipId123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(equipamentoAtualizacaoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Impressora Laser XL")))
                .andExpect(jsonPath("$.modificadoPorUsuario", is("admin@test.com")));
    }

    @Test
    @DisplayName("PUT /api/equipamentos/{id} - Deve retornar 403 Forbidden para USUARIO_PADRAO")
    @WithMockUser(username = "padrao@test.com", roles = {"USUARIO_PADRAO"})
    void atualizarEquipamento_ComoPadrao_DeveRetornarForbidden() throws Exception {
        mockMvc.perform(put("/api/equipamentos/equipId123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(equipamentoAtualizacaoDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PUT /api/equipamentos/{id} - Deve retornar 404 Not Found se equipamento não existe")
    @WithMockUser(username = "admin@test.com", roles = {"USUARIO_ADMIN"})
    void atualizarEquipamento_IdNaoExistente_DeveRetornarNotFound() throws Exception {
        when(equipamentoAppService.atualizarEquipamento(eq("idNaoExiste"), any(EquipamentoAtualizacaoDTO.class)))
                .thenThrow(new RecursoNaoEncontradoException("Equipamento não encontrado"));

        mockMvc.perform(put("/api/equipamentos/idNaoExiste")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(equipamentoAtualizacaoDTO)))
                .andExpect(status().isNotFound());
    }


    // --- Testes para deletarEquipamento (DELETE /api/equipamentos/{id}) ---
    @Test
    @DisplayName("DELETE /api/equipamentos/{id} - Deve deletar com sucesso para MASTER")
    @WithMockUser(username = "master@test.com", roles = {"USUARIO_MASTER"})
    void deletarEquipamento_ComoMaster_DeveRetornarNoContent() throws Exception {
        doNothing().when(equipamentoAppService).deletarEquipamento("equipId123");

        mockMvc.perform(delete("/api/equipamentos/equipId123"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/equipamentos/{id} - Deve retornar 403 Forbidden para USUARIO_PADRAO")
    @WithMockUser(username = "padrao@test.com", roles = {"USUARIO_PADRAO"})
    void deletarEquipamento_ComoPadrao_DeveRetornarForbidden() throws Exception {
        mockMvc.perform(delete("/api/equipamentos/equipId123"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE /api/equipamentos/{id} - Deve retornar 404 Not Found se equipamento não existe")
    @WithMockUser(username = "admin@test.com", roles = {"USUARIO_ADMIN"})
    void deletarEquipamento_IdNaoExistente_DeveRetornarNotFound() throws Exception {
        doThrow(new RecursoNaoEncontradoException("Equipamento não encontrado"))
                .when(equipamentoAppService).deletarEquipamento("idNaoExiste");

        mockMvc.perform(delete("/api/equipamentos/idNaoExiste"))
                .andExpect(status().isNotFound());
    }
}