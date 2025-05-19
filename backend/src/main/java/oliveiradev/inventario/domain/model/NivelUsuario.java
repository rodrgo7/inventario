package oliveiradev.inventario.domain.model;

public enum NivelUsuario {
    USUARIO_PADRAO("Usuario Padrão"),
    USUARIO_ADMIN("Administrador"),
    USUARIO_MASTER("Master");

    private final String roleName;  // "Usuario Padrão"

    NivelUsuario(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    public static NivelUsuario fromRoleName(String roleName) {
        for (NivelUsuario nivel : NivelUsuario.values()) {
            if (nivel.getRoleName().equalsIgnoreCase(roleName)) {
                return nivel;
            }
        }
        throw new IllegalArgumentException("Nível de usuário desconhecido: " + roleName);
    }
}