package school.sptech.APIDesbravadores.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

public class TarefaResponseDto {

    @Schema(description = "ID da tarefa", example = "1")
    private Integer id;

    @Schema(description = "ID do Clube", example = "1")
    private Integer fkClube;

    @Schema(description = "ID da Unidade", example = "2")
    private Integer fkUnidade;

    @Schema(description = "ID do Caderno", example = "1")
    private Integer fkCaderno;

    @Schema(description = "Título da tarefa", example = "Comprar materiais")
    private String titulo;

    @Schema(description = "Descrição da tarefa", example = "Comprar materiais para o acampamento")
    private String descricao;

    @Schema(description = "Tipo da tarefa", example = "CLUBE")
    private String tipoTarefa;

    @Schema(description = "Pontuação da tarefa", example = "10")
    private Integer pontuacao;

    @Schema(description = "Prazo padrão da tarefa", example = "2026-04-10")
    private LocalDate prazoPadrao;

    @Schema(description = "Status da tarefa no Kanban", example = "A FAZER")
    private String statusKanban;

    @Schema(description = "ID do vínculo tarefa↔unidade (Unidade_Tarefa)", example = "7")
    private Integer idUnidadeTarefa;

    @Schema(description = "Prazo de entrega da unidade (ou o prazo padrão da tarefa)", example = "2026-04-10")
    private LocalDate prazoEntrega;

    @Schema(description = "Indica se a unidade já enviou evidência para esta tarefa (preenchido no Kanban)", example = "false")
    private Boolean possuiEvidencia;

    @Schema(description = "Nome do caderno (tarefas de caderno)", example = "Amigo")
    private String nomeCaderno;

    @Schema(description = "Unidades com a tarefa no ciclo ativo", example = "4")
    private Integer unidadesTotal;

    @Schema(description = "Unidades que já entregaram (em revisão ou concluída) no ciclo ativo", example = "1")
    private Integer unidadesEntregaram;

    public TarefaResponseDto() {
    }

    public Integer getIdUnidadeTarefa() {
        return idUnidadeTarefa;
    }

    public void setIdUnidadeTarefa(Integer idUnidadeTarefa) {
        this.idUnidadeTarefa = idUnidadeTarefa;
    }

    public LocalDate getPrazoEntrega() {
        return prazoEntrega;
    }

    public void setPrazoEntrega(LocalDate prazoEntrega) {
        this.prazoEntrega = prazoEntrega;
    }

    public Boolean getPossuiEvidencia() {
        return possuiEvidencia;
    }

    public void setPossuiEvidencia(Boolean possuiEvidencia) {
        this.possuiEvidencia = possuiEvidencia;
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

    public String getStatusKanban() {
        return statusKanban;
    }

    public void setStatusKanban(String statusKanban) {
        this.statusKanban = statusKanban;
    }

    public String getNomeCaderno() {
        return nomeCaderno;
    }

    public void setNomeCaderno(String nomeCaderno) {
        this.nomeCaderno = nomeCaderno;
    }

    public Integer getUnidadesTotal() {
        return unidadesTotal;
    }

    public void setUnidadesTotal(Integer unidadesTotal) {
        this.unidadesTotal = unidadesTotal;
    }

    public Integer getUnidadesEntregaram() {
        return unidadesEntregaram;
    }

    public void setUnidadesEntregaram(Integer unidadesEntregaram) {
        this.unidadesEntregaram = unidadesEntregaram;
    }
}
