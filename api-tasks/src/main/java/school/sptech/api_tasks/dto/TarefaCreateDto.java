package school.sptech.api_tasks.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class TarefaCreateDto {

    @NotNull
    @Schema(description = "ID do Clube", example = "1")
    private Integer fkClube;

    @NotNull
    @Schema(description = "ID da Unidade", example = "2")
    private Integer fkUnidade;

    @NotBlank
    @Schema(description = "Nome da tarefa", example = "Comprar materiais")
    private String nome;

    @Schema(description = "Descrição da tarefa", example = "Comprar materiais para o acampamento")
    private String descricao;

    @Schema(description = "Pontuação da tarefa", example = "10")
    private Integer pontuacao;

    @Schema(description = "Prazo de entrega da tarefa", example = "2026-04-10T23:59:59")
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
