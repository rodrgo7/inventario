package oliveiradev.inventario.domain.model.equipamentos;

import java.time.LocalDateTime;
import java.util.Objects;

public class LogAlteracao {
    private LocalDateTime dataHora;
    private String usuarioResponsavel;
    private String descricao;

    public LogAlteracao() { }

    public LogAlteracao(LocalDateTime dataHora, String usuarioResponsavel, String descricao) {
        this.dataHora = Objects.requireNonNull(dataHora, "Data/Hora não pode ser nula.");
        this.usuarioResponsavel = Objects.requireNonNull(usuarioResponsavel, "Usuário responsável não pode ser nulo.");
        this.descricao = Objects.requireNonNull(descricao, "Descrição não pode ser nula.");
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public String getUsuarioResponsavel() {
        return usuarioResponsavel;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public void setUsuarioResponsavel(String usuarioResponsavel) {
        this.usuarioResponsavel = usuarioResponsavel;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogAlteracao that = (LogAlteracao) o;
        return Objects.equals(dataHora, that.dataHora) &&
                Objects.equals(usuarioResponsavel, that.usuarioResponsavel) &&
                Objects.equals(descricao, that.descricao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataHora, usuarioResponsavel, descricao);
    }

    @Override
    public String toString() {
        return "LogAlteracao{" +
                "dataHora=" + dataHora +
                ", usuarioResponsavel='" + usuarioResponsavel + '\'' +
                ", descricao='" + descricao + '\'' +
                '}';
    }
}