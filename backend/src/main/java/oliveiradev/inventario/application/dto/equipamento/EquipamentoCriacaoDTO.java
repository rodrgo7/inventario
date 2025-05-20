package oliveiradev.inventario.application.dto.equipamento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EquipamentoCriacaoDTO(
        @NotBlank(message = "O nome não pode estar em branco.")
        @Size(min = 3, max = 150, message = "O nome deve ter entre 3 e 150 caracteres.")
        String nome,

        @NotBlank(message = "O número de série não pode estar em branco.")
        @Size(min = 1, max = 100, message = "O número de série deve ter entre 1 e 100 caracteres.")
        String numeroDeSerie,

        @Size(max = 500, message = "A descrição detalhada não pode exceder 500 caracteres.")
        String descricaoDetalhada) {
}