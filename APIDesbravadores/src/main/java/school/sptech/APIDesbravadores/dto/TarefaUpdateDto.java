package school.sptech.APIDesbravadores.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class TarefaUpdateDto {

    @NotBlank
    @Size(max = 150)
    @Schema(description = "Título da tarefa", example = "Comprar materiais atualizado")
    private String titulo;

    @Schema(description = "Descrição da tarefa", example = "Nova descrição para a tarefa")
    private String descricao;

    @PositiveOrZero
    @Schema(description = "Pontuação da tarefa", example = "15")
    private Integer pontuacao;

    @Schema(description = "Prazo padrão da tarefa", example = "2026-04-15")
    private LocalDate prazoPadrao;

    public TarefaUpdateDto() {
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
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

    public LocalDate getPrazoPadrao() {
        return prazoPadrao;
    }

    public void setPrazoPadrao(LocalDate prazoPadrao) {
        this.prazoPadrao = prazoPadrao;
    }
}
