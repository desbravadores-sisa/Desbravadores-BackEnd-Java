package school.sptech.APIDesbravadores.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class TarefaCreateDto {

    @NotNull
    @Schema(description = "ID do Clube", example = "1")
    private Integer fkClube;

    @NotNull
    @Schema(description = "ID da Unidade", example = "2")
    private Integer fkUnidade;

    @Schema(description = "ID do Caderno (opcional, para tarefas de Caderno)", example = "1")
    private Integer fkCaderno;

    @NotBlank
    @Schema(description = "Título da tarefa", example = "Comprar materiais")
    private String titulo;

    @Schema(description = "Descrição da tarefa", example = "Comprar materiais para o acampamento")
    private String descricao;

    @NotBlank
    @Schema(description = "Tipo da tarefa: CLUBE ou CADERNO", example = "CLUBE")
    private String tipoTarefa;

    @Schema(description = "Pontuação da tarefa", example = "10")
    private Integer pontuacao;

    @Schema(description = "Prazo padrão da tarefa", example = "2026-04-10")
    private LocalDate prazoPadrao;

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

    public Integer getFkCaderno() {
        return fkCaderno;
    }

    public void setFkCaderno(Integer fkCaderno) {
        this.fkCaderno = fkCaderno;
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

    public String getTipoTarefa() {
        return tipoTarefa;
    }

    public void setTipoTarefa(String tipoTarefa) {
        this.tipoTarefa = tipoTarefa;
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
