package school.sptech.APIDesbravadores.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

public class TarefaResponseDto {

    @Schema(description = "ID da tarefa", example = "1")
    private Integer id;

    @Schema(description = "ID do Clube", example = "1")
    private Integer fkClube;

    @Schema(description = "ID da Unidade", example = "2")
    private Integer fkUnidade;

    @Schema(description = "Nome da tarefa", example = "Comprar materiais")
    private String nome;

    @Schema(description = "Descrição da tarefa", example = "Comprar materiais para o acampamento")
    private String descricao;

    @Schema(description = "Pontuação da tarefa", example = "10")
    private Integer pontuacao;

    @Schema(description = "Prazo de entrega da tarefa", example = "2026-04-10T23:59:59")
    private LocalDateTime prazoEntrega;

    @Schema(description = "Data de criação da tarefa", example = "2026-04-07T10:00:00")
    private LocalDateTime dataCriacao;

    @Schema(description = "Status da tarefa no Kanban", example = "A FAZER")
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
