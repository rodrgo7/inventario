package oliveiradev.inventario.domain.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field; // Importado para uso opcional

import java.time.LocalDateTime;
import java.util.Collections; // Para coleções imutáveis
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Document(collection = "usuarios")
public class Usuario {
    @Id
    private String id;

    private String nome;

    @Indexed(unique = true)
    private String email;

    private String senha;

    private Set<NivelUsuario> niveis;

    @CreatedDate
    private LocalDateTime dataCriacao;

    @LastModifiedDate
    private LocalDateTime dataModificacao;

    public Usuario() {
        this.niveis = new HashSet<>();
    }

    public Usuario(String nome, String email, String senha) {
        Objects.requireNonNull(nome, "O nome não pode ser nulo.");
        Objects.requireNonNull(email, "O email não pode ser nulo.");
        Objects.requireNonNull(senha, "A senha não pode ser nula.");

        if (nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome não pode ser vazio.");
        }
        if (email.trim().isEmpty() || !email.contains("@")) {
            throw new IllegalArgumentException("Formato de email inválido.");
        }

        this.nome = nome.trim();
        this.email = email.trim().toLowerCase(); // Padroniza para minúsculas para evitar duplicidade
        this.senha = senha;
        this.niveis = new HashSet<>();
    }

    // --- Getters ---
    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public Set<NivelUsuario> getNiveis() {
        return Collections.unmodifiableSet(new HashSet<>(this.niveis));
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public LocalDateTime getDataModificacao() {
        return dataModificacao;
    }

    public void alterarNome(String novoNome) {
        Objects.requireNonNull(novoNome, "O novo nome não pode ser nulo.");
        if (novoNome.trim().isEmpty()) {
            throw new IllegalArgumentException("O novo nome não pode ser vazio.");
        }
        this.nome = novoNome.trim();
    }

    public void alterarEmail(String novoEmail) {
        Objects.requireNonNull(novoEmail, "O novo email não pode ser nulo.");
        if (novoEmail.trim().isEmpty() || !novoEmail.contains("@")) {
            throw new IllegalArgumentException("Formato de email inválido para alteração.");
        }
        this.email = novoEmail.trim().toLowerCase();
    }

    public void definirSenha(String senhaHashed) {
        Objects.requireNonNull(senhaHashed, "A senha hashed não pode ser nula.");
        if (senhaHashed.trim().isEmpty()) {
            throw new IllegalArgumentException("A senha hashed não pode ser vazia.");
        }
        this.senha = senhaHashed;
    }

    public void adicionarNivel(NivelUsuario nivel) {
        Objects.requireNonNull(nivel, "Nível a ser adicionado não pode ser nulo.");
        if (NivelUsuario.USUARIO_MASTER.equals(nivel)) {
            this.niveis.clear(); // Limpa outros níveis se está se tornando MASTER
        } else if (this.niveis.contains(NivelUsuario.USUARIO_MASTER)) {
            throw new IllegalStateException("Usuário MASTER não pode ter outros níveis adicionados.");
        }
        this.niveis.add(nivel);
    }

    public void removerNivel(NivelUsuario nivel) {
        Objects.requireNonNull(nivel, "Nível a ser removido não pode ser nulo.");
        this.niveis.remove(nivel);
    }

    public void definirNiveis(Set<NivelUsuario> novosNiveis) {
        Objects.requireNonNull(novosNiveis, "O conjunto de níveis não pode ser nulo.");
        this.niveis.clear();
        for (NivelUsuario nivel : novosNiveis) {
            adicionarNivel(nivel); // Reutiliza a lógica de adicionarNivel com suas invariantes
        }
    }

    public boolean possuiNivel(NivelUsuario nivel) {
        Objects.requireNonNull(nivel, "Nível a ser verificado não pode ser nulo.");
        return this.niveis.contains(nivel);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        if (id != null) {
            return id.equals(usuario.id);
        }
        return email.equals(usuario.email);
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return Objects.hash(id);
        }
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id='" + id + '\'' +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", niveis=" + niveis +
                ", dataCriacao=" + dataCriacao +
                ", dataModificacao=" + dataModificacao +
                '}';
    }
}