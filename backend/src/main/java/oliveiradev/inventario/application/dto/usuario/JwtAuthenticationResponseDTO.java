package oliveiradev.inventario.application.dto.usuario;

public record JwtAuthenticationResponseDTO(
        String token,
        String tipoToken) {
    public JwtAuthenticationResponseDTO(String token) {
        this(token, "Bearer");
    }
}
