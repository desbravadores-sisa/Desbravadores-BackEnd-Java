package school.sptech.APIDesbravadores.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class EvidenciaUpdateDto {

    @NotBlank
    @Schema(description = "Nome da evidência", example = "Foto da atividade atualizada")
    private String nome;

    @NotBlank
    @Schema(description = "URL do arquivo anexado", example = "https://storage.exemplo.com/evidencias/foto-atualizada.jpg")
    private String urlAnexo;

    public EvidenciaUpdateDto() {
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
