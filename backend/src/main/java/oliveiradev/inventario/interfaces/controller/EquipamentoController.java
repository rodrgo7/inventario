package oliveiradev.inventario.interfaces.controller;

import oliveiradev.inventario.application.dto.equipamento.EquipamentoAtualizacaoDTO;
import oliveiradev.inventario.application.dto.equipamento.EquipamentoCriacaoDTO;
import oliveiradev.inventario.application.dto.equipamento.EquipamentoRespostaDTO;
import oliveiradev.inventario.application.exception.RecursoNaoEncontradoException;
import oliveiradev.inventario.application.exception.RegraDeNegocioException;
import oliveiradev.inventario.application.service.EquipamentoAppService;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipamentos") // Path base para os endpoints de equipamentos
public class EquipamentoController {
    private static final Logger logger = LoggerFactory.getLogger(EquipamentoController.class);

    private final EquipamentoAppService equipamentoAppService;

    @Autowired
    public EquipamentoController(EquipamentoAppService equipamentoAppService) {
        this.equipamentoAppService = equipamentoAppService;
    }

    // Endpoint para criar um novo equipamento (Apenas Admin ou Master)
    @PostMapping
    @PreAuthorize("hasRole('USUARIO_ADMIN') or hasRole('USUARIO_MASTER')")
    public ResponseEntity<?> criarEquipamento(@Valid @RequestBody EquipamentoCriacaoDTO equipamentoCriacaoDTO) {
        logger.info("Requisição para criar novo equipamento: {}", equipamentoCriacaoDTO.nome());
        try {
            EquipamentoRespostaDTO novoEquipamento = equipamentoAppService.criarEquipamento(equipamentoCriacaoDTO);
            logger.info("Equipamento {} criado com sucesso com ID: {}", novoEquipamento.nome(), novoEquipamento.id());
            return ResponseEntity.status(HttpStatus.CREATED).body(novoEquipamento);
        } catch (RegraDeNegocioException e) {
            logger.warn("Falha ao criar equipamento {}: {}", equipamentoCriacaoDTO.nome(), e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage()); // Ex: Número de série duplicado
        } catch (Exception e) {
            logger.error("Erro inesperado ao criar equipamento {}: ", equipamentoCriacaoDTO.nome(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno ao criar equipamento.");
        }
    }

    // Endpoint para listar todos os equipamentos (Acessível por Padrão, Admin e Master)
    @GetMapping
    @PreAuthorize("isAuthenticated()") // Todos os usuários autenticados podem listar
    public ResponseEntity<List<EquipamentoRespostaDTO>> listarTodosEquipamentos() {
        logger.info("Requisição para listar todos os equipamentos");
        List<EquipamentoRespostaDTO> equipamentos = equipamentoAppService.listarTodosEquipamentos();
        return ResponseEntity.ok(equipamentos);
    }

    // Endpoint para buscar um equipamento por ID (Acessível por Padrão, Admin e Master)
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()") // Todos os usuários autenticados podem buscar por ID
    public ResponseEntity<?> buscarEquipamentoPorId(@PathVariable String id) {
        logger.info("Requisição para buscar equipamento por ID: {}", id);
        return equipamentoAppService.buscarEquipamentoPorId(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> {
                    logger.warn("Equipamento com ID: {} não encontrado.", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Equipamento não encontrado com ID: " + id);
                });
    }

    // Endpoint para buscar um equipamento por Número de Série (Acessível por Padrão, Admin e Master)
    @GetMapping("/serial/{numeroDeSerie}")
    @PreAuthorize("isAuthenticated()") // Todos os usuários autenticados podem buscar por serial
    public ResponseEntity<?> buscarEquipamentoPorNumeroDeSerie(@PathVariable String numeroDeSerie) {
        logger.info("Requisição para buscar equipamento por Número de Série: {}", numeroDeSerie);
        return equipamentoAppService.buscarEquipamentoPorNumeroDeSerie(numeroDeSerie)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> {
                    logger.warn("Equipamento com Número de Série: {} não encontrado.", numeroDeSerie);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Equipamento não encontrado com Número de Série: " + numeroDeSerie);
                });
    }


    // Endpoint para atualizar um equipamento (Apenas Admin ou Master)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USUARIO_ADMIN') or hasRole('USUARIO_MASTER')")
    public ResponseEntity<?> atualizarEquipamento(@PathVariable String id,
                                                  @Valid @RequestBody EquipamentoAtualizacaoDTO equipamentoAtualizacaoDTO) {
        logger.info("Requisição para atualizar equipamento ID: {}", id);
        try {
            EquipamentoRespostaDTO equipamentoAtualizado = equipamentoAppService.atualizarEquipamento(id, equipamentoAtualizacaoDTO);
            logger.info("Equipamento ID: {} atualizado com sucesso.", id);
            return ResponseEntity.ok(equipamentoAtualizado);
        } catch (RecursoNaoEncontradoException e) {
            logger.warn("Falha ao atualizar equipamento. ID: {} não encontrado.", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RegraDeNegocioException e) {
            logger.warn("Falha ao atualizar equipamento ID: {}. Erro de regra de negócio: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Erro inesperado ao atualizar equipamento ID {}: ", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno ao atualizar equipamento.");
        }
    }

    // Endpoint para deletar um equipamento (Apenas Admin ou Master)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USUARIO_ADMIN') or hasRole('USUARIO_MASTER')")
    public ResponseEntity<Void> deletarEquipamento(@PathVariable String id) {
        logger.info("Requisição para deletar equipamento ID: {}", id);
        try {
            equipamentoAppService.deletarEquipamento(id);
            logger.info("Equipamento ID: {} deletado com sucesso.", id);
            return ResponseEntity.noContent().build(); // Retorna 204 No Content
        } catch (RecursoNaoEncontradoException e) {
            logger.warn("Falha ao deletar equipamento. ID: {} não encontrado.", id);
            return ResponseEntity.notFound().build(); // Retorna 404 Not Found
        } catch (Exception e) {
            logger.error("Erro inesperado ao deletar equipamento ID {}: ", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}