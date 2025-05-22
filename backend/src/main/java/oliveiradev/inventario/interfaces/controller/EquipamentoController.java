package oliveiradev.inventario.interfaces.controller;

import oliveiradev.inventario.application.dto.equipamento.EquipamentoAtualizacaoDTO;
import oliveiradev.inventario.application.dto.equipamento.EquipamentoCriacaoDTO;
import oliveiradev.inventario.application.dto.equipamento.EquipamentoRespostaDTO;
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
import java.util.Optional;

@RestController
@RequestMapping("/api/equipamentos")
public class EquipamentoController {
    private static final Logger logger = LoggerFactory.getLogger(EquipamentoController.class);

    private final EquipamentoAppService equipamentoAppService;

    @Autowired
    public EquipamentoController(EquipamentoAppService equipamentoAppService) {
        this.equipamentoAppService = equipamentoAppService;
    }

    @PostMapping
    @PreAuthorize("hasRole('USUARIO_ADMIN') or hasRole('USUARIO_MASTER')")
    public ResponseEntity<EquipamentoRespostaDTO> criarEquipamento(@Valid @RequestBody EquipamentoCriacaoDTO equipamentoCriacaoDTO) {
        logger.info("Requisição para criar novo equipamento: {}", equipamentoCriacaoDTO.nome());
        EquipamentoRespostaDTO novoEquipamento = equipamentoAppService.criarEquipamento(equipamentoCriacaoDTO);
        logger.info("Equipamento {} criado com sucesso com ID: {}", novoEquipamento.nome(), novoEquipamento.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(novoEquipamento);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<EquipamentoRespostaDTO>> listarTodosEquipamentos() {
        logger.info("Requisição para listar todos os equipamentos");
        List<EquipamentoRespostaDTO> equipamentos = equipamentoAppService.listarTodosEquipamentos();
        return ResponseEntity.ok(equipamentos);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EquipamentoRespostaDTO> buscarEquipamentoPorId(@PathVariable String id) {
        logger.info("Requisição para buscar equipamento por ID: {}", id);
        Optional<EquipamentoRespostaDTO> dtoOptional = equipamentoAppService.buscarEquipamentoPorId(id);
        return dtoOptional
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    logger.warn("Equipamento com ID: {} não encontrado.", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping("/serial/{numeroDeSerie}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EquipamentoRespostaDTO> buscarEquipamentoPorNumeroDeSerie(@PathVariable String numeroDeSerie) {
        logger.info("Requisição para buscar equipamento por Número de Série: {}", numeroDeSerie);
        Optional<EquipamentoRespostaDTO> dtoOptional = equipamentoAppService.buscarEquipamentoPorNumeroDeSerie(numeroDeSerie);
        return dtoOptional
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    logger.warn("Equipamento com Número de Série: {} não encontrado.", numeroDeSerie);
                    return ResponseEntity.notFound().build();
                });
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USUARIO_ADMIN') or hasRole('USUARIO_MASTER')")
    public ResponseEntity<EquipamentoRespostaDTO> atualizarEquipamento(@PathVariable String id,
                                                                       @Valid @RequestBody EquipamentoAtualizacaoDTO equipamentoAtualizacaoDTO) {
        logger.info("Requisição para atualizar equipamento ID: {}", id);
        EquipamentoRespostaDTO equipamentoAtualizado = equipamentoAppService.atualizarEquipamento(id, equipamentoAtualizacaoDTO);
        logger.info("Equipamento ID: {} atualizado com sucesso.", id);
        return ResponseEntity.ok(equipamentoAtualizado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USUARIO_ADMIN') or hasRole('USUARIO_MASTER')")
    public ResponseEntity<Void> deletarEquipamento(@PathVariable String id) {
        logger.info("Requisição para deletar equipamento ID: {}", id);
        equipamentoAppService.deletarEquipamento(id); // Serviço lançará RecursoNaoEncontradoException se não achar
        logger.info("Equipamento ID: {} deletado com sucesso.", id);
        return ResponseEntity.noContent().build();
    }
}