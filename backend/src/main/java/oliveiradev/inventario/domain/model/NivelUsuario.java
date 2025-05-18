package oliveiradev.inventario.domain.model;

public enum NivelUsuario {
    USUARIO_PADRAO("Usuario Padrão"),
    USUARIO_ADMIN("Administrador"),
    USUARIO_MASTER("Master");

    private final String descricao;
    NivelUsuario(String descricao) {
        this.descricao = descricao;
    }

    public  String getDescricao() {
        return descricao;
    }

    public String getRoleName() {

        return roleName;
    }

    public static NivelUsuario fromRoleNameSpring(String roleNameSpring) {
        for (NivelUsuario nivel : NivelUsuario.values()) {
            if (nivel.name().equalsIgnoreCase(roleNameSpring)) {
                return nivel;
            }
        }
        throw new IllegalArgumentException("Nivel de usuário Spring Security desconhecido: " + roleNameSpring);
    }

    public static NivelUsuario fromDescricao(String descricao) {
        for (NivelUsuario nivel : NivelUsuario.values()) {
            if (nivel.getDescricao().equalsIgnoreCase(descricao)) {
                return nivel;
            }
        }
        throw new IllegalArgumentException("Descrição de nivel de usuário desconhecido: " + descricao);
    }
}