package oliveiradev.inventario.application.dto.usuario;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank(message = "O email não pode estar em branco.")
        String email,

        @NotBlank(message = "A senha não pode estar em branco.")
        String senha) {
}