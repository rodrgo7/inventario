package oliveiradev.inventario.application.dto.usuario;

import oliveiradev.inventario.domain.model.NivelUsuario;

import java.time.LocalDateTime;
import java.util.Set;

public record UsuarioRespostaDTO(
        String id,
        String nome,
        String email,
        Set<NivelUsuario> niveis,
        LocalDateTime dataCriacao,
        LocalDateTime dataModificacao)
    {
}