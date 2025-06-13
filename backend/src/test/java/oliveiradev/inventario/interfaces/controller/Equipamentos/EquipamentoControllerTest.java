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
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
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
    private String equipamentoIdExistente = "equipId123";
    private String numeroDeSerieExistente = "SN-LASER-PRINT01";

    @BeforeEach
    void setUp() {
        equipamentoCriacaoDTO = new EquipamentoCriacaoDTO(
                "Impressora Laser", numeroDeSerieExistente, "Impressora rápida P&B"
        );

        List<LogAlteracaoDTO> logs = List.of(new LogAlteracaoDTO(LocalDateTime.now(), "user@test.com", "Criado"));
        equipamentoRespostaDTO = new EquipamentoRespostaDTO(
                equipamentoIdExistente, "Impressora Laser", numeroDeSerieExistente, "Impressora rápida P&B",
                LocalDateTime.now(), LocalDateTime.now(), "user@test.com", "user@test.com", logs
        );

        equipamentoAtualizacaoDTO = new EquipamentoAtualizacaoDTO("Impressora Laser XL", "Nova descrição P&B");
    }

    @Nested
    @DisplayName("Testes para Criar Equipamento (POST /api/equipamentos)")
    class CriarEquipamentoTests {
        @Test
        @DisplayName("Deve criar equipamento com sucesso para ADMIN")
        @WithMockUser(username = "admin@test.com", roles = {"USUARIO_ADMIN"})
        void criarEquipamento_ComoAdmin_DeveRetornarCreated() throws Exception {
            when(equipamentoAppService.criarEquipamento(any(EquipamentoCriacaoDTO.class))).thenReturn(equipamentoRespostaDTO);

            mockMvc.perform(post("/api/equipamentos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(equipamentoCriacaoDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", is(equipamentoIdExistente)))
                    .andExpect(jsonPath("$.nome", is(equipamentoCriacaoDTO.nome())));
        }

        @Test
        @DisplayName("Deve criar equipamento com sucesso para MASTER")
        @WithMockUser(username = "master@test.com", roles = {"USUARIO_MASTER"})
        void criarEquipamento_ComoMaster_DeveRetornarCreated() throws Exception {
            when(equipamentoAppService.criarEquipamento(any(EquipamentoCriacaoDTO.class))).thenReturn(equipamentoRespostaDTO);

            mockMvc.perform(post("/api/equipamentos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(equipamentoCriacaoDTO)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("Deve retornar 403 Forbidden para USUARIO_PADRAO ao tentar criar")
        @WithMockUser(username = "padrao@test.com", roles = {"USUARIO_PADRAO"})
        void criarEquipamento_ComoPadrao_DeveRetornarForbidden() throws Exception {
            mockMvc.perform(post("/api/equipamentos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(equipamentoCriacaoDTO)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Deve retornar 401 Unauthorized se não autenticado ao tentar criar")
        void criarEquipamento_NaoAutenticado_DeveRetornarUnauthorized() throws Exception {
            mockMvc.perform(post("/api/equipamentos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(equipamentoCriacaoDTO)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Deve retornar 409 Conflict se número de série duplicado")
        @WithMockUser(username = "admin@test.com", roles = {"USUARIO_ADMIN"})
        void criarEquipamento_NumeroSerieDuplicado_DeveRetornarConflict() throws Exception {
            when(equipamentoAppService.criarEquipamento(any(EquipamentoCriacaoDTO.class)))
                    .thenThrow(new RegraDeNegocioException("Número de série '" + equipamentoCriacaoDTO.numeroDeSerie() + "' já cadastrado."));

            mockMvc.perform(post("/api/equipamentos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(equipamentoCriacaoDTO)))
                    .andExpect(status().isConflict()) // Assumindo que seu @ControllerAdvice mapeia RegraDeNegocioException para 409 ou 400
                    .andExpect(jsonPath("$.detail", is("Número de série '" + equipamentoCriacaoDTO.numeroDeSerie() + "' já cadastrado.")));
        }

        @Test
        @DisplayName("Deve retornar 400 Bad Request para DTO inválido ao criar")
        @WithMockUser(username = "admin@test.com", roles = {"USUARIO_ADMIN"})
        void criarEquipamento_ComDTOInvalido_DeveRetornarBadRequest() throws Exception {
            EquipamentoCriacaoDTO dtoInvalido = new EquipamentoCriacaoDTO("", "SN-INVALID", null); // Nome vazio

            mockMvc.perform(post("/api/equipamentos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dtoInvalido)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Testes para Listar Todos Equipamentos (GET /api/equipamentos)")
    class ListarEquipamentosTests {
        @Test
        @DisplayName("Deve retornar lista para USUARIO_PADRAO")
        @WithMockUser(username = "padrao@test.com", roles = {"USUARIO_PADRAO"})
        void listarTodosEquipamentos_ComoPadrao_DeveRetornarLista() throws Exception {
            when(equipamentoAppService.listarTodosEquipamentos()).thenReturn(List.of(equipamentoRespostaDTO));

            mockMvc.perform(get("/api/equipamentos"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id", is(equipamentoIdExistente)));
        }

        // Testes similares para ADMIN e MASTER também retornando lista
        @Test
        @DisplayName("Deve retornar lista para USUARIO_ADMIN")
        @WithMockUser(username = "admin@test.com", roles = {"USUARIO_ADMIN"})
        void listarTodosEquipamentos_ComoAdmin_DeveRetornarLista() throws Exception {
            when(equipamentoAppService.listarTodosEquipamentos()).thenReturn(List.of(equipamentoRespostaDTO));
            mockMvc.perform(get("/api/equipamentos")).andExpect(status().isOk());
        }


        @Test
        @DisplayName("Deve retornar 401 Unauthorized se não autenticado ao listar")
        void listarTodosEquipamentos_NaoAutenticado_DeveRetornarUnauthorized() throws Exception {
            mockMvc.perform(get("/api/equipamentos"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Testes para Buscar Equipamento por ID (GET /api/equipamentos/{id})")
    class BuscarEquipamentoPorIdTests {
        @Test
        @DisplayName("Deve retornar equipamento para USUARIO_PADRAO se ID existe")
        @WithMockUser(username = "padrao@test.com", roles = {"USUARIO_PADRAO"})
        void buscarEquipamentoPorId_ComoPadrao_DeveRetornarEquipamento() throws Exception {
            when(equipamentoAppService.buscarEquipamentoPorId(equipamentoIdExistente)).thenReturn(Optional.of(equipamentoRespostaDTO));

            mockMvc.perform(get("/api/equipamentos/{id}", equipamentoIdExistente))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(equipamentoIdExistente)));
        }

        @Test
        @DisplayName("Deve retornar 404 Not Found se ID não existe para USUARIO_PADRAO")
        @WithMockUser(username = "padrao@test.com", roles = {"USUARIO_PADRAO"})
        void buscarEquipamentoPorId_NaoExistenteComoPadrao_DeveRetornarNotFound() throws Exception {
            when(equipamentoAppService.buscarEquipamentoPorId("idInexistente")).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/equipamentos/{id}", "idInexistente"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Deve retornar 401 Unauthorized se não autenticado ao buscar por ID")
        void buscarEquipamentoPorId_NaoAutenticado_DeveRetornarUnauthorized() throws Exception {
            mockMvc.perform(get("/api/equipamentos/{id}", equipamentoIdExistente))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Testes para Buscar Equipamento por Número de Série (GET /api/equipamentos/serial/{numeroDeSerie})")
    class BuscarEquipamentoPorNumeroDeSerieTests {
        @Test
        @DisplayName("Deve retornar equipamento para USUARIO_PADRAO se serial existe")
        @WithMockUser(username = "padrao@test.com", roles = {"USUARIO_PADRAO"})
        void buscarPorSerial_ComoPadrao_Existente_DeveRetornarEquipamento() throws Exception {
            when(equipamentoAppService.buscarEquipamentoPorNumeroDeSerie(numeroDeSerieExistente))
                    .thenReturn(Optional.of(equipamentoRespostaDTO));

            mockMvc.perform(get("/api/equipamentos/serial/{numeroDeSerie}", numeroDeSerieExistente))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.numeroDeSerie", is(numeroDeSerieExistente)));
        }

        @Test
        @DisplayName("Deve retornar 404 Not Found se serial não existe para USUARIO_PADRAO")
        @WithMockUser(username = "padrao@test.com", roles = {"USUARIO_PADRAO"})
        void buscarPorSerial_NaoExistenteComoPadrao_DeveRetornarNotFound() throws Exception {
            when(equipamentoAppService.buscarEquipamentoPorNumeroDeSerie("SN-INEXISTENTE"))
                    .thenReturn(Optional.empty());

            mockMvc.perform(get("/api/equipamentos/serial/{numeroDeSerie}", "SN-INEXISTENTE"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Deve retornar 401 Unauthorized se não autenticado ao buscar por serial")
        void buscarPorSerial_NaoAutenticado_DeveRetornarUnauthorized() throws Exception {
            mockMvc.perform(get("/api/equipamentos/serial/{numeroDeSerie}", numeroDeSerieExistente))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Testes para Atualizar Equipamento (PUT /api/equipamentos/{id})")
    class AtualizarEquipamentoTests {
        @Test
        @DisplayName("Deve atualizar com sucesso para ADMIN")
        @WithMockUser(username = "admin@test.com", roles = {"USUARIO_ADMIN"})
        void atualizarEquipamento_ComoAdmin_DeveRetornarOk() throws Exception {
            EquipamentoRespostaDTO dtoAtualizado = new EquipamentoRespostaDTO(
                    equipamentoIdExistente, "Impressora Laser XL", numeroDeSerieExistente, "Nova descrição P&B",
                    LocalDateTime.now(), LocalDateTime.now(), "user@test.com", "admin@test.com", Collections.emptyList()
            );
            when(equipamentoAppService.atualizarEquipamento(eq(equipamentoIdExistente), any(EquipamentoAtualizacaoDTO.class)))
                    .thenReturn(dtoAtualizado);

            mockMvc.perform(put("/api/equipamentos/{id}", equipamentoIdExistente)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(equipamentoAtualizacaoDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nome", is("Impressora Laser XL")))
                    .andExpect(jsonPath("$.modificadoPorUsuario", is("admin@test.com")));
        }

        @Test
        @DisplayName("Deve atualizar com sucesso para MASTER")
        @WithMockUser(username = "master@test.com", roles = {"USUARIO_MASTER"})
        void atualizarEquipamento_ComoMaster_DeveRetornarOk() throws Exception {
            EquipamentoRespostaDTO dtoAtualizado = new EquipamentoRespostaDTO(
                    equipamentoIdExistente, "Impressora Laser MasterEdit", numeroDeSerieExistente, "Editado por Master",
                    LocalDateTime.now(), LocalDateTime.now(), "user@test.com", "master@test.com", Collections.emptyList()
            );
            when(equipamentoAppService.atualizarEquipamento(eq(equipamentoIdExistente), any(EquipamentoAtualizacaoDTO.class)))
                    .thenReturn(dtoAtualizado);

            mockMvc.perform(put("/api/equipamentos/{id}", equipamentoIdExistente)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new EquipamentoAtualizacaoDTO("Impressora Laser MasterEdit", "Editado por Master"))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nome", is("Impressora Laser MasterEdit")));
        }

        @Test
        @DisplayName("Deve retornar 403 Forbidden para USUARIO_PADRAO ao tentar atualizar")
        @WithMockUser(username = "padrao@test.com", roles = {"USUARIO_PADRAO"})
        void atualizarEquipamento_ComoPadrao_DeveRetornarForbidden() throws Exception {
            mockMvc.perform(put("/api/equipamentos/{id}", equipamentoIdExistente)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(equipamentoAtualizacaoDTO)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Deve retornar 404 Not Found se equipamento não existe ao atualizar")
        @WithMockUser(username = "admin@test.com", roles = {"USUARIO_ADMIN"})
        void atualizarEquipamento_IdNaoExistente_DeveRetornarNotFound() throws Exception {
            when(equipamentoAppService.atualizarEquipamento(eq("idNaoExiste"), any(EquipamentoAtualizacaoDTO.class)))
                    .thenThrow(new RecursoNaoEncontradoException("Equipamento com ID idNaoExiste não encontrado."));

            mockMvc.perform(put("/api/equipamentos/{id}", "idNaoExiste")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(equipamentoAtualizacaoDTO)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.detail", is("Equipamento com ID idNaoExiste não encontrado.")));
        }

        @Test
        @DisplayName("Deve retornar 400 Bad Request para DTO de atualização inválido")
        @WithMockUser(username = "admin@test.com", roles = {"USUARIO_ADMIN"})
        void atualizarEquipamento_ComDTOInvalido_DeveRetornarBadRequest() throws Exception {
            // Supondo que @Size(min=2) para nome. "N" é inválido.
            EquipamentoAtualizacaoDTO dtoInvalido = new EquipamentoAtualizacaoDTO("N", "");

            mockMvc.perform(put("/api/equipamentos/{id}", equipamentoIdExistente)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dtoInvalido)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Testes para Deletar Equipamento (DELETE /api/equipamentos/{id})")
    class DeletarEquipamentoTests {
        @Test
        @DisplayName("Deve deletar com sucesso para ADMIN")
        @WithMockUser(username = "admin@test.com", roles = {"USUARIO_ADMIN"})
        void deletarEquipamento_ComoAdmin_DeveRetornarNoContent() throws Exception {
            doNothing().when(equipamentoAppService).deletarEquipamento(equipamentoIdExistente);

            mockMvc.perform(delete("/api/equipamentos/{id}", equipamentoIdExistente))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Deve deletar com sucesso para MASTER")
        @WithMockUser(username = "master@test.com", roles = {"USUARIO_MASTER"})
        void deletarEquipamento_ComoMaster_DeveRetornarNoContent() throws Exception {
            doNothing().when(equipamentoAppService).deletarEquipamento(equipamentoIdExistente);

            mockMvc.perform(delete("/api/equipamentos/{id}", equipamentoIdExistente))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Deve retornar 403 Forbidden para USUARIO_PADRAO ao tentar deletar")
        @WithMockUser(username = "padrao@test.com", roles = {"USUARIO_PADRAO"})
        void deletarEquipamento_ComoPadrao_DeveRetornarForbidden() throws Exception {
            mockMvc.perform(delete("/api/equipamentos/{id}", equipamentoIdExistente))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Deve retornar 404 Not Found se equipamento não existe ao deletar")
        @WithMockUser(username = "admin@test.com", roles = {"USUARIO_ADMIN"})
        void deletarEquipamento_IdNaoExistente_DeveRetornarNotFound() throws Exception {
            doThrow(new RecursoNaoEncontradoException("Equipamento com ID idNaoExiste não encontrado para deleção."))
                    .when(equipamentoAppService).deletarEquipamento("idNaoExiste");

            mockMvc.perform(delete("/api/equipamentos/{id}", "idNaoExiste"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.detail", is("Equipamento com ID idNaoExiste não encontrado para deleção.")));
        }
    }
}