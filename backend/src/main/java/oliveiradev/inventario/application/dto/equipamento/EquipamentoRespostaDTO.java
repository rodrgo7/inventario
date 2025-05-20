package oliveiradev.inventario.application.dto.equipamento;

import java.time.LocalDateTime;
import java.util.List;

public record EquipamentoRespostaDTO(
        String id,
        String nome,
        String numeroDeSerie,
        String descricaoDetalhada,
        LocalDateTime dataInclusaoNoSistema,
        LocalDateTime dataUltimaModificacao,
        String criadoPorUsuario,
        String modificadoPorUsuario,
        List<LogAlteracaoDTO> logs) {
}