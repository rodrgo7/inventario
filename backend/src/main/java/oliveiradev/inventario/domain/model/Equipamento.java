package oliveiradev.inventario.domain.model;

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

    @Indexed(unique = true)
    private String nome;

    @Indexed(unique = true)
    private String numeroDeSerie;

    private String descricaoDetalhada;

    @CreatedDate
    private LocalDateTime dataInclusaoNoSistema;

    @LastModifiedDate
    private LocalDateTime dataUltimaModificacao;

    @CreatedBy
    private String criadoPorUsuario;

    @LastModifiedBy
    private String modificadoPorUsuario;

    // Lista para armazenar os logs de alterações específicos do equipamento
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

    // Método para adicionar uma entrada de log
    public void adicionarLog(String usuarioResponsavel, String descricaoDaAlteracao) {
        if (this.logs == null) {
            this.logs = new ArrayList<>();
        }
        this.logs.add(new LogAlteracao(LocalDateTime.now(), usuarioResponsavel, descricaoDaAlteracao));
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
        // Retorna uma cópia imutável para proteger a lista interna
        return Collections.unmodifiableList(new ArrayList<>(this.logs != null ? this.logs : Collections.emptyList()));
    }

    public void alterarNome(String novoNome, String usuarioQueAlterou) {
        Objects.requireNonNull(novoNome, "Novo nome não pode ser nulo.");
        if (novoNome.trim().isEmpty()) {
            throw new IllegalArgumentException("Novo nome não pode ser vazio.");
        }
        if (!this.nome.equals(novoNome.trim())) {
            adicionarLog(usuarioQueAlterou, "Nome alterado de '" + this.nome + "' para '" + novoNome.trim() + "'.");
            this.nome = novoNome.trim();
        }
    }

    public void alterarNumeroDeSerie(String novoNumeroDeSerie, String usuarioQueAlterou) {
        Objects.requireNonNull(novoNumeroDeSerie, "Novo número de série não pode ser nulo.");
        if (novoNumeroDeSerie.trim().isEmpty()) {
            throw new IllegalArgumentException("Novo número de série não pode ser vazio.");
        }
        // A unicidade do novo número de série deve ser verificada no serviço de aplicação
        if (!this.numeroDeSerie.equals(novoNumeroDeSerie.trim())) {
            adicionarLog(usuarioQueAlterou, "Número de série alterado de '" + this.numeroDeSerie + "' para '" + novoNumeroDeSerie.trim() + "'.");
            this.numeroDeSerie = novoNumeroDeSerie.trim();
        }
    }

    public void alterarDescricaoDetalhada(String novaDescricao, String usuarioQueAlterou) {
        String novaDescricaoTrimmed = (novaDescricao != null) ? novaDescricao.trim() : null;
        if (!Objects.equals(this.descricaoDetalhada, novaDescricaoTrimmed)) {
            adicionarLog(usuarioQueAlterou, "Descrição detalhada alterada."); // Pode logar o antes/depois se necessário
            this.descricaoDetalhada = novaDescricaoTrimmed;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Equipamento that = (Equipamento) o;
        // Se o ID existe, é a melhor forma de verificar igualdade.
        // Caso contrário (objeto não persistido), o número de série (que deve ser único) é um bom candidato.
        if (id != null) {
            return Objects.equals(id, that.id);
        }
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