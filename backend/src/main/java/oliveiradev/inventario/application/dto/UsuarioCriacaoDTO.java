package oliveiradev.inventario.application.dto;

import oliveiradev.inventario.domain.model.NivelUsuario;

import java.util.Set;

public record UsuarioCriacaoDTO(
        String nome,
        String email,
        String senha,
        Set<NivelUsuario> niveis) {

    public UsuarioCriacaoDTO {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome não pode ser vazio.");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Email inválido.");
        }
        if (senha == null || senha.length() < 8) { // Exemplo de regra mínima
            throw new IllegalArgumentException("Senha deve ter pelo menos 8 caracteres.");
        }
    }
}
