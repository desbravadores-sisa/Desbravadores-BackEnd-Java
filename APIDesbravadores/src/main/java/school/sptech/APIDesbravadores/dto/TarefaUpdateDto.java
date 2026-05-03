package school.sptech.APIDesbravadores.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class TarefaUpdateDto {

    @NotBlank
    @Schema(description = "Nome da tarefa", example = "Comprar materiais atualizado")
    private String nome;

    @Schema(description = "Descrição da tarefa", example = "Nova descrição para a tarefa")
    private String descricao;

    @Schema(description = "Pontuação da tarefa", example = "15")
    private Integer pontuacao;

    @Schema(description = "Prazo de entrega da tarefa", example = "2026-04-15T23:59:59")
    private LocalDateTime prazoEntrega;

    public TarefaUpdateDto() {
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
