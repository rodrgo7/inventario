package oliveiradev.inventario.interfaces.controller;

import oliveiradev.inventario.application.dto.usuario.UsuarioRespostaDTO;
import oliveiradev.inventario.application.exception.RecursoNaoEncontradoException;
import oliveiradev.inventario.application.exception.RegraDeNegocioException;
import oliveiradev.inventario.application.service.UsuarioAppService;
import oliveiradev.inventario.domain.model.NivelUsuario;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/admin/usuarios")
public class AdminUsuarioController {
    private static final Logger logger = LoggerFactory.getLogger(AdminUsuarioController.class);

    private final UsuarioAppService usuarioAppService;

    @Autowired
    public AdminUsuarioController(UsuarioAppService usuarioAppService) {
        this.usuarioAppService = usuarioAppService;
    }

    // Endpoint para UsuarioMaster atualizar os níveis de um usuário específico
    @PutMapping("/{usuarioId}/niveis")
    @PreAuthorize("hasRole('USUARIO_MASTER')") // Apenas USUARIO_MASTER pode acessar
    public ResponseEntity<?> atualizarNiveisDeUsuario(
            @PathVariable String usuarioId,
            @Valid @RequestBody Set<NivelUsuario> novosNiveis) { // Recebe os novos níveis no corpo da requisição

        logger.info("Requisição para atualizar níveis do usuário ID: {} para {}", usuarioId, novosNiveis);

        if (novosNiveis == null || novosNiveis.isEmpty()) {
            logger.warn("Tentativa de definir um conjunto vazio de níveis para o usuário ID: {}", usuarioId);
        }

        try {
            UsuarioRespostaDTO usuarioAtualizado = usuarioAppService.atualizarNiveisUsuario(usuarioId, novosNiveis);
            logger.info("Níveis do usuário ID: {} atualizados com sucesso para {}", usuarioId, novosNiveis);
            return ResponseEntity.ok(usuarioAtualizado);
        } catch (RecursoNaoEncontradoException e) {
            logger.warn("Tentativa de atualizar níveis de usuário não encontrado. ID: {}, Erro: {}", usuarioId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RegraDeNegocioException | IllegalStateException e) {
            logger.warn("Erro de regra de negócio ao atualizar níveis do usuário ID: {}. Erro: {}", usuarioId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Erro inesperado ao atualizar níveis do usuário ID: {}", usuarioId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno ao atualizar níveis do usuário.");
        }
    }
}