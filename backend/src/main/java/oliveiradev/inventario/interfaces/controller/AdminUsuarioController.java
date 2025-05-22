package oliveiradev.inventario.interfaces.controller;

import oliveiradev.inventario.application.dto.usuario.UsuarioRespostaDTO;
import oliveiradev.inventario.application.service.UsuarioAppService;
import oliveiradev.inventario.domain.model.enums.NivelUsuario;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PutMapping("/{usuarioId}/niveis")
    @PreAuthorize("hasRole('USUARIO_MASTER')")
    public ResponseEntity<UsuarioRespostaDTO> atualizarNiveisDeUsuario(
            @PathVariable String usuarioId,
            @RequestBody Set<NivelUsuario> novosNiveis) {

        logger.info("Requisição para atualizar níveis do usuário ID: {} para {}", usuarioId, novosNiveis);

        if (novosNiveis == null || novosNiveis.isEmpty()) {
            throw new IllegalArgumentException("O conjunto de novos níveis não pode ser vazio ou nulo.");
        }

        UsuarioRespostaDTO usuarioAtualizado = usuarioAppService.atualizarNiveisUsuario(usuarioId, novosNiveis);
        logger.info("Níveis do usuário ID: {} atualizados com sucesso para {}", usuarioId, novosNiveis);
        return ResponseEntity.ok(usuarioAtualizado);
    }
}