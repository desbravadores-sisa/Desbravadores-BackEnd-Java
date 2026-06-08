package school.sptech.APIDesbravadores.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class EvidenciaCreateDto {

    @NotNull
    @Schema(description = "ID da tarefa vinculada à unidade do conselheiro", example = "1")
    private Integer idTarefa;

    @NotBlank
    @Schema(description = "Nome da evidência", example = "Foto da atividade")
    private String nome;

    @NotBlank
    @Schema(description = "URL do arquivo anexado", example = "https://storage.exemplo.com/evidencias/foto.jpg")
    private String urlAnexo;

    public EvidenciaCreateDto() {
    }

    public Integer getIdTarefa() {
        return idTarefa;
    }

    public void setIdTarefa(Integer idTarefa) {
        this.idTarefa = idTarefa;
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
}
