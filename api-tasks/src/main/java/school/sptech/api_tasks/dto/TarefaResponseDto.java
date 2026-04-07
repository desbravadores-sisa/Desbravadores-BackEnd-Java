package school.sptech.api_tasks.dto;

import java.time.LocalDateTime;

public class TarefaResponseDto {

    private Integer id;
    private Integer fkClube;
    private Integer fkUnidade;
    private String nome;
    private String descricao;
    private Integer pontuacao;
    private LocalDateTime prazoEntrega;
    private LocalDateTime dataCriacao;
    private String statusKanban;

    public TarefaResponseDto() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public String getStatusKanban() {
        return statusKanban;
    }

    public void setStatusKanban(String statusKanban) {
        this.statusKanban = statusKanban;
    }
}
