package oliveiradev.inventario.application.dto;

import oliveiradev.inventario.domain.model.NivelUsuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record UsuarioCriacaoDTO(
        @NotBlank(message = "O nome não pode estar em branco.")
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres.")
        String nome,

        @NotBlank(message = "O email não pode estar em branco.")
        @Email(message = "O email deve ser válido.")
        @Size(max = 100, message = "O email não pode exceder 100 caracteres.")
        String email,

        @NotBlank(message = "A senha não pode estar em branco.")
        @Size(min = 6, max = 100, message = "A senha deve ter entre 6 e 100 caracteres.")
        String senha,

        Set<NivelUsuario> niveis) {
}