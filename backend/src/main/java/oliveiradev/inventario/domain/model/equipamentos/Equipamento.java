package oliveiradev.inventario.domain.model.equipamentos;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Document(collection = "equipamentos")
public class Equipamento {
    @Id
    private String id;

    @Indexed
    private String nome;

    @Indexed(unique = true) // Número de série DEVE ser único
    private String numeroDeSerie;

    private String descricaoDetalhada;

    // Campos de Auditoria Básica
    @CreatedDate
    private LocalDateTime dataInclusaoNoSistema; // Data de criação do registro no BD

    @LastModifiedDate
    private LocalDateTime dataUltimaModificacao;

    @CreatedBy
    private String criadoPorUsuario; // Email do usuário que criou

    @LastModifiedBy
    private String modificadoPorUsuario; // Email do usuário que modificou pela última vez

    private List<LogAlteracao> logs;

    public Equipamento() {
        this.logs = new ArrayList<>();
    }

    public Equipamento(String nome, String numeroDeSerie, String descricaoDetalhada) {
        Objects.requireNonNull(nome, "Nome não pode ser nulo.");
        Objects.requireNonNull(numeroDeSerie, "Número de série não pode ser nulo.");

        if (nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio.");
        }
        if (numeroDeSerie.trim().isEmpty()) {
            throw new IllegalArgumentException("Número de série não pode ser vazio.");
        }

        this.nome = nome.trim();
        this.numeroDeSerie = numeroDeSerie.trim();
        this.descricaoDetalhada = descricaoDetalhada != null ? descricaoDetalhada.trim() : null;
        this.logs = new ArrayList<>();
    }

    public void adicionarLog(String usuarioResponsavel, String descricaoDaAlteracao) {
        Objects.requireNonNull(usuarioResponsavel, "Usuário responsável pelo log não pode ser nulo.");
        Objects.requireNonNull(descricaoDaAlteracao, "Descrição do log não pode ser nula.");
        if (descricaoDaAlteracao.trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição do log não pode ser vazia.");
        }
        if (this.logs == null) {
            this.logs = new ArrayList<>();
        }
        this.logs.add(new LogAlteracao(LocalDateTime.now(), usuarioResponsavel, descricaoDaAlteracao.trim()));
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getNumeroDeSerie() {
        return numeroDeSerie;
    }

    public String getDescricaoDetalhada() {
        return descricaoDetalhada;
    }

    public LocalDateTime getDataInclusaoNoSistema() {
        return dataInclusaoNoSistema;
    }

    public LocalDateTime getDataUltimaModificacao() {
        return dataUltimaModificacao;
    }

    public String getCriadoPorUsuario() {
        return criadoPorUsuario;
    }

    public String getModificadoPorUsuario() {
        return modificadoPorUsuario;
    }

    public List<LogAlteracao> getLogs() {
        return Collections.unmodifiableList(new ArrayList<>(this.logs != null ? this.logs : Collections.emptyList()));
    }

    public void alterarNome(String novoNome, String usuarioQueAlterou) {
        Objects.requireNonNull(novoNome, "Novo nome não pode ser nulo.");
        Objects.requireNonNull(usuarioQueAlterou, "Usuário que alterou não pode ser nulo.");
        String nomeAntigo = this.nome;
        String novoNomeTrimmed = novoNome.trim();

        if (novoNomeTrimmed.isEmpty()) {
            throw new IllegalArgumentException("Novo nome não pode ser vazio.");
        }
        if (!Objects.equals(nomeAntigo, novoNomeTrimmed)) {
            this.nome = novoNomeTrimmed;
            adicionarLog(usuarioQueAlterou, "Nome alterado de '" + nomeAntigo + "' para '" + this.nome + "'.");
        }
    }

    public void alterarNumeroDeSerie(String novoNumeroDeSerie, String usuarioQueAlterou) {
        Objects.requireNonNull(novoNumeroDeSerie, "Novo número de série não pode ser nulo.");
        Objects.requireNonNull(usuarioQueAlterou, "Usuário que alterou não pode ser nulo.");
        String serialAntigo = this.numeroDeSerie;
        String novoSerialTrimmed = novoNumeroDeSerie.trim();

        if (novoSerialTrimmed.isEmpty()) {
            throw new IllegalArgumentException("Novo número de série não pode ser vazio.");
        }
        // A verificação de unicidade do novo número de série deve ser feita pelo SERVIÇO
        if (!Objects.equals(serialAntigo, novoSerialTrimmed)) {
            this.numeroDeSerie = novoSerialTrimmed;
            adicionarLog(usuarioQueAlterou, "Número de série alterado de '" + serialAntigo + "' para '" + this.numeroDeSerie + "'.");
        }
    }

    public void alterarDescricaoDetalhada(String novaDescricao, String usuarioQueAlterou) {
        Objects.requireNonNull(usuarioQueAlterou, "Usuário que alterou não pode ser nulo.");
        String descricaoAntiga = this.descricaoDetalhada;
        // Permite que a nova descrição seja nula ou uma string vazia para limpar o campo
        String novaDescricaoTratada = (novaDescricao != null) ? novaDescricao.trim() : null;

        if (!Objects.equals(descricaoAntiga, novaDescricaoTratada)) {
            this.descricaoDetalhada = novaDescricaoTratada;
            if (descricaoAntiga == null && novaDescricaoTratada != null && !novaDescricaoTratada.isEmpty()) {
                adicionarLog(usuarioQueAlterou, "Descrição detalhada definida.");
            } else if (descricaoAntiga != null && (novaDescricaoTratada == null || novaDescricaoTratada.isEmpty())) {
                adicionarLog(usuarioQueAlterou, "Descrição detalhada removida.");
            } else {
                adicionarLog(usuarioQueAlterou, "Descrição detalhada alterada.");
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Equipamento that = (Equipamento) o;
        if (id != null) {
            return Objects.equals(id, that.id);
        }
        // Se ambos os IDs são nulos, compara pelo número de série (que deve ser único)
        return Objects.equals(numeroDeSerie, that.numeroDeSerie);
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return Objects.hash(id);
        }
        return Objects.hash(numeroDeSerie);
    }

    @Override
    public String toString() {
        return "Equipamento{" +
                "id='" + id + '\'' +
                ", nome='" + nome + '\'' +
                ", numeroDeSerie='" + numeroDeSerie + '\'' +
                ", descricaoDetalhada='" + descricaoDetalhada + '\'' +
                ", dataInclusaoNoSistema=" + dataInclusaoNoSistema +
                ", dataUltimaModificacao=" + dataUltimaModificacao +
                ", criadoPorUsuario='" + criadoPorUsuario + '\'' +
                ", modificadoPorUsuario='" + modificadoPorUsuario + '\'' +
                ", logs=" + (logs != null ? logs.size() + " entradas" : "nenhuma entrada") +
                '}';
    }
}