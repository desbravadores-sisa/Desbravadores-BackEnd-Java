package school.sptech.APIDesbravadores.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public class EvidenciaResponseDto {

    @Schema(description = "ID da evidência", example = "1")
    private Integer id;

    @Schema(description = "ID do vínculo entre tarefa e unidade", example = "10")
    private Integer idTarefaUnidade;

    @Schema(description = "ID da tarefa", example = "1")
    private Integer idTarefa;

    @Schema(description = "ID da unidade", example = "2")
    private Integer idUnidade;

    @Schema(description = "Nome da evidência", example = "Foto da atividade")
    private String nome;

    @Schema(description = "URL do arquivo anexado", example = "https://storage.exemplo.com/evidencias/foto.jpg")
    private String urlAnexo;

    @Schema(description = "Status da tarefa no Kanban", example = "Em andamento")
    private String statusKanban;

    @Schema(description = "Data de upload da evidência", example = "2026-06-07T14:30:00")
    private LocalDateTime dataUpload;

    public EvidenciaResponseDto() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdTarefaUnidade() {
        return idTarefaUnidade;
    }

    public void setIdTarefaUnidade(Integer idTarefaUnidade) {
        this.idTarefaUnidade = idTarefaUnidade;
    }

    public Integer getIdTarefa() {
        return idTarefa;
    }

    public void setIdTarefa(Integer idTarefa) {
        this.idTarefa = idTarefa;
    }

    public Integer getIdUnidade() {
        return idUnidade;
    }

    public void setIdUnidade(Integer idUnidade) {
        this.idUnidade = idUnidade;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUrlAnexo() {
        return urlAnexo;
    }

    public void setUrlAnexo(String urlAnexo) {
        this.urlAnexo = urlAnexo;
    }

    public String getStatusKanban() {
        return statusKanban;
    }

    public void setStatusKanban(String statusKanban) {
        this.statusKanban = statusKanban;
    }

    public LocalDateTime getDataUpload() {
        return dataUpload;
    }

    public void setDataUpload(LocalDateTime dataUpload) {
        this.dataUpload = dataUpload;
    }
}
