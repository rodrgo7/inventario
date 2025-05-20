package oliveiradev.inventario.application.dto.equipamento;

import jakarta.validation.constraints.Size;

public record EquipamentoAtualizacaoDTO(
        @Size(min = 2, max = 150, message = "O nome deve ter entre 2 e 150 caracteres, se fornecido.")
        String nome, // Se nulo, não atualiza o nome

        @Size(max = 500, message = "A descrição detalhada não pode exceder 500 caracteres, se fornecida.")
        String descricaoDetalhada) { // Se nulo, não atualiza a descrição
}
