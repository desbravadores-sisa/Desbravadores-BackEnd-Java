package school.sptech.api_tasks.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class TarefaCreateDto {

    @NotNull
    private Integer fkClube;

    @NotNull
    private Integer fkUnidade;

    @NotBlank
    private String nome;

    private String descricao;

    private Integer pontuacao;

    private LocalDateTime prazoEntrega;

    public TarefaCreateDto() {
    }

    public Integer getFkClube() {
        return fkClube;
    }

    public void setFkClube(Integer fkClube) {
        this.fkClube = fkClube;
    }

    public Integer getFkUnidade() {
        return fkUnidade;
    }

    public void setFkUnidade(Integer fkUnidade) {
        this.fkUnidade = fkUnidade;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(Integer pontuacao) {
        this.pontuacao = pontuacao;
    }

    public LocalDateTime getPrazoEntrega() {
        return prazoEntrega;
    }

    public void setPrazoEntrega(LocalDateTime prazoEntrega) {
        this.prazoEntrega = prazoEntrega;
    }
}
