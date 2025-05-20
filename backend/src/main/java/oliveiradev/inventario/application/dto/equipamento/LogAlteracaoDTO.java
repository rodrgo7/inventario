package oliveiradev.inventario.application.dto.equipamento;

import java.time.LocalDateTime;

public record LogAlteracaoDTO(
        LocalDateTime dataHora,
        String usuarioResponsavel,
        String descricao) {
}