package school.sptech.APIDesbravadores.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class TarefaStatusUpdateDto {

    @NotBlank
    @Schema(description = "Novo status da tarefa no Kanban", example = "Em andamento")
    private String status;

    public TarefaStatusUpdateDto() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
